package com.example.website_login_1.repository;

import com.example.website_login_1.entity.AdminAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminActionRepository extends JpaRepository<AdminAction, UUID> {
}
