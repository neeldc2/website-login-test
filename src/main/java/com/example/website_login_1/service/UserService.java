package com.example.website_login_1.service;

import com.example.website_login_1.dto.CreateTenantRequest;
import com.example.website_login_1.dto.CreateTenantResponse;
import com.example.website_login_1.dto.CreateTenantUserRequest;
import com.example.website_login_1.dto.CreateUserRequest;
import com.example.website_login_1.dto.RefreshTokenRequest;
import com.example.website_login_1.dto.RefreshTokenResponse;
import com.example.website_login_1.dto.UpdateTenantRequest;
import com.example.website_login_1.dto.UserLoginRequest;
import com.example.website_login_1.dto.UserLoginResponse;
import com.example.website_login_1.entity.LoginHistory;
import com.example.website_login_1.entity.Permission;
import com.example.website_login_1.entity.Role;
import com.example.website_login_1.entity.RolePermission;
import com.example.website_login_1.entity.Tenant;
import com.example.website_login_1.entity.TenantUser;
import com.example.website_login_1.entity.User;
import com.example.website_login_1.entity.UserTenantRole;
import com.example.website_login_1.exception.WebsiteException;
import com.example.website_login_1.repository.LoginHistoryRepository;
import com.example.website_login_1.repository.RoleRepository;
import com.example.website_login_1.repository.TenantRepository;
import com.example.website_login_1.repository.TenantUserRepository;
import com.example.website_login_1.repository.UserRepository;
import com.example.website_login_1.repository.UserTenantRoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.website_login_1.constant.WebsiteLoginConstants.ADMIN_ROLE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;
    private final RoleRepository roleRepository;
    private final UserTenantRoleRepository userTenantRoleRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void createTenantUser(final CreateTenantUserRequest createTenantUserRequest) {
        Tenant tenant = getValidTenant(createTenantUserRequest.tenantGuid());

        CreateUserRequest createUserRequest = createTenantUserRequest.createUserRequest();
        Set<Role> roles = getRoles(createUserRequest.roleNames());
        final Optional<User> userOptional = getUser(createUserRequest.email());
        final User user = userOptional.orElseGet(() -> createTenantUser(createUserRequest));

        final TenantUser tenantUser = TenantUser.builder()
                .tenant(tenant)
                .user(user)
                // default tenant if user is getting created for the first time
                .defaultTenant(userOptional.isEmpty())
                .build();
        tenantUserRepository.save(tenantUser);

        roles.forEach(role -> {
            final UserTenantRole userRole = UserTenantRole.builder()
                    .role(role)
                    .user(user)
                    .tenant(tenant)
                    .build();
            userTenantRoleRepository.save(userRole);
        });

    }

    public UserLoginResponse userLogin(final UserLoginRequest userLoginRequest) {
        // UsernamePasswordAuthenticationFilter is the default filter
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.email(), userLoginRequest.password()));
        // The above code throws BadCredentialsException if username/password is invalid.
        // Below code will not run if username/password is invalid.
        // So, the below if code is not required
        if (authentication.isAuthenticated()) {
            log.info("Good creds");
        }

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //String jwtToken = jwtService.generateJwtToken(userDetails.getUsername());

        User user = getUser(userLoginRequest.email()).get();
        TenantUser tenantUser = user.getTenantUserList().stream()
                .filter(tenantUserToFilter -> {
                    if (userLoginRequest.tenantId() == null) {
                        return tenantUserToFilter.isDefaultTenant();
                    } else {
                        return tenantUserToFilter.getTenant().getId().equals(userLoginRequest.tenantId());
                    }
                })
                .findFirst()
                .get();
        final String accessToken = getAccessToken(userLoginRequest.email(), tenantUser.getTenant().getId());
        final String refreshToken = jwtService.generateRefreshToken(userLoginRequest.email(), tenantUser.getTenant().getId());

        LoginHistory loginHistory = LoginHistory.builder()
                .userId(user.getId())
                .tenantId(tenantUser.getTenant().getId())
                .email(user.getEmail())
                .ipAddress(userLoginRequest.ipAddress())
                .userAgent(userLoginRequest.userAgent())
                .success(true)
                .loginTimestamp(Instant.now())
                .build();
        loginHistoryRepository.save(loginHistory);

        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void captureFailedUserLoginHistory(final UserLoginRequest userLoginRequest,
                                              final Exception exception) {
        LoginHistory loginHistory = LoginHistory.builder()
                .email(userLoginRequest.email())
                .ipAddress(userLoginRequest.ipAddress())
                .userAgent(userLoginRequest.userAgent())
                .success(false)
                .failureReason(exception.getMessage())
                .loginTimestamp(Instant.now())
                .build();
        loginHistoryRepository.save(loginHistory);
    }

    public RefreshTokenResponse refreshToken(
            final RefreshTokenRequest refreshTokenRequest) {
        final String email = jwtService.getSubject(refreshTokenRequest.refreshToken());
        final Long tenantId = jwtService.getTenantId(refreshTokenRequest.refreshToken());
        final String accessToken = getAccessToken(email, tenantId);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public CreateTenantResponse registerTenant(final CreateTenantRequest createTenantRequest) {
        validateTenantRequest(createTenantRequest);

        // Create Tenant
        final Tenant tenant = Tenant.builder()
                .guid(UUID.randomUUID())
                .name(createTenantRequest.tenantName())
                .databaseName(createTenantRequest.tenantName())
                .build();
        tenantRepository.save(tenant);

        // Create first user in that tenant
        final CreateTenantUserRequest createTenantUserRequest = CreateTenantUserRequest.builder()
                .tenantGuid(tenant.getGuid())
                .createUserRequest(createTenantRequest.createUserRequest())
                .build();
        createTenantUser(createTenantUserRequest);

        return CreateTenantResponse.builder()
                .tenantId(tenant.getId())
                .tenantGuid(tenant.getGuid())
                .build();
    }

    public void updateTenant(final UpdateTenantRequest updateTenantRequest) {
        final Tenant tenant = getTenant(updateTenantRequest.tenantGuid());

        final boolean currentStatus = tenant.isEnabled();
        final boolean newStatus = updateTenantRequest.enable();
        if (currentStatus == newStatus) {
            return;
        }

        tenant.setEnabled(newStatus);
        tenantRepository.save(tenant);
    }

    private String getAccessToken(
            @NonNull final String email,
            @NonNull final Long tenantId
    ) {
        User user = getUser(email).get();
        TenantUser tenantUser = user.getTenantUserList().stream()
                .filter(tenantUserToFilter -> tenantUserToFilter.getTenant().getId().equals(tenantId))
                .findFirst()
                .get();
        Tenant tenant = tenantUser.getTenant();

        Set<String> permissions = user.getUserTenantRoles().stream()
                .filter(userTenantRole -> userTenantRole.getTenant().getGuid().equals(tenant.getGuid()))
                .map(UserTenantRole::getRole)
                .map(Role::getRolePermissionList)
                .flatMap(List::stream)
                .map(RolePermission::getPermission)
                .map(Permission::getName)
                .collect(Collectors.toSet());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("tenantId", tenant.getId());
        claims.put("tenantGuid", tenant.getGuid());
        claims.put("tenant", tenant.getName());
        claims.put("permissions", permissions);

        return jwtService.generateJwtToken(email, claims);
    }

    private User createTenantUser(CreateUserRequest createUserRequest) {
        final User newUser = User.builder()
                .email(createUserRequest.email())
                .enabled(true)
                .username(createUserRequest.username())
                // TODO: add salt
                .password(encoder.encode(createUserRequest.password()))
                .build();
        return userRepository.save(newUser);
    }

    private Tenant getTenant(final UUID tenantGuid) {
        final Tenant tenant = tenantRepository.findByGuid(tenantGuid)
                .orElseThrow(() -> new RuntimeException("Tenant Guid is invalid"));
        return tenant;
    }

    private Tenant getValidTenant(final UUID tenantGuid) {
        final Tenant tenant = tenantRepository.findByGuid(tenantGuid)
                .orElseThrow(() -> new RuntimeException("Tenant Guid is invalid"));

        if (!tenant.isEnabled()) {
            throw new WebsiteException("Tenant is disabled");
        }
        return tenant;
    }

    private Set<Role> getRoles(final Set<String> roleNames) {
        Set<Role> roles = roleRepository.findByNameIn(roleNames);

        if (CollectionUtils.isEmpty(roles) || roles.size() != roleNames.size()) {
            throw new WebsiteException("Invalid Roles");
        }

        return Collections.unmodifiableSet(roles);
    }

    private void validateTenantRequest(final CreateTenantRequest createTenantRequest) {
        final Tenant tenant = tenantRepository.findByName(createTenantRequest.tenantName());

        if (tenant != null) {
            throw new WebsiteException("Tenant exists already");
        }

        CreateUserRequest createUserRequest = createTenantRequest.createUserRequest();

        if (!createUserRequest.roleNames().contains(ADMIN_ROLE_NAME)) {
            throw new WebsiteException("Tenant User is not admin");
        }
    }

    private Optional<User> getUser(String email) {
        return userRepository.findByEmail(email);
    }

}
