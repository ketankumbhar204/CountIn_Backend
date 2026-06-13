CREATE TABLE occupancy_charge_snapshots (
    id              UUID            NOT NULL,
    occupancy_id    UUID            NOT NULL,
    charge_code     VARCHAR(30)     NOT NULL,
    label           VARCHAR(100)    NOT NULL,
    amount          DECIMAL(12, 2)  NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_occupancy_charge_snapshots PRIMARY KEY (id),
    CONSTRAINT fk_occupancy_charge_snapshots_occupancy
        FOREIGN KEY (occupancy_id) REFERENCES occupancies (id),
    CONSTRAINT chk_occupancy_charge_snapshots_amount CHECK (amount >= 0)
);

CREATE INDEX idx_occupancy_charge_snapshots_occupancy_id
    ON occupancy_charge_snapshots (occupancy_id);
