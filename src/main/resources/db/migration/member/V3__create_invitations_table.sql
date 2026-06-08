CREATE TABLE invitations (
    id                  UUID        NOT NULL,
    space_id            UUID        NOT NULL,
    invited_by_user_id  UUID        NOT NULL,
    mobile_number       VARCHAR(15) NOT NULL,
    role                VARCHAR(20) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    expires_at          TIMESTAMP   NOT NULL,
    accepted_at         TIMESTAMP,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_invitations            PRIMARY KEY (id),
    CONSTRAINT fk_invitations_space      FOREIGN KEY (space_id)           REFERENCES spaces (id),
    CONSTRAINT fk_invitations_invited_by FOREIGN KEY (invited_by_user_id) REFERENCES users  (id),
    CONSTRAINT chk_invitations_role      CHECK (role   IN ('OWNER', 'MANAGER', 'TENANT', 'CUSTOMER', 'STAFF')),
    CONSTRAINT chk_invitations_status    CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED'))
);

CREATE INDEX idx_invitations_space_id      ON invitations (space_id);
CREATE INDEX idx_invitations_mobile_number ON invitations (mobile_number);
CREATE INDEX idx_invitations_status        ON invitations (status);
