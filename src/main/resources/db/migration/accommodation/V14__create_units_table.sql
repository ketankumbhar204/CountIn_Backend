CREATE TABLE units (
    id           UUID         NOT NULL,
    building_id  UUID         NOT NULL,
    floor_id     UUID,
    name         VARCHAR(255) NOT NULL,
    unit_number  VARCHAR(50)  NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE',
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_units              PRIMARY KEY (id),
    CONSTRAINT fk_units_building     FOREIGN KEY (building_id) REFERENCES buildings (id),
    CONSTRAINT fk_units_floor        FOREIGN KEY (floor_id) REFERENCES floors (id),
    CONSTRAINT chk_units_status      CHECK (status IN (
        'AVAILABLE', 'OCCUPIED', 'RESERVED', 'MAINTENANCE', 'BLOCKED'))
);

CREATE INDEX idx_units_building_id ON units (building_id);
CREATE UNIQUE INDEX uq_units_building_unit_number_active
    ON units (building_id, unit_number)
    WHERE is_active = TRUE;
