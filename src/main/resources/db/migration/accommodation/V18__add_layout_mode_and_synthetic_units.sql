-- Phase 4.2.5: Property layout modes and synthetic units

ALTER TABLE buildings
    ADD COLUMN layout_mode VARCHAR(30) NOT NULL DEFAULT 'CORRIDOR_PG';

ALTER TABLE units
    ADD COLUMN synthetic BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE units
    ADD COLUMN unit_kind VARCHAR(30);

CREATE INDEX IF NOT EXISTS idx_units_floor_id ON units (floor_id);

CREATE INDEX IF NOT EXISTS idx_units_building_synthetic ON units (building_id, synthetic);
