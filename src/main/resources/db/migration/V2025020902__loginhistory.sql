CREATE TABLE login_history (
    id BIGINT AUTO_INCREMENT,
    user_id binary(16) NULL,
    email VARCHAR(100) NOT NULL,
    tenant_id BIGINT,
    success BOOLEAN,
    login_type VARCHAR(50) NOT NULL,
    failure_reason TEXT,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255) NOT NULL,
    -- There is a difference in DATETIME and TIMESTAMP
    login_timestamp DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX login_history_tenant_id (tenant_id),
    INDEX login_history_user_id (user_id),
    -- The PRIMARY KEY includes both id and login_timestamp as required by MySQL's partitioning rules
    PRIMARY KEY (id, login_timestamp)
)
PARTITION BY RANGE (TO_DAYS(login_timestamp)) (
    PARTITION p_2025_01 VALUES LESS THAN (TO_DAYS('2025-02-01')),
    PARTITION p_2025_02 VALUES LESS THAN (TO_DAYS('2025-03-01')),
    PARTITION p_2025_03 VALUES LESS THAN (TO_DAYS('2025-04-01')),
    PARTITION p_2025_04 VALUES LESS THAN (TO_DAYS('2025-05-01')),
    PARTITION p_2025_05 VALUES LESS THAN (TO_DAYS('2025-06-01')),
    PARTITION p_2025_06 VALUES LESS THAN (TO_DAYS('2025-07-01')),
    PARTITION p_2025_07 VALUES LESS THAN (TO_DAYS('2025-08-01')),
    PARTITION p_2025_08 VALUES LESS THAN (TO_DAYS('2025-09-01')),
    PARTITION p_2025_09 VALUES LESS THAN (TO_DAYS('2025-10-01')),
    PARTITION p_2025_10 VALUES LESS THAN (TO_DAYS('2025-11-01')),
    PARTITION p_2025_11 VALUES LESS THAN (TO_DAYS('2025-12-01')),
    PARTITION p_2025_12 VALUES LESS THAN (TO_DAYS('2026-01-01')),
    PARTITION p_max VALUES LESS THAN MAXVALUE
);

-- partitions will help with queries like
-- SELECT * FROM login_history
-- WHERE login_timestamp >= '2025-01-01'
-- AND login_timestamp < '2025-02-01'
-- AND login_status = 'failed';
