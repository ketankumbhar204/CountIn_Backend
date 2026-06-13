ALTER TABLE spaces
    ADD COLUMN default_food_charge DECIMAL(12, 2);

ALTER TABLE spaces
    ADD CONSTRAINT chk_spaces_default_food_charge
        CHECK (default_food_charge IS NULL OR default_food_charge >= 0);
