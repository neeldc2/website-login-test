package com.example.website_login_1.excel;

import com.example.website_login_1.annotation.ValidatePermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ExcelController {

    private final UserImportService userImportService;

    @ValidatePermission({"MANAGE_USERS"})
    @PostMapping("/import")
    public List<UserProfileExcelDto> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<UserProfileExcelDto> userProfileExcelDtoList = ExcelUtils.readExcelFile(file);
            userProfileExcelDtoList.forEach(userProfileExcelDto -> userImportService.importUser(userProfileExcelDto));
            //userImportService.importUsers(userProfileExcelDtoList);
            return userProfileExcelDtoList;
        } catch (IOException e) {
            throw new RuntimeException("Exception importing users");
        }
    }

    @ValidatePermission({"MANAGE_USERS"})
    @PostMapping("/import-2")
    public List<UserProfileExcelDto> uploadExcel2(@RequestParam("file") MultipartFile file) {
        try {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            List<UserProfileExcelDto> userProfileExcelDtoList = ExcelUtils.readExcelFile(file);
            userProfileExcelDtoList.forEach(userProfileExcelDto -> futures.add(userImportService.importUser2(userProfileExcelDto)));

            // Wait for all tasks to complete
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            // Block until all tasks are complete
            allOf.join();

            log.info("Number of users {}", userImportService.getUserCount());

            return userProfileExcelDtoList;
        } catch (IOException e) {
            throw new RuntimeException("Exception importing users");
        }
    }
}
