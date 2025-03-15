package com.example.website_login_1.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record UserProfileExcelDto(
        @NonNull String firstName,
        String middleName,
        String lastName,
        @NonNull String email,
        String gender,
        String usn,
        Integer yearOfAdmission,
        Integer yearOfPassing,
        String phoneNumber,
        String branch
) {
}
