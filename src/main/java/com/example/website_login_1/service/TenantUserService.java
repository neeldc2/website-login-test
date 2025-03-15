package com.example.website_login_1.service;

import com.example.website_login_1.entity.TenantUser;
import com.example.website_login_1.entity.User;
import com.example.website_login_1.exception.WebsiteException;
import com.example.website_login_1.repository.TenantUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TenantUserService {

    private final TenantUserRepository tenantUserRepository;

    public void approveTenantUsers(
            final List<TenantUser> tenantUserList
    ) {
        tenantUserList.forEach(tenantUser -> tenantUser.setApproved(true));
        tenantUserList.forEach(tenantUser -> tenantUser.setRejected(false));
        tenantUserList.forEach(TenantUser::resetRejectionCounter);
        tenantUserRepository.saveAll(tenantUserList);
    }

    public void rejectTenantUsers(
            final List<TenantUser> tenantUserList) {
        tenantUserList.forEach(tenantUser -> tenantUser.setApproved(false));
        tenantUserList.forEach(tenantUser -> tenantUser.setRejected(true));
        tenantUserList.forEach(TenantUser::increaseRejectionCounter);
        tenantUserRepository.saveAll(tenantUserList);
    }

    public void requestAdminApproval(
            final TenantUser tenantUser
    ) {
        tenantUser.setApproved(false);
        tenantUser.setRejected(false);
        tenantUser.increaseRejectionCounter();
        tenantUserRepository.save(tenantUser);
    }

    public void activateTenantUser(
            final List<TenantUser> tenantUserList
    ) {
        tenantUserList.forEach(tenantUser -> tenantUser.setActive(true));
        tenantUserRepository.saveAll(tenantUserList);
    }

    public static TenantUser getValidTenantUser(Long tenantId, User user) {
        return getTenantUser(tenantId, user)
                .orElseThrow(() -> new WebsiteException("Invalid Tenant"));
    }

    public static Optional<TenantUser> getTenantUser(
            final Long tenantId,
            final User user) {
        return user.getTenantUserList().stream()
                .filter(tenantUserToFilter -> {
                    if (tenantId == null) {
                        return tenantUserToFilter.isDefaultTenant();
                    } else {
                        return tenantUserToFilter.getTenant().getId().equals(tenantId);
                    }
                })
                .findFirst();
    }

}
