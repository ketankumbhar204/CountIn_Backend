ALTER TABLE members
    ADD COLUMN occupancy_status VARCHAR(20) NOT NULL DEFAULT 'VACATED';

ALTER TABLE members
    ADD CONSTRAINT chk_members_occupancy_status
        CHECK (occupancy_status IN ('ALLOCATED', 'VACATED'));
