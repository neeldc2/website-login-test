package com.example.website_login_1.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserTenantRole> userTenantRoles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<TenantUser> tenantUserList;

    public void addTenantUsers(@NonNull final TenantUser tenantUser) {
        if (CollectionUtils.isEmpty(getTenantUserList())) {
            setTenantUserList(new ArrayList<>());
        }
        getTenantUserList().add(tenantUser);
    }

    public void addUserTenantRoles(@NonNull final List<UserTenantRole> userTenantRoles) {
        if (CollectionUtils.isEmpty(this.getUserTenantRoles())) {
            setUserTenantRoles(new ArrayList<>());
        }
        getUserTenantRoles().addAll(userTenantRoles);
    }
}
