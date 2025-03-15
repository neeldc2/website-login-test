package com.example.website_login_1.dto;

import com.example.website_login_1.entity.TenantUser;
import com.example.website_login_1.entity.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserInfoResponse(
        UUID userId,
        String email,
        String firstName,
        String middleName,
        String lastName,
        boolean active,
        boolean approved,
        Long rejectionCounter
) {
    public static UserInfoResponse getUserInfoResponse(
            final User user,
            final TenantUser tenantUser
    ) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .active(tenantUser.isActive())
                .approved(tenantUser.isApproved())
                .rejectionCounter(tenantUser.getRejectionCounter())
                .build();
    }
}
