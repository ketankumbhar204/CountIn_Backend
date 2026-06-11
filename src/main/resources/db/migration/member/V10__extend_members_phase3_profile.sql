ALTER TABLE members
    ADD COLUMN IF NOT EXISTS status                     VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS status_updated_at          TIMESTAMP,
    ADD COLUMN IF NOT EXISTS emergency_contact_name     VARCHAR(255),
    ADD COLUMN IF NOT EXISTS emergency_contact_relation VARCHAR(100),
    ADD COLUMN IF NOT EXISTS emergency_contact_mobile   VARCHAR(15),
    ADD COLUMN IF NOT EXISTS deposit_amount             NUMERIC(12, 2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS deposit_paid               NUMERIC(12, 2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS deposit_refunded           NUMERIC(12, 2) NOT NULL DEFAULT 0;

ALTER TABLE members
    DROP CONSTRAINT IF EXISTS chk_members_status;

ALTER TABLE members
    ADD CONSTRAINT chk_members_status
        CHECK (status IN ('ACTIVE', 'VACATED', 'SUSPENDED', 'BLACKLISTED'));

UPDATE members
SET status_updated_at = created_at
WHERE status_updated_at IS NULL;
