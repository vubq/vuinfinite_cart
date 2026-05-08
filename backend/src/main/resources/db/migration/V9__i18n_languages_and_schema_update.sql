-- V9: i18n_languages table and schema update for translations
-- Supports dynamic language management and nullable translation values

-- 1. Create i18n_languages table
CREATE TABLE i18n_languages (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(10) NOT NULL UNIQUE,      -- vi, en, ja, ko
    name        VARCHAR(100) NOT NULL,             -- Vietnamese, English
    native_name VARCHAR(100),                      -- Tiếng Việt, English
    is_default  BOOLEAN NOT NULL DEFAULT false,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    sort_order  INT NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 2. Seed initial languages
INSERT INTO i18n_languages (code, name, native_name, is_default, is_active, sort_order)
VALUES
    ('vi', 'Vietnamese', 'Tiếng Việt', true, true, 0),
    ('en', 'English', 'English', false, true, 1);

-- 3. Allow translation_value to be nullable (key can exist without value for some locales)
ALTER TABLE i18n_translations ALTER COLUMN translation_value DROP NOT NULL;

-- 4. Update existing translations to be more consistent if needed
-- (V7 already seeded some, we keep them)
