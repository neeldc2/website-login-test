package com.example.website_login_1.service;

import com.example.website_login_1.dto.CreateTenantRequest;
import com.example.website_login_1.dto.CreateTenantResponse;
import com.example.website_login_1.dto.CreateTenantUserRequest;
import com.example.website_login_1.dto.CreateUserRequest;
import com.example.website_login_1.dto.RefreshTokenRequest;
import com.example.website_login_1.dto.RefreshTokenResponse;
import com.example.website_login_1.dto.ResetPasswordRequest;
import com.example.website_login_1.dto.UpdateTenantRequest;
import com.example.website_login_1.dto.UpsertUserProfileRequest;
import com.example.website_login_1.dto.UserLoginRequest;
import com.example.website_login_1.dto.UserLoginResponse;
import com.example.website_login_1.entity.LoginHistory;
import com.example.website_login_1.entity.Permission;
import com.example.website_login_1.entity.Role;
import com.example.website_login_1.entity.RolePermission;
import com.example.website_login_1.entity.Tenant;
import com.example.website_login_1.entity.TenantUser;
import com.example.website_login_1.entity.User;
import com.example.website_login_1.entity.UserProfile;
import com.example.website_login_1.entity.UserTenantRole;
import com.example.website_login_1.exception.WebsiteException;
import com.example.website_login_1.repository.LoginHistoryRepository;
import com.example.website_login_1.repository.RoleRepository;
import com.example.website_login_1.repository.TenantRepository;
import com.example.website_login_1.repository.TenantUserRepository;
import com.example.website_login_1.repository.UserProfileRepository;
import com.example.website_login_1.repository.UserRepository;
import com.example.website_login_1.repository.UserTenantRoleRepository;
import com.example.website_login_1.usercontext.UserContextHolder;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.website_login_1.constant.WebsiteLoginConstants.ADMIN_ROLE_NAME;
import static com.example.website_login_1.constant.WebsiteLoginConstants.Permissions.MANAGE_USERS;

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
    private final UserProfileRepository userProfileRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final NotificationService notificationService;

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
        //tenantUserRepository.save(tenantUser);
        user.addTenantUsers(tenantUser);

        List<UserTenantRole> userTenantRoleList = new ArrayList<>();
        roles.forEach(role -> {
            final UserTenantRole userTenantRole = UserTenantRole.builder()
                    .role(role)
                    .user(user)
                    .tenant(tenant)
                    .build();
            //userTenantRoleRepository.save(userRole);
            userTenantRoleList.add(userTenantRole);
        });
        user.addUserTenantRoles(userTenantRoleList);

        userRepository.save(user);
    }

    public boolean doesUserExists(
            @NonNull final String email) {
        return userRepository.existsByEmail(email);
    }

    public UserLoginResponse userLoginViaUsernamePassword(final UserLoginRequest userLoginRequest,
                                                          final String userAgent,
                                                          final String ipAddress) {
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

        return performUserLogin(
                userLoginRequest.email(),
                userLoginRequest.tenantId(),
                userAgent,
                ipAddress);
    }

    public UserLoginResponse performUserLogin(
            @NonNull final String email,
            final Long tenantId,
            @NonNull final String userAgent,
            @NonNull final String ipAddress) {
        final User user = getValidUser(email);
        final TenantUser tenantUser = getValidTenantUser(tenantId, user);
        final String accessToken = getAccessToken(email, tenantUser.getTenant().getId());
        final String refreshToken = jwtService.generateRefreshToken(email, tenantUser.getTenant().getId());

        LoginHistory loginHistory = LoginHistory.builder()
                .userId(user.getId())
                .tenantId(tenantUser.getTenant().getId())
                .email(user.getEmail())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
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
                                              final String userAgent,
                                              final String ipAddress,
                                              final Exception exception) {
        LoginHistory loginHistory = LoginHistory.builder()
                .email(userLoginRequest.email())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
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
                .databaseName(createTenantRequest.tenantCode())
                .build();
        tenantRepository.save(tenant);

        // Create first user in that tenant
        final CreateTenantUserRequest createTenantUserRequest = CreateTenantUserRequest.builder()
                .tenantGuid(tenant.getGuid())
                .createUserRequest(createTenantRequest.createUserRequest())
                .build();
        createTenantUser(createTenantUserRequest);

        CreateTenantResponse createTenantResponse = CreateTenantResponse.builder()
                .tenantId(tenant.getId())
                .tenantGuid(tenant.getGuid())
                .build();

        notificationService.sendEmailOnAddTenant(
                createTenantUserRequest.createUserRequest().email(),
                tenant.getId()
        );

        return createTenantResponse;
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

    public void upsertUserProfile(final UpsertUserProfileRequest upsertUserProfileRequest) {
        User user = getUser(upsertUserProfileRequest.email())
                .orElseThrow(() -> new WebsiteException("User does not exists"));

        UUID userIdFromContext = UserContextHolder.getUserContext().userId();
        Set<String> userPermissions = UserContextHolder.getUserContext().permissions();
        UUID userId = user.getId();

        // Users can update only their own profile
        // Only admins can update other user profiles
        if (!userId.equals(userIdFromContext) ||
                !userPermissions.contains(MANAGE_USERS)) {
            throw new WebsiteException("User does not have permission to update profile");
        }

        Long tenantId = UserContextHolder.getUserContext().tenantId();
        Optional<UserProfile> userProfileOptional = getUserProfile(userId, tenantId);
        UserProfile userProfile;

        if (userProfileOptional.isEmpty()) {
            userProfile = new UserProfile();
            userProfile.setUserId(userId);
            userProfile.setTenantId(tenantId);
        } else {
            userProfile = userProfileOptional.get();
        }

        userProfile.setFirstName(upsertUserProfileRequest.firstName());
        userProfile.setMiddleName(upsertUserProfileRequest.middleName());
        userProfile.setLastName(upsertUserProfileRequest.lastName());
        userProfile.setGender(upsertUserProfileRequest.gender());
        userProfile.setUsn(upsertUserProfileRequest.usn());
        userProfile.setYearOfAdmission(upsertUserProfileRequest.yearOfAdmission());
        userProfile.setYearOfPassing(upsertUserProfileRequest.yearOfPassing());
        userProfile.setPhoneNumber(upsertUserProfileRequest.phoneNumber());
        userProfile.setGender(upsertUserProfileRequest.gender());
        userProfile.setBranch(upsertUserProfileRequest.branch());

        userProfileRepository.save(userProfile);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            if (user.getEmail().contains("neel") || user.getEmail().contains("prem")) {
                return;
            }
            userRepository.delete(user);
        });
    }

    public void sendResetPasswordEmail(
            final String email) {
        final User user = getValidUser(email);
        final TenantUser tenantUser = getValidTenantUser(null, user);

        notificationService.sendEmailOnAddTenant(
                email,
                tenantUser.getTenant().getId()
        );
    }

    public void resetPassword(final ResetPasswordRequest resetPasswordRequest) {
        final String refreshToken = resetPasswordRequest.refreshToken();
        final String email = jwtService.getSubject(refreshToken);
        final Long tenantId = jwtService.getTenantId(refreshToken);

        final User user = getValidUser(email);
        final TenantUser tenantUser = getValidTenantUser(tenantId, user);

        user.setPassword(encoder.encode(resetPasswordRequest.password()));
        userRepository.save(user);
    }

    public boolean doesUserExistsWithSameTenant(
            final String email
    ) {
        Optional<User> userOptional = getUser(email);
        if (userOptional.isEmpty()) {
            return false;
        }

        Long tenantId = UserContextHolder.getUserContext().tenantId();
        if (getTenantUser(tenantId, userOptional.get()).isEmpty()) {
            return false;
        }
        return true;
    }

    private static TenantUser getValidTenantUser(Long tenantId, User user) {
        return getTenantUser(tenantId, user)
                .orElseThrow(() -> new WebsiteException("Invalid Tenant"));
    }

    private User getValidUser(String email) {
        return getUser(email)
                .orElseThrow(() -> new WebsiteException("Invalid Email"));
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
        claims.put("tenant", tenant.getDatabaseName());
        claims.put("permissions", permissions);

        return jwtService.generateJwtToken(email, claims);
    }

    private User createTenantUser(CreateUserRequest createUserRequest) {
        final User newUser = User.builder()
                .email(createUserRequest.email())
                .enabled(true)
                .username(createUserRequest.username())
                // BCryptPasswordEncoder uses salt by default
                .password(encoder.encode(createUserRequest.password()))
                .build();
        //return userRepository.save(newUser);
        return newUser;
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

    private Optional<UserProfile> getUserProfile(UUID userId, Long tenantId) {
        return userProfileRepository.findByUserIdAndTenantId(userId, tenantId);
    }

    private static Optional<TenantUser> getTenantUser(
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
