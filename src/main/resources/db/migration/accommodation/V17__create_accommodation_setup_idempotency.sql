CREATE TABLE accommodation_setup_idempotency (
    id              UUID         NOT NULL,
    space_id        UUID         NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    building_id     UUID         NOT NULL,
    totals_json     TEXT,
    created_by      UUID         NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_accommodation_setup_idempotency PRIMARY KEY (id),
    CONSTRAINT fk_setup_idempotency_space    FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT fk_setup_idempotency_building FOREIGN KEY (building_id) REFERENCES buildings (id),
    CONSTRAINT fk_setup_idempotency_user     FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT uq_setup_idempotency_space_key UNIQUE (space_id, idempotency_key)
);

CREATE INDEX idx_setup_idempotency_created_at ON accommodation_setup_idempotency (created_at);
