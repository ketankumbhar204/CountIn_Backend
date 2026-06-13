CREATE TABLE occupancies (
    id                      UUID         NOT NULL,
    space_id                UUID         NOT NULL,
    member_id               UUID         NOT NULL,
    target_type             VARCHAR(10)  NOT NULL,
    building_id             UUID         NOT NULL,
    floor_id                UUID,
    unit_id                 UUID,
    room_id                 UUID,
    bed_id                  UUID,
    allocated_at            TIMESTAMP    NOT NULL,
    allocated_by            UUID         NOT NULL,
    expected_checkout_date  DATE,
    vacated_at              TIMESTAMP,
    vacated_by              UUID,
    status                  VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    remarks                 TEXT,
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by              UUID         NOT NULL,
    updated_by              UUID         NOT NULL,
    CONSTRAINT pk_occupancies              PRIMARY KEY (id),
    CONSTRAINT fk_occupancies_space          FOREIGN KEY (space_id)     REFERENCES spaces (id),
    CONSTRAINT fk_occupancies_member         FOREIGN KEY (member_id)    REFERENCES members (id),
    CONSTRAINT fk_occupancies_building       FOREIGN KEY (building_id)  REFERENCES buildings (id),
    CONSTRAINT fk_occupancies_floor          FOREIGN KEY (floor_id)     REFERENCES floors (id),
    CONSTRAINT fk_occupancies_unit           FOREIGN KEY (unit_id)      REFERENCES units (id),
    CONSTRAINT fk_occupancies_room           FOREIGN KEY (room_id)      REFERENCES rooms (id),
    CONSTRAINT fk_occupancies_bed            FOREIGN KEY (bed_id)       REFERENCES beds (id),
    CONSTRAINT fk_occupancies_allocated_by   FOREIGN KEY (allocated_by) REFERENCES users (id),
    CONSTRAINT fk_occupancies_vacated_by     FOREIGN KEY (vacated_by)   REFERENCES users (id),
    CONSTRAINT fk_occupancies_created_by     FOREIGN KEY (created_by)   REFERENCES users (id),
    CONSTRAINT fk_occupancies_updated_by     FOREIGN KEY (updated_by)   REFERENCES users (id),
    CONSTRAINT chk_occupancies_target_type   CHECK (target_type IN ('BED', 'ROOM', 'UNIT')),
    CONSTRAINT chk_occupancies_status        CHECK (status IN ('ACTIVE', 'VACATED'))
);

CREATE INDEX idx_occupancies_space_id   ON occupancies (space_id);
CREATE INDEX idx_occupancies_member_id  ON occupancies (member_id);
CREATE INDEX idx_occupancies_building_id ON occupancies (building_id);
CREATE INDEX idx_occupancies_status     ON occupancies (space_id, status);

CREATE UNIQUE INDEX uq_occupancies_member_active
    ON occupancies (space_id, member_id)
    WHERE status = 'ACTIVE';

CREATE UNIQUE INDEX uq_occupancies_bed_active
    ON occupancies (bed_id)
    WHERE status = 'ACTIVE' AND bed_id IS NOT NULL;

CREATE UNIQUE INDEX uq_occupancies_room_active
    ON occupancies (room_id)
    WHERE status = 'ACTIVE' AND room_id IS NOT NULL;

CREATE UNIQUE INDEX uq_occupancies_unit_active
    ON occupancies (unit_id)
    WHERE status = 'ACTIVE' AND unit_id IS NOT NULL;
