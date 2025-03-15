package com.example.website_login_1.service;

import com.example.website_login_1.entity.Role;
import com.example.website_login_1.entity.Tenant;
import com.example.website_login_1.entity.User;
import com.example.website_login_1.entity.UserTenantRole;
import com.example.website_login_1.exception.WebsiteException;
import com.example.website_login_1.repository.RoleRepository;
import com.example.website_login_1.repository.UserTenantRoleRepository;
import com.example.website_login_1.usercontext.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserTenantRoleRepository userTenantRoleRepository;

    public Set<Role> getRoles(final Set<String> roleNames) {
        Set<Role> roles = roleRepository.findByNameIn(roleNames);

        if (CollectionUtils.isEmpty(roles) || roles.size() != roleNames.size()) {
            throw new WebsiteException("Invalid Roles");
        }

        return Collections.unmodifiableSet(roles);
    }

    public void updateUserRole(
            final User user,
            final String roleName
    ) {
        final Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Invalid Role Name"));

        UUID tenantGuid = UserContextHolder.getUserContext().tenantGuid();
        Tenant tenant = user.getTenantUserList().stream()
                .map(tenantUser -> tenantUser.getTenant())
                .filter(t -> t.getGuid().equals(tenantGuid))
                .findFirst().get();

        List<UserTenantRole> userTenantRoleList = user.getUserTenantRoles().stream()
                .filter(userTenantRole -> userTenantRole.getTenant().getGuid().equals(tenantGuid))
                .toList();
        userTenantRoleRepository.deleteAll(userTenantRoleList);

        UserTenantRole userTenantRole = UserTenantRole.builder()
                .user(user)
                .tenant(tenant)
                .role(role)
                .build();
        userTenantRoleRepository.save(userTenantRole);
    }
}
