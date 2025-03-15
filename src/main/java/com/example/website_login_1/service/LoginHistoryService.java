package com.example.website_login_1.service;

import com.example.website_login_1.entity.LoginHistory;
import com.example.website_login_1.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public LoginHistory saveLoginHistory(
            final LoginHistory loginHistory
    ) {
        return loginHistoryRepository.save(loginHistory);
    }
}
