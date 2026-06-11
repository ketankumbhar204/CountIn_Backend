ALTER TABLE space_memberships
    ADD COLUMN IF NOT EXISTS is_default BOOLEAN NOT NULL DEFAULT FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS uq_space_memberships_one_default_per_user
    ON space_memberships (user_id)
    WHERE is_default = TRUE;

CREATE INDEX IF NOT EXISTS idx_space_memberships_is_default
    ON space_memberships (user_id, is_default)
    WHERE is_default = TRUE;
