CREATE TABLE beds (
    id          UUID         NOT NULL,
    room_id     UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    bed_number  VARCHAR(20)  NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE',
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_beds            PRIMARY KEY (id),
    CONSTRAINT fk_beds_room         FOREIGN KEY (room_id) REFERENCES rooms (id),
    CONSTRAINT chk_beds_status      CHECK (status IN (
        'AVAILABLE', 'OCCUPIED', 'RESERVED', 'MAINTENANCE', 'BLOCKED'))
);

CREATE INDEX idx_beds_room_id ON beds (room_id);
CREATE UNIQUE INDEX uq_beds_room_bed_number_active
    ON beds (room_id, bed_number)
    WHERE is_active = TRUE;
