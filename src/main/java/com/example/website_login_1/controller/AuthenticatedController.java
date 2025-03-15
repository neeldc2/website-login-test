package com.example.website_login_1.controller;

import com.example.website_login_1.annotation.ValidatePermission;
import com.example.website_login_1.dto.ApproveUserRequest;
import com.example.website_login_1.dto.CreateTenantRequest;
import com.example.website_login_1.dto.CreateTenantResponse;
import com.example.website_login_1.dto.RejectUserRequest;
import com.example.website_login_1.dto.UpdateTenantRequest;
import com.example.website_login_1.dto.UpsertUserProfileRequest;
import com.example.website_login_1.dto.UserInfoResponse;
import com.example.website_login_1.enums.UserStatus;
import com.example.website_login_1.exception.WebsiteException;
import com.example.website_login_1.service.NotificationService;
import com.example.website_login_1.service.PermissionService;
import com.example.website_login_1.service.UserService;
import com.example.website_login_1.usercontext.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.website_login_1.constant.WebsiteLoginConstants.Permissions.ADD_ADMIN;
import static com.example.website_login_1.constant.WebsiteLoginConstants.Permissions.APPROVED_USER;
import static com.example.website_login_1.constant.WebsiteLoginConstants.Permissions.EDIT_PROFILE;
import static com.example.website_login_1.constant.WebsiteLoginConstants.Permissions.MANAGE_TENANT;
import static com.example.website_login_1.constant.WebsiteLoginConstants.Permissions.MANAGE_USERS;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthenticatedController {

    private final UserService userService;
    private final PermissionService permissionService;
    private final NotificationService notificationService;

    // This is also for basic auth
    // This is to test APIs using JWT
    @GetMapping("/test")
    @ValidatePermission({"MANAGE_USERS"})
    public String testAPI() {
        log.info("User is {}", UserContextHolder.getUserContext().userId());
        return "works";
    }

    /**
     * Create first tenant via SQL script. Add yourself as a user with MANAGE_TENANT permission.
     * Script is present in add_super_user.sql file
     * Then, you would be able to add tenants.
     *
     * @param createTenantRequest
     * @return
     */
    //@ValidatePermission({"MANAGE_TENANT"})
    @PostMapping("/tenants")
    public CreateTenantResponse registerTenant(@RequestBody CreateTenantRequest createTenantRequest) {
        return userService.registerTenant(createTenantRequest);
    }

    @ValidatePermission({MANAGE_TENANT})
    @PutMapping("/tenants")
    public void updateTenant(@RequestBody UpdateTenantRequest updateTenantRequest) {
        userService.updateTenant(updateTenantRequest);
    }

    @ValidatePermission({EDIT_PROFILE})
    @PutMapping("/user-profile")
    public void updateUserProfile(@RequestBody UpsertUserProfileRequest upsertUserProfileRequest) {
        userService.upsertUserProfile(upsertUserProfileRequest);
    }

    // TODO: order by name. Add pagination
    @ValidatePermission({MANAGE_USERS})
    @GetMapping("/users")
    public List<UserInfoResponse> getAllUsersInTenant(
            @RequestParam UserStatus userStatus
    ) {
        return userService.getAllUsersResponseInTenant(userStatus);
    }

    @ValidatePermission({MANAGE_USERS})
    @PostMapping("/users/approve")
    public void approveUsers(@RequestBody List<ApproveUserRequest> approveUserRequestList,
                             @RequestHeader("Ip-Address") String ipAddress,
                             @RequestHeader("User-Agent") String userAgent
    ) {
        userService.approveUsers(approveUserRequestList, ipAddress, userAgent);
    }

    @ValidatePermission({MANAGE_USERS})
    @PostMapping("/users/reject")
    public void rejectUsers(@RequestBody List<RejectUserRequest> rejectUserRequestList,
                            @RequestHeader("Ip-Address") String ipAddress,
                            @RequestHeader("User-Agent") String userAgent
    ) {
        userService.rejectUsers(rejectUserRequestList, ipAddress, userAgent);
    }

    @ValidatePermission({ADD_ADMIN})
    @PostMapping("/users/admin")
    public void addAdminUser(@RequestParam UUID newAdminUserId,
                             @RequestHeader("Ip-Address") String ipAddress,
                             @RequestHeader("User-Agent") String userAgent
    ) {
        userService.addAdmin(newAdminUserId, ipAddress, userAgent);
    }

    @ValidatePermission({EDIT_PROFILE})
    @PostMapping("/users/request-approval")
    public void requestApproval() {
        userService.requestApproval();
    }

    @GetMapping("/permissions")
    public Set<String> getPermission() {
        return permissionService.getPermissions();
    }

    @ValidatePermission({MANAGE_USERS})
    @DeleteMapping("/users")
    public void deleteAllUsers() {
        userService.deleteUsers();
    }

    @ValidatePermission({APPROVED_USER})
    @PostMapping("/users/invite")
    public void inviteUser(
            @RequestParam String email
    ) {
        if (userService.doesUserExistsWithSameTenant(email)) {
            throw new WebsiteException("User Already exists within same tenant");
        }
        notificationService.inviteNewUserToTenant(email);
    }

}
