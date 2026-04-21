CREATE TABLE customers (
    id           BIGSERIAL PRIMARY KEY,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    name         VARCHAR(255),
    avatar_url   TEXT,
    phone        VARCHAR(20),
    status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, BANNED, UNVERIFIED
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE customer_oauth (
    id             BIGSERIAL PRIMARY KEY,
    customer_id    BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    provider       VARCHAR(50) NOT NULL,   -- google, facebook
    provider_id    VARCHAR(255) NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(provider, provider_id)
);

CREATE TABLE customer_addresses (
    id            BIGSERIAL PRIMARY KEY,
    customer_id   BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    label         VARCHAR(100),            -- Home, Office...
    recipient     VARCHAR(255) NOT NULL,
    phone         VARCHAR(20) NOT NULL,
    province      VARCHAR(100) NOT NULL,
    district      VARCHAR(100) NOT NULL,
    ward          VARCHAR(100) NOT NULL,
    street        VARCHAR(255) NOT NULL,
    is_default    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customer_oauth_customer ON customer_oauth(customer_id);
CREATE INDEX idx_customer_addresses_customer ON customer_addresses(customer_id);
