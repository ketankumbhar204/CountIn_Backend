ALTER TABLE beds
    ADD COLUMN default_rent DECIMAL(12, 2),
    ADD COLUMN default_deposit DECIMAL(12, 2);

ALTER TABLE rooms
    ADD COLUMN default_rent DECIMAL(12, 2),
    ADD COLUMN default_deposit DECIMAL(12, 2);

ALTER TABLE units
    ADD COLUMN default_rent DECIMAL(12, 2),
    ADD COLUMN default_deposit DECIMAL(12, 2);

ALTER TABLE beds
    ADD CONSTRAINT chk_beds_default_rent CHECK (default_rent IS NULL OR default_rent >= 0),
    ADD CONSTRAINT chk_beds_default_deposit CHECK (default_deposit IS NULL OR default_deposit >= 0);

ALTER TABLE rooms
    ADD CONSTRAINT chk_rooms_default_rent CHECK (default_rent IS NULL OR default_rent >= 0),
    ADD CONSTRAINT chk_rooms_default_deposit CHECK (default_deposit IS NULL OR default_deposit >= 0);

ALTER TABLE units
    ADD CONSTRAINT chk_units_default_rent CHECK (default_rent IS NULL OR default_rent >= 0),
    ADD CONSTRAINT chk_units_default_deposit CHECK (default_deposit IS NULL OR default_deposit >= 0);
