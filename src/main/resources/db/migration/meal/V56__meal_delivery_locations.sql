CREATE TABLE meal_delivery_locations (
    id              UUID         NOT NULL,
    space_id        UUID         NOT NULL,
    name            VARCHAR(120) NOT NULL,
    description     VARCHAR(500),
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    sort_order      INT          NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_delivery_locations PRIMARY KEY (id),
    CONSTRAINT fk_meal_delivery_locations_space FOREIGN KEY (space_id) REFERENCES spaces (id)
);

CREATE INDEX idx_meal_delivery_locations_space ON meal_delivery_locations (space_id, active, sort_order);

CREATE TABLE meal_poll_member_delivery (
    id                      UUID         NOT NULL,
    poll_id                 UUID         NOT NULL,
    member_id               UUID         NOT NULL,
    delivery_location_id    UUID         NOT NULL,
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_poll_member_delivery PRIMARY KEY (id),
    CONSTRAINT fk_meal_poll_member_delivery_poll FOREIGN KEY (poll_id) REFERENCES meal_polls (id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_poll_member_delivery_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_meal_poll_member_delivery_location FOREIGN KEY (delivery_location_id) REFERENCES meal_delivery_locations (id),
    CONSTRAINT uq_meal_poll_member_delivery UNIQUE (poll_id, member_id)
);

CREATE INDEX idx_meal_poll_member_delivery_poll ON meal_poll_member_delivery (poll_id);

ALTER TABLE meal_participations
    ADD COLUMN default_delivery_location_id UUID,
    ADD CONSTRAINT fk_meal_participations_default_delivery
        FOREIGN KEY (default_delivery_location_id) REFERENCES meal_delivery_locations (id);
