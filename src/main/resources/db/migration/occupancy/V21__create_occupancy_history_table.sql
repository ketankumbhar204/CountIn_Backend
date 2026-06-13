CREATE TABLE occupancy_history (
    id                  UUID         NOT NULL,
    occupancy_id        UUID         NOT NULL,
    space_id            UUID         NOT NULL,
    member_id           UUID         NOT NULL,
    event_type          VARCHAR(20)  NOT NULL,
    from_target_type    VARCHAR(10),
    from_building_id    UUID,
    from_floor_id       UUID,
    from_unit_id        UUID,
    from_room_id        UUID,
    from_bed_id         UUID,
    to_target_type      VARCHAR(10),
    to_building_id      UUID,
    to_floor_id         UUID,
    to_unit_id          UUID,
    to_room_id          UUID,
    to_bed_id           UUID,
    performed_by        UUID         NOT NULL,
    performed_at        TIMESTAMP    NOT NULL,
    remarks             TEXT,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_occupancy_history             PRIMARY KEY (id),
    CONSTRAINT fk_occupancy_history_occupancy   FOREIGN KEY (occupancy_id)   REFERENCES occupancies (id),
    CONSTRAINT fk_occupancy_history_space       FOREIGN KEY (space_id)       REFERENCES spaces (id),
    CONSTRAINT fk_occupancy_history_member      FOREIGN KEY (member_id)      REFERENCES members (id),
    CONSTRAINT fk_occupancy_history_performed_by FOREIGN KEY (performed_by)  REFERENCES users (id),
    CONSTRAINT chk_occupancy_history_event_type CHECK (event_type IN ('ALLOCATED', 'TRANSFERRED', 'VACATED'))
);

CREATE INDEX idx_occupancy_history_occupancy_id ON occupancy_history (occupancy_id);
CREATE INDEX idx_occupancy_history_member_id    ON occupancy_history (member_id, performed_at DESC);
