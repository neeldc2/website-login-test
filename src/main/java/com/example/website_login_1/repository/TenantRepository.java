package com.example.website_login_1.repository;

import com.example.website_login_1.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByGuid(UUID guid);

    Tenant findByName(String tenantName);
}
