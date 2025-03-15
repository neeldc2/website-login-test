package com.example.website_login_1.repository;

import com.example.website_login_1.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Set<Role> findByNameIn(final Set<String> roleNames);

    Optional<Role> findByName(final String roleName);
}
