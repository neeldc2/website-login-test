CREATE TABLE user_profile (
    user_id binary(16) NOT NULL PRIMARY KEY,
    first_name VARCHAR(100),
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    usn VARCHAR(100),
    tenant_id BIGINT,
    year_of_admission BIGINT,
    year_of_passing BIGINT,
    phone_number VARCHAR(20),
    gender VARCHAR(20),
    branch VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX user_profile_tenant_id (tenant_id)
)
