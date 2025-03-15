package com.example.website_login_1.service;

import com.example.website_login_1.entity.AdminAction;
import com.example.website_login_1.enums.AdminActionType;
import com.example.website_login_1.repository.AdminActionRepository;
import com.example.website_login_1.usercontext.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminActionService {

    private final AdminActionRepository adminActionRepository;

    public void addAdminAction(
            final AdminActionType adminActionType,
            final String actionValues,
            final String ipAddress,
            final String userAgent
    ) {
        AdminAction adminAction = AdminAction.builder()
                .userId(UserContextHolder.getUserContext().userId())
                .tenantId(UserContextHolder.getUserContext().tenantId())
                .action(adminActionType)
                .actionValues(actionValues)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        adminActionRepository.save(adminAction);
    }
}
