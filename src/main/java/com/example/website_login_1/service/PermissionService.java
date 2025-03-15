package com.example.website_login_1.service;

import com.example.website_login_1.usercontext.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {

    public Set<String> getPermissions() {
        return UserContextHolder.getUserContext().permissions();
    }
}
