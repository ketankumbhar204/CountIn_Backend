ALTER TABLE meal_combos
    ADD COLUMN price DECIMAL(10, 2) NULL,
    ADD COLUMN currency_code VARCHAR(3) NOT NULL DEFAULT 'INR';

ALTER TABLE daily_menu_entries
    ADD COLUMN price DECIMAL(10, 2) NULL,
    ADD COLUMN currency_code VARCHAR(3) NOT NULL DEFAULT 'INR';
