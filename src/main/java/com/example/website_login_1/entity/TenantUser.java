package com.example.website_login_1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tenant_users")
public class TenantUser {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "approved", nullable = false)
    private boolean approved;

    @Column(name = "rejected", nullable = false)
    private boolean rejected;

    @Column(name = "rejection_counter", nullable = false)
    private Long rejectionCounter;

    @Column(name = "default_tenant", nullable = false)
    @Builder.Default
    private boolean defaultTenant = false;

    public void resetRejectionCounter() {
        this.rejectionCounter = 0L;
    }

    public void increaseRejectionCounter() {
        this.rejectionCounter = this.rejectionCounter + 1;
    }

}
