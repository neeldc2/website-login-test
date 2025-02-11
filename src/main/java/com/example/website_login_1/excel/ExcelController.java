package com.example.website_login_1.excel;

import com.example.website_login_1.annotation.ValidatePermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
}
