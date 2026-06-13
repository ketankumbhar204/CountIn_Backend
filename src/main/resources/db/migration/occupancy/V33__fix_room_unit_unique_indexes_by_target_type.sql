-- Bed-level occupancies denormalize room_id/unit_id for location.
-- Room/unit uniqueness must apply only to ROOM/UNIT target allocations, not every bed in the same room/unit.

DROP INDEX IF EXISTS uq_occupancies_room_active;
DROP INDEX IF EXISTS uq_occupancies_unit_active;
DROP INDEX IF EXISTS uq_occupancies_room_reserved;
DROP INDEX IF EXISTS uq_occupancies_unit_reserved;

CREATE UNIQUE INDEX uq_occupancies_room_active
    ON occupancies (room_id)
    WHERE status = 'ACTIVE'
      AND room_id IS NOT NULL
      AND target_type = 'ROOM';

CREATE UNIQUE INDEX uq_occupancies_unit_active
    ON occupancies (unit_id)
    WHERE status = 'ACTIVE'
      AND unit_id IS NOT NULL
      AND target_type = 'UNIT';

CREATE UNIQUE INDEX uq_occupancies_room_reserved
    ON occupancies (room_id)
    WHERE status = 'RESERVED'
      AND room_id IS NOT NULL
      AND target_type = 'ROOM';

CREATE UNIQUE INDEX uq_occupancies_unit_reserved
    ON occupancies (unit_id)
    WHERE status = 'RESERVED'
      AND unit_id IS NOT NULL
      AND target_type = 'UNIT';
