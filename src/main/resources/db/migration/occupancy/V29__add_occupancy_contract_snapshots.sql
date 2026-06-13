ALTER TABLE occupancies
    ADD COLUMN rent_snapshot DECIMAL(12, 2),
    ADD COLUMN deposit_snapshot DECIMAL(12, 2) NOT NULL DEFAULT 0,
    ADD COLUMN food_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN food_charge_snapshot DECIMAL(12, 2),
    ADD COLUMN pricing_locked_at TIMESTAMP;

ALTER TABLE occupancies
    ADD CONSTRAINT chk_occupancies_rent_snapshot
        CHECK (rent_snapshot IS NULL OR rent_snapshot >= 0),
    ADD CONSTRAINT chk_occupancies_deposit_snapshot
        CHECK (deposit_snapshot >= 0),
    ADD CONSTRAINT chk_occupancies_food_charge_snapshot
        CHECK (food_charge_snapshot IS NULL OR food_charge_snapshot >= 0);
