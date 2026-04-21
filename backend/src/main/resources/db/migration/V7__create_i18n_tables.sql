-- V7: i18n (Internationalization) tables
-- Hot-reload from DB — no redeploy needed when translations change
CREATE TABLE i18n_translations (
    id                  BIGSERIAL PRIMARY KEY,
    locale              VARCHAR(10) NOT NULL,      -- e.g. vi, en, ja
    namespace           VARCHAR(100) NOT NULL,     -- e.g. common, product, admin
    translation_key     VARCHAR(500) NOT NULL,     -- e.g. btn.addToCart
    translation_value   TEXT NOT NULL,             -- e.g. Add to Cart
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(locale, namespace, translation_key)
);

CREATE INDEX idx_i18n_locale_namespace ON i18n_translations(locale, namespace);

-- Seed default translations (Vietnamese + English)
INSERT INTO i18n_translations (locale, namespace, translation_key, translation_value) VALUES
    -- Common (EN)
    ('en', 'common', 'btn.addToCart',    'Add to Cart'),
    ('en', 'common', 'btn.buyNow',       'Buy Now'),
    ('en', 'common', 'btn.submit',       'Submit'),
    ('en', 'common', 'btn.cancel',       'Cancel'),
    ('en', 'common', 'btn.save',         'Save'),
    ('en', 'common', 'btn.edit',         'Edit'),
    ('en', 'common', 'btn.delete',       'Delete'),
    ('en', 'common', 'btn.login',        'Login'),
    ('en', 'common', 'btn.logout',       'Logout'),
    ('en', 'common', 'btn.register',     'Register'),
    ('en', 'common', 'label.search',     'Search'),
    ('en', 'common', 'label.loading',    'Loading...'),
    ('en', 'common', 'msg.noResults',    'No results found'),
    -- Common (VI)
    ('vi', 'common', 'btn.addToCart',    'Them vao gio'),
    ('vi', 'common', 'btn.buyNow',       'Mua ngay'),
    ('vi', 'common', 'btn.submit',       'Xac nhan'),
    ('vi', 'common', 'btn.cancel',       'Huy'),
    ('vi', 'common', 'btn.save',         'Luu'),
    ('vi', 'common', 'btn.edit',         'Chinh sua'),
    ('vi', 'common', 'btn.delete',       'Xoa'),
    ('vi', 'common', 'btn.login',        'Dang nhap'),
    ('vi', 'common', 'btn.logout',       'Dang xuat'),
    ('vi', 'common', 'btn.register',     'Dang ky'),
    ('vi', 'common', 'label.search',     'Tim kiem'),
    ('vi', 'common', 'label.loading',    'Dang tai...'),
    ('vi', 'common', 'msg.noResults',    'Khong tim thay ket qua');
