package com.example.website_login_1.dto.messaging;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@ToString
@NoArgsConstructor
public class AddTenantEmailPayload extends EmailPayload {
    private String urlToResetPassword;
}
