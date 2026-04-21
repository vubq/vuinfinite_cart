-- V2: Admin tables (completely separate from customer)
CREATE TABLE admins (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(100) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(255),
    is_superadmin   BOOLEAN NOT NULL DEFAULT FALSE,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE, INACTIVE
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Default SUPERADMIN (password: Admin@123 — MUST be changed on first login)
-- BCrypt hash of 'Admin@123' with cost 12
INSERT INTO admins (username, email, password_hash, full_name, is_superadmin)
VALUES (
    'superadmin',
    'superadmin@vuinfinite.local',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBdXIG/6yvbNKm',
    'Super Administrator',
    TRUE
);
