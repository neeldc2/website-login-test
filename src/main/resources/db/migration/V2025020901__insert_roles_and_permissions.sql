-- Insert roles
INSERT INTO roles (id, name)
VALUES
    (UUID_TO_BIN(UUID()), 'ADMIN'),
    (UUID_TO_BIN(UUID()), 'MODERATOR'),
    (UUID_TO_BIN(UUID()), 'STUDENT'),
    (UUID_TO_BIN(UUID()), 'USER');

-- Insert permissions
INSERT INTO permissions (id, name)
VALUES
    (UUID_TO_BIN(UUID()), 'MANAGE_TENANT'),
    (UUID_TO_BIN(UUID()), 'ADD_ADMIN'),
    (UUID_TO_BIN(UUID()), 'CREATE_POST'),
    (UUID_TO_BIN(UUID()), 'DELETE_POST'),
    (UUID_TO_BIN(UUID()), 'MANAGE_USERS'),
    (UUID_TO_BIN(UUID()), 'APPROVED_USER'),
    (UUID_TO_BIN(UUID()), 'EDIT_PROFILE');

-- For role_permissions, we need to first get the IDs we want to reference
-- Assuming we want to link:
-- ADMIN -> all permissions
-- MODERATOR -> CREATE_POST, DELETE_POST
-- STUDENT -> CREATE_POST

-- First, let's create variables to store our IDs
SET @admin_id = (SELECT id FROM roles WHERE name = 'ADMIN');
SET @mod_id = (SELECT id FROM roles WHERE name = 'MODERATOR');
SET @student_id = (SELECT id FROM roles WHERE name = 'STUDENT');
SET @user_id = (SELECT id FROM roles WHERE name = 'USER');

-- never add "MANAGE_TENANT" permission here. It should be never assigned to an role.
SET @add_admin_id = (SELECT id FROM permissions WHERE name = 'ADD_ADMIN');
SET @create_post_id = (SELECT id FROM permissions WHERE name = 'CREATE_POST');
SET @delete_post_id = (SELECT id FROM permissions WHERE name = 'DELETE_POST');
SET @manage_users_id = (SELECT id FROM permissions WHERE name = 'MANAGE_USERS');
SET @edit_profile_id = (SELECT id FROM permissions WHERE name = 'EDIT_PROFILE');
SET @approved_user_id = (SELECT id FROM permissions WHERE name = 'APPROVED_USER');

-- Now insert into role_permissions
INSERT INTO role_permissions (id, role_id, permission_id)
VALUES
    -- add all permissions to admin
    (UUID_TO_BIN(UUID()), @admin_id, @add_admin_id),
    (UUID_TO_BIN(UUID()), @admin_id, @create_post_id),
    (UUID_TO_BIN(UUID()), @admin_id, @delete_post_id),
    (UUID_TO_BIN(UUID()), @admin_id, @manage_users_id),
    (UUID_TO_BIN(UUID()), @admin_id, @edit_profile_id),
    (UUID_TO_BIN(UUID()), @admin_id, @approved_user_id),

    -- moderator permissions
    (UUID_TO_BIN(UUID()), @mod_id, @create_post_id),
    (UUID_TO_BIN(UUID()), @mod_id, @delete_post_id),
    (UUID_TO_BIN(UUID()), @mod_id, @approved_user_id),

    -- student permissions
    (UUID_TO_BIN(UUID()), @student_id, @create_post_id),
    (UUID_TO_BIN(UUID()), @student_id, @approved_user_id),
    (UUID_TO_BIN(UUID()), @student_id, @edit_profile_id),

    -- user permissions
    (UUID_TO_BIN(UUID()), @user_id, @edit_profile_id);
