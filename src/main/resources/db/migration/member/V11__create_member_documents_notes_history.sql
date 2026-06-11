CREATE TABLE member_documents (
    id                  UUID          NOT NULL,
    member_id           UUID          NOT NULL,
    document_type       VARCHAR(30)   NOT NULL,
    document_number     VARCHAR(100)  NOT NULL,
    file_url            VARCHAR(1024) NOT NULL,
    verification_status VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    uploaded_at         TIMESTAMP     NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_member_documents              PRIMARY KEY (id),
    CONSTRAINT fk_member_documents_member       FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT chk_member_documents_type        CHECK (document_type IN (
        'AADHAAR', 'PAN', 'PASSPORT', 'DRIVING_LICENSE', 'STUDENT_ID', 'OTHER')),
    CONSTRAINT chk_member_documents_verification  CHECK (verification_status IN (
        'PENDING', 'VERIFIED', 'REJECTED'))
);

CREATE INDEX idx_member_documents_member_id ON member_documents (member_id);

CREATE TABLE member_notes (
    id          UUID        NOT NULL,
    member_id   UUID        NOT NULL,
    note        TEXT        NOT NULL,
    created_by  UUID        NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_member_notes        PRIMARY KEY (id),
    CONSTRAINT fk_member_notes_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_member_notes_user   FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE INDEX idx_member_notes_member_id ON member_notes (member_id);

CREATE TABLE member_history (
    id          UUID        NOT NULL,
    member_id   UUID        NOT NULL,
    action      VARCHAR(40) NOT NULL,
    old_value   TEXT,
    new_value   TEXT,
    changed_by  UUID        NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_member_history        PRIMARY KEY (id),
    CONSTRAINT fk_member_history_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_member_history_user   FOREIGN KEY (changed_by) REFERENCES users (id),
    CONSTRAINT chk_member_history_action CHECK (action IN (
        'STATUS_CHANGED', 'DEPOSIT_UPDATED', 'EMERGENCY_CONTACT_UPDATED'))
);

CREATE INDEX idx_member_history_member_id ON member_history (member_id);
CREATE INDEX idx_member_history_created_at ON member_history (member_id, created_at DESC);
