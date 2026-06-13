CREATE TABLE rooms (
    id           UUID         NOT NULL,
    floor_id     UUID,
    unit_id      UUID,
    name         VARCHAR(255) NOT NULL,
    room_number  VARCHAR(50)  NOT NULL,
    room_type    VARCHAR(20)  NOT NULL,
    capacity     INT          NOT NULL DEFAULT 1,
    status       VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE',
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_rooms              PRIMARY KEY (id),
    CONSTRAINT fk_rooms_floor          FOREIGN KEY (floor_id) REFERENCES floors (id),
    CONSTRAINT fk_rooms_unit           FOREIGN KEY (unit_id) REFERENCES units (id),
    CONSTRAINT chk_rooms_parent        CHECK (
        (floor_id IS NOT NULL AND unit_id IS NULL)
        OR (floor_id IS NULL AND unit_id IS NOT NULL)),
    CONSTRAINT chk_rooms_room_type     CHECK (room_type IN ('PRIVATE', 'SHARED', 'DORMITORY')),
    CONSTRAINT chk_rooms_status        CHECK (status IN (
        'AVAILABLE', 'OCCUPIED', 'RESERVED', 'MAINTENANCE', 'BLOCKED')),
    CONSTRAINT chk_rooms_capacity      CHECK (capacity >= 1)
);

CREATE INDEX idx_rooms_floor_id ON rooms (floor_id);
CREATE INDEX idx_rooms_unit_id ON rooms (unit_id);
