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
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guid")
    private UUID guid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_testing_tenant", nullable = false)
    @Builder.Default
    private boolean isTestingTenant = false;

    @Column(name = "database_name", nullable = false)
    private String databaseName;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<TenantUser> tenantUserList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserTenantRole> userTenantRoles;
}
