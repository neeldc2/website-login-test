package com.example.website_login_1.utils;

import com.example.website_login_1.dto.UserProfileExcelDto;
import io.jsonwebtoken.lang.Strings;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ExcelUtils {

    public List<UserProfileExcelDto> readExcelFile(MultipartFile file) throws IOException {
        List<UserProfileExcelDto> employees = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Skip header row
            int startRow = 1;

            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    UserProfileExcelDto.UserProfileExcelDtoBuilder userProfileDtoBuilder = UserProfileExcelDto.builder();

                    userProfileDtoBuilder.firstName(getStringCellValue(row, 0));
                    userProfileDtoBuilder.middleName(getStringCellValue(row, 1));
                    userProfileDtoBuilder.lastName(getStringCellValue(row, 2));
                    userProfileDtoBuilder.email(getStringCellValue(row, 3));
                    userProfileDtoBuilder.gender(getStringCellValue(row, 4));
                    userProfileDtoBuilder.usn(getStringCellValue(row, 5));
                    userProfileDtoBuilder.yearOfAdmission(getIntegerCellValue(row, 6));
                    userProfileDtoBuilder.yearOfPassing(getIntegerCellValue(row, 7));
                    userProfileDtoBuilder.phoneNumber(getStringCellValue(row, 8));
                    userProfileDtoBuilder.branch(getStringCellValue(row, 9));

                    employees.add(userProfileDtoBuilder.build());
                }
            }
        }

        return employees;
    }

    private static String getStringCellValue(Row row, int columnNumber) {
        if (row == null || row.getCell(columnNumber) == null) {
            return Strings.EMPTY;
        }
        return row.getCell(columnNumber).getStringCellValue();
    }

    private static Integer getIntegerCellValue(Row row, int columnNumber) {
        if (row == null || row.getCell(columnNumber) == null) {
            return null;
        }
        Double value = row.getCell(columnNumber).getNumericCellValue();

        if (value == null) {
            return null;
        }
        return value.intValue();
    }

}
