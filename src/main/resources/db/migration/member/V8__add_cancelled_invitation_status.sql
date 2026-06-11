ALTER TABLE invitations
    DROP CONSTRAINT IF EXISTS chk_invitations_status;

ALTER TABLE invitations
    ADD CONSTRAINT chk_invitations_status
        CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED', 'CANCELLED'));
