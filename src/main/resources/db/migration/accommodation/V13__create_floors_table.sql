CREATE TABLE floors (
    id            UUID         NOT NULL,
    building_id   UUID         NOT NULL,
    name          VARCHAR(255) NOT NULL,
    floor_number  INT          NOT NULL,
    sort_order    INT          NOT NULL DEFAULT 0,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_floors            PRIMARY KEY (id),
    CONSTRAINT fk_floors_building   FOREIGN KEY (building_id) REFERENCES buildings (id)
);

CREATE INDEX idx_floors_building_id ON floors (building_id);
CREATE UNIQUE INDEX uq_floors_building_floor_number_active
    ON floors (building_id, floor_number)
    WHERE is_active = TRUE;
