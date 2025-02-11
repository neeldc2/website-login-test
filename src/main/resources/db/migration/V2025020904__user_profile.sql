CREATE TABLE user_profile (
    id binary(16) NOT NULL PRIMARY KEY,
    user_id binary(16) NOT NULL,
    tenant_id BIGINT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    usn VARCHAR(100),
    year_of_admission BIGINT,
    year_of_passing BIGINT,
    phone_number VARCHAR(20),
    gender VARCHAR(20),
    branch VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    UNIQUE KEY user_profile_tenant_id_user_id (user_id, tenant_id)
)
