-- V3: Permission & RBAC tables
CREATE TABLE permissions (
    id           BIGSERIAL PRIMARY KEY,
    resource     VARCHAR(100) NOT NULL,   -- e.g. products, orders, customers
    action       VARCHAR(50) NOT NULL,    -- e.g. create, read, update, delete
    description  VARCHAR(255),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(resource, action)
);

CREATE TABLE permission_groups (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    description  VARCHAR(255),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE permission_group_permissions (
    group_id       BIGINT NOT NULL REFERENCES permission_groups(id) ON DELETE CASCADE,
    permission_id  BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (group_id, permission_id)
);

CREATE TABLE admin_permission_groups (
    admin_id   BIGINT NOT NULL REFERENCES admins(id) ON DELETE CASCADE,
    group_id   BIGINT NOT NULL REFERENCES permission_groups(id) ON DELETE CASCADE,
    PRIMARY KEY (admin_id, group_id)
);

-- Individual permissions override (can add extra or restrict)
CREATE TABLE admin_permissions (
    admin_id      BIGINT NOT NULL REFERENCES admins(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (admin_id, permission_id)
);

-- Seed default permissions
INSERT INTO permissions (resource, action, description) VALUES
    ('products',   'create', 'Create new products'),
    ('products',   'read',   'View products'),
    ('products',   'update', 'Edit products'),
    ('products',   'delete', 'Delete products'),
    ('orders',     'create', 'Create orders'),
    ('orders',     'read',   'View orders'),
    ('orders',     'update', 'Update order status'),
    ('orders',     'delete', 'Cancel/delete orders'),
    ('customers',  'read',   'View customer list'),
    ('customers',  'update', 'Edit customer info'),
    ('customers',  'ban',    'Ban/unban customers'),
    ('admins',     'create', 'Create admin accounts'),
    ('admins',     'read',   'View admin list'),
    ('admins',     'update', 'Edit admin accounts'),
    ('admins',     'delete', 'Delete admin accounts'),
    ('categories', 'create', 'Create categories'),
    ('categories', 'read',   'View categories'),
    ('categories', 'update', 'Edit categories'),
    ('categories', 'delete', 'Delete categories'),
    ('coupons',    'create', 'Create coupons'),
    ('coupons',    'read',   'View coupons'),
    ('coupons',    'update', 'Edit coupons'),
    ('coupons',    'delete', 'Delete coupons'),
    ('reviews',    'read',   'View reviews'),
    ('reviews',    'update', 'Moderate reviews'),
    ('reviews',    'delete', 'Delete reviews'),
    ('i18n',       'read',   'View translations'),
    ('i18n',       'update', 'Edit translations'),
    ('i18n',       'delete', 'Delete translations'),
    ('reports',    'read',   'View reports and analytics');
