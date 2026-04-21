-- V4: Product & Category tables
CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    parent_id   BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    image_url   TEXT,
    sort_order  INT NOT NULL DEFAULT 0,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE products (
    id           BIGSERIAL PRIMARY KEY,
    category_id  BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    name         VARCHAR(500) NOT NULL,
    slug         VARCHAR(500) NOT NULL UNIQUE,
    description  TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT, ACTIVE, ARCHIVED
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE product_images (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url         TEXT NOT NULL,
    alt_text    VARCHAR(255),
    sort_order  INT NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Variant attributes (e.g. "Color", "Size")
CREATE TABLE product_attributes (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,   -- e.g. Color
    sort_order  INT NOT NULL DEFAULT 0
);

-- Attribute values (e.g. "Red", "XL")
CREATE TABLE product_attribute_values (
    id            BIGSERIAL PRIMARY KEY,
    attribute_id  BIGINT NOT NULL REFERENCES product_attributes(id) ON DELETE CASCADE,
    value         VARCHAR(255) NOT NULL,
    sort_order    INT NOT NULL DEFAULT 0
);

-- SKU = a unique combination of attribute values
CREATE TABLE product_skus (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sku_code    VARCHAR(100) NOT NULL UNIQUE,
    price       NUMERIC(15, 2) NOT NULL,
    sale_price  NUMERIC(15, 2),
    stock       INT NOT NULL DEFAULT 0,
    image_url   TEXT,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Maps each SKU to the attribute values that define it
CREATE TABLE product_sku_attributes (
    sku_id              BIGINT NOT NULL REFERENCES product_skus(id) ON DELETE CASCADE,
    attribute_value_id  BIGINT NOT NULL REFERENCES product_attribute_values(id) ON DELETE CASCADE,
    PRIMARY KEY (sku_id, attribute_value_id)
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_status   ON products(status);
CREATE INDEX idx_product_skus_product ON product_skus(product_id);
CREATE INDEX idx_product_images_product ON product_images(product_id);
