package com.example.website_login_1.excel;

import com.example.website_login_1.dto.CreateTenantUserRequest;
import com.example.website_login_1.dto.CreateUserRequest;
import com.example.website_login_1.dto.UpsertUserProfileRequest;
import com.example.website_login_1.repository.RoleRepository;
import com.example.website_login_1.repository.TenantRepository;
import com.example.website_login_1.repository.TenantUserRepository;
import com.example.website_login_1.repository.UserRepository;
import com.example.website_login_1.repository.UserTenantRoleRepository;
import com.example.website_login_1.service.UserService;
import com.example.website_login_1.usercontext.UserContextHolder;
import com.example.website_login_1.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.website_login_1.constant.WebsiteLoginConstants.Roles.STUDENT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserImportService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;
    private final RoleRepository roleRepository;
    private final UserTenantRoleRepository userTenantRoleRepository;

    public void importUsers(final List<UserProfileExcelDto> userProfileExcelDtoList) {
        userProfileExcelDtoList.forEach(this::importUser);
    }

    @Async("threadPoolTaskExecutor")
    public void importUser(UserProfileExcelDto userProfileExcelDto) {
        UUID tenantGuid = UserContextHolder.getUserContext().tenantGuid();

        Instant startTime = Instant.now();
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email(userProfileExcelDto.email())
                .username(userProfileExcelDto.firstName())
                .password(PasswordGenerator.generateSecurePassword(10))
                .roleNames(Set.of(STUDENT))
                .build();
        Instant endTime = Instant.now();
        log.info("Time for create user request {}", ChronoUnit.MILLIS.between(startTime, endTime));

        startTime = Instant.now();
        CreateTenantUserRequest createTenantUserRequest = CreateTenantUserRequest.builder()
                .tenantGuid(tenantGuid)
                .createUserRequest(createUserRequest)
                .build();
        userService.createTenantUser(createTenantUserRequest);
        endTime = Instant.now();
        log.info("Time for create tenant user {}", ChronoUnit.MILLIS.between(startTime, endTime));

        startTime = Instant.now();
        UpsertUserProfileRequest upsertUserProfileRequest = UpsertUserProfileRequest.builder()
                .firstName(userProfileExcelDto.firstName())
                .middleName(userProfileExcelDto.middleName())
                .lastName(userProfileExcelDto.lastName())
                .email(userProfileExcelDto.email())
                .gender(userProfileExcelDto.gender())
                .usn(userProfileExcelDto.usn())
                .yearOfAdmission(userProfileExcelDto.yearOfAdmission())
                .yearOfPassing(userProfileExcelDto.yearOfPassing())
                .phoneNumber(userProfileExcelDto.phoneNumber())
                .branch(userProfileExcelDto.branch())
                .build();
        userService.upsertUserProfile(upsertUserProfileRequest);
        endTime = Instant.now();
        log.info("Time for upsert user {}", ChronoUnit.MILLIS.between(startTime, endTime));

        log.info("user added");
    }
}
