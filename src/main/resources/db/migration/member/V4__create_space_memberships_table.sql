CREATE TABLE space_memberships (
    id            UUID        NOT NULL,
    user_id       UUID        NOT NULL,
    space_id      UUID        NOT NULL,
    role          VARCHAR(20) NOT NULL,
    status        VARCHAR(20) NOT NULL,
    invitation_id UUID,
    joined_at     TIMESTAMP,
    exited_at     TIMESTAMP,
    created_at    TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_space_memberships            PRIMARY KEY (id),
    CONSTRAINT fk_space_memberships_user       FOREIGN KEY (user_id)       REFERENCES users       (id),
    CONSTRAINT fk_space_memberships_space      FOREIGN KEY (space_id)      REFERENCES spaces      (id),
    CONSTRAINT fk_space_memberships_invitation FOREIGN KEY (invitation_id) REFERENCES invitations (id),
    CONSTRAINT uq_space_memberships_user_space UNIQUE (user_id, space_id),
    CONSTRAINT chk_space_memberships_role      CHECK (role   IN ('OWNER', 'MANAGER', 'TENANT', 'CUSTOMER', 'STAFF')),
    CONSTRAINT chk_space_memberships_status    CHECK (status IN ('INVITATION_SENT', 'ACCEPTED', 'ACTIVE', 'INACTIVE', 'REMOVED', 'VACATED'))
);

CREATE INDEX idx_space_memberships_user_id  ON space_memberships (user_id);
CREATE INDEX idx_space_memberships_space_id ON space_memberships (space_id);
CREATE INDEX idx_space_memberships_status   ON space_memberships (status);
