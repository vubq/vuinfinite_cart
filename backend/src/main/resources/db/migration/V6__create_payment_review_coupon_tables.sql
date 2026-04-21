-- V6: Payment, Review & Coupon tables
CREATE TABLE payments (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT NOT NULL REFERENCES orders(id),
    provider        VARCHAR(50) NOT NULL DEFAULT 'COD',  -- COD, VNPAY, STRIPE, MOMO
    amount          NUMERIC(15, 2) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    -- PENDING, SUCCESS, FAILED, REFUNDED
    transaction_id  VARCHAR(255),    -- Provider's transaction reference
    meta            JSONB,           -- Provider-specific metadata
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE reviews (
    id           BIGSERIAL PRIMARY KEY,
    customer_id  BIGINT NOT NULL REFERENCES customers(id),
    product_id   BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    order_id     BIGINT REFERENCES orders(id),
    rating       SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment      TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, APPROVED, REJECTED
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(customer_id, product_id, order_id)
);

CREATE TABLE coupons (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE,
    type            VARCHAR(20) NOT NULL,  -- PERCENT, FIXED
    value           NUMERIC(15, 2) NOT NULL,
    min_order       NUMERIC(15, 2),
    max_discount    NUMERIC(15, 2),        -- Cap for PERCENT type
    usage_limit     INT,                   -- NULL = unlimited
    used_count      INT NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    starts_at       TIMESTAMP,
    expires_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE coupon_usages (
    id          BIGSERIAL PRIMARY KEY,
    coupon_id   BIGINT NOT NULL REFERENCES coupons(id),
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    order_id    BIGINT NOT NULL REFERENCES orders(id),
    used_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(coupon_id, order_id)
);

CREATE INDEX idx_payments_order    ON payments(order_id);
CREATE INDEX idx_reviews_product   ON reviews(product_id);
CREATE INDEX idx_reviews_customer  ON reviews(customer_id);
CREATE INDEX idx_reviews_status    ON reviews(status);
CREATE INDEX idx_coupons_code      ON coupons(code);
CREATE INDEX idx_coupon_usages_customer ON coupon_usages(customer_id);
