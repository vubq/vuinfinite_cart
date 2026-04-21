-- V5: Cart & Order tables
CREATE TABLE carts (
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE,
    session_id  VARCHAR(255),   -- For guest carts before login
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_cart_owner CHECK (customer_id IS NOT NULL OR session_id IS NOT NULL)
);

CREATE TABLE cart_items (
    id          BIGSERIAL PRIMARY KEY,
    cart_id     BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    sku_id      BIGINT NOT NULL REFERENCES product_skus(id) ON DELETE CASCADE,
    quantity    INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    added_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(cart_id, sku_id)
);

CREATE TABLE orders (
    id               BIGSERIAL PRIMARY KEY,
    customer_id      BIGINT NOT NULL REFERENCES customers(id),
    status           VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    -- PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
    subtotal         NUMERIC(15, 2) NOT NULL,
    discount_amount  NUMERIC(15, 2) NOT NULL DEFAULT 0,
    shipping_fee     NUMERIC(15, 2) NOT NULL DEFAULT 0,
    total_amount     NUMERIC(15, 2) NOT NULL,
    coupon_code      VARCHAR(50),
    note             TEXT,
    -- Snapshot of delivery address at order time
    address_snapshot JSONB NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    sku_id       BIGINT REFERENCES product_skus(id) ON DELETE SET NULL,
    quantity     INT NOT NULL,
    unit_price   NUMERIC(15, 2) NOT NULL,
    -- Snapshot of product info at order time
    product_snapshot JSONB NOT NULL
);

CREATE INDEX idx_carts_customer    ON carts(customer_id);
CREATE INDEX idx_orders_customer   ON orders(customer_id);
CREATE INDEX idx_orders_status     ON orders(status);
CREATE INDEX idx_order_items_order ON order_items(order_id);
