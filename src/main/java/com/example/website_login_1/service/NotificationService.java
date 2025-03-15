package com.example.website_login_1.service;

import com.example.website_login_1.dto.messaging.AddTenantEmailPayload;
import com.example.website_login_1.dto.messaging.EmailType;
import com.example.website_login_1.messaging.ActiveMqProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.example.website_login_1.constant.WebsiteLoginConstants.ActiveMqConstants.EMAIL_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final UrlGeneratorService urlGeneratorService;
    private final ActiveMqProducer activeMqProducer;

    public void sendEmailOnAddTenant(
            final String tenantAdminEmail,
            final Long tenantId
    ) {
        final String urlToResetPassword = urlGeneratorService.getUrlForResetPassword(
                tenantAdminEmail,
                tenantId);
        AddTenantEmailPayload addTenantEmailPayload = AddTenantEmailPayload.builder()
                .urlToResetPassword(urlToResetPassword)
                .toEmailIds(Set.of(tenantAdminEmail))
                .ccEmailIds(Set.of())
                .bccEmailIds(Set.of())
                .emailType(EmailType.ADD_TENANT)
                .build();
        activeMqProducer.sendMessage(EMAIL_QUEUE, addTenantEmailPayload);
    }

    public void inviteNewUserToTenant(
            final String email
    ) {
        System.out.println("Send invite email to user");
    }
}
