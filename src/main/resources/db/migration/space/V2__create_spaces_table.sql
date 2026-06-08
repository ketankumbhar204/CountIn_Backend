CREATE TABLE spaces (
    id             UUID         NOT NULL,
    owner_id       UUID         NOT NULL,
    name           VARCHAR(255) NOT NULL,
    type           VARCHAR(20)  NOT NULL,
    address        TEXT,
    contact_number VARCHAR(15),
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_spaces       PRIMARY KEY (id),
    CONSTRAINT fk_spaces_owner FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT chk_spaces_type CHECK (type IN ('PG', 'MESS', 'HOSTEL', 'CO_LIVING'))
);

CREATE INDEX idx_spaces_owner_id ON spaces (owner_id);
