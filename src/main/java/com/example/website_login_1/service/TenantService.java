package com.example.website_login_1.service;

import com.example.website_login_1.dto.TenantInfoResponse;
import com.example.website_login_1.entity.Tenant;
import com.example.website_login_1.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public Set<TenantInfoResponse> getRegisteredTenants() {
        return tenantRepository.findAll().stream()
                .filter(Predicate.not(Tenant::isTestingTenant))
                .filter(Tenant::isEnabled)
                .map(TenantInfoResponse::getTenantInfoResponse)
                .collect(Collectors.toSet());
    }
}
