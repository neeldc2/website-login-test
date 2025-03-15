CREATE TABLE admin_actions (
    id binary(16) PRIMARY KEY,
    user_id binary(16) NOT NULL,
    tenant_id BIGINT NOT NULL,
    action VARCHAR(45) NOT NULL,
    action_values TEXT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX admin_actions_user_id (user_id),
    INDEX admin_actions_tenant_id (tenant_id)
)