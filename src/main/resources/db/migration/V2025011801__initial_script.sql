CREATE TABLE users (
    id binary(16) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE roles (
    id binary(16) PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE permissions (
    id binary(16) PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE tenants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guid binary(16) default (uuid_to_bin(uuid())) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    database_name VARCHAR(100) NOT NULL UNIQUE,
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE user_tenant_roles (
    id binary(16) PRIMARY KEY,
    user_id binary(16) NOT NULL,
    tenant_id binary(16) NOT NULL,
    role_id binary(16) NOT NULL,
    UNIQUE KEY (user_id, role_id, tenant_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants(guid) ON DELETE CASCADE
);

CREATE TABLE role_permissions (
    id binary(16) PRIMARY KEY,
    role_id binary(16) NOT NULL,
    permission_id binary(16) NOT NULL,
    UNIQUE KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE tenant_users (
    id binary(16) PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id binary(16) NOT NULL,
    default_tenant BOOLEAN DEFAULT FALSE,
    UNIQUE KEY (tenant_id, user_id),
    -- this key ensures that only one tenant is default for a user
    UNIQUE KEY (user_id, default_tenant),
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
