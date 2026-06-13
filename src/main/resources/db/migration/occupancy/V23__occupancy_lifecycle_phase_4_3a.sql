-- Phase 4.3a: occupancy lifecycle (reserve, move-in, cancel)

ALTER TABLE occupancies
    ADD COLUMN reserved_at TIMESTAMP,
    ADD COLUMN move_in_date DATE,
    ADD COLUMN actual_move_in_at TIMESTAMP,
    ADD COLUMN expected_exit_date DATE,
    ADD COLUMN member_category VARCHAR(30),
    ADD COLUMN agreement_signed BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE occupancies
SET move_in_date = allocated_at::date,
    actual_move_in_at = allocated_at
WHERE status = 'ACTIVE';

UPDATE occupancies
SET expected_exit_date = expected_checkout_date
WHERE expected_checkout_date IS NOT NULL;

ALTER TABLE occupancies
    ALTER COLUMN move_in_date SET NOT NULL;

ALTER TABLE occupancies
    DROP CONSTRAINT chk_occupancies_status;

ALTER TABLE occupancies
    ADD CONSTRAINT chk_occupancies_status
        CHECK (status IN ('ACTIVE', 'VACATED', 'RESERVED'));

ALTER TABLE occupancies
    ADD CONSTRAINT chk_occupancies_member_category
        CHECK (member_category IS NULL OR member_category IN (
            'STUDENT', 'WORKING_PROFESSIONAL', 'FAMILY', 'GUEST', 'INTERN'
        ));

CREATE UNIQUE INDEX uq_occupancies_member_reserved
    ON occupancies (space_id, member_id)
    WHERE status = 'RESERVED';

CREATE UNIQUE INDEX uq_occupancies_bed_reserved
    ON occupancies (bed_id)
    WHERE status = 'RESERVED' AND bed_id IS NOT NULL;

CREATE UNIQUE INDEX uq_occupancies_room_reserved
    ON occupancies (room_id)
    WHERE status = 'RESERVED' AND room_id IS NOT NULL;

CREATE UNIQUE INDEX uq_occupancies_unit_reserved
    ON occupancies (unit_id)
    WHERE status = 'RESERVED' AND unit_id IS NOT NULL;

ALTER TABLE occupancy_history
    DROP CONSTRAINT chk_occupancy_history_event_type;

ALTER TABLE occupancy_history
    ADD CONSTRAINT chk_occupancy_history_event_type
        CHECK (event_type IN (
            'ALLOCATED', 'TRANSFERRED', 'VACATED',
            'RESERVED', 'MOVE_IN', 'RESERVATION_CANCELLED'
        ));
