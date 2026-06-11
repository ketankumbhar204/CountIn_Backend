CREATE TABLE members (
    id             UUID         NOT NULL,
    space_id       UUID         NOT NULL,
    user_id        UUID,
    membership_id  UUID,
    full_name      VARCHAR(255) NOT NULL,
    mobile_number  VARCHAR(15)  NOT NULL,
    role           VARCHAR(20)  NOT NULL,
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_members              PRIMARY KEY (id),
    CONSTRAINT fk_members_space        FOREIGN KEY (space_id)      REFERENCES spaces (id),
    CONSTRAINT fk_members_user         FOREIGN KEY (user_id)       REFERENCES users (id),
    CONSTRAINT fk_members_membership   FOREIGN KEY (membership_id) REFERENCES space_memberships (id),
    CONSTRAINT chk_members_role        CHECK (role IN ('OWNER', 'MANAGER', 'TENANT', 'CUSTOMER', 'STAFF'))
);

CREATE INDEX idx_members_space_id      ON members (space_id);
CREATE INDEX idx_members_user_id       ON members (user_id);
CREATE INDEX idx_members_membership_id ON members (membership_id);
CREATE INDEX idx_members_mobile_number ON members (mobile_number);

CREATE UNIQUE INDEX uq_members_space_mobile_active
    ON members (space_id, mobile_number)
    WHERE is_active = TRUE;
