-- Spec refers to an "active" flag; the spaces table uses is_active (see V2).
-- Idempotent safeguard for environments created before V2 included the column.
ALTER TABLE spaces
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;
