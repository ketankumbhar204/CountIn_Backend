-- Backfill synthetic units for existing PG corridor rooms attached directly to floors.
-- After migration: Floor -> Synthetic Unit -> Room

-- Set layout mode on existing buildings from space type
UPDATE buildings b
SET layout_mode = CASE s.type
    WHEN 'PG' THEN 'CORRIDOR_PG'
    WHEN 'HOSTEL' THEN 'CORRIDOR_PG'
    WHEN 'CO_LIVING' THEN 'CO_LIVING'
    WHEN 'RENTAL' THEN 'RENTAL'
    ELSE b.layout_mode
END
FROM spaces s
WHERE b.space_id = s.id;

-- Create one synthetic unit per floor-attached room and re-parent the room
INSERT INTO units (
    id,
    building_id,
    floor_id,
    name,
    unit_number,
    status,
    is_active,
    synthetic,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    f.building_id,
    r.floor_id,
    'Unit ' || r.room_number,
    r.room_number,
    'AVAILABLE',
    TRUE,
    TRUE,
    NOW(),
    NOW()
FROM rooms r
JOIN floors f ON f.id = r.floor_id
WHERE r.floor_id IS NOT NULL
  AND r.unit_id IS NULL
  AND r.is_active = TRUE
  AND f.is_active = TRUE;

UPDATE rooms r
SET floor_id = NULL,
    unit_id = u.id,
    updated_at = NOW()
FROM units u
WHERE r.floor_id IS NOT NULL
  AND r.unit_id IS NULL
  AND u.synthetic = TRUE
  AND u.floor_id = r.floor_id
  AND u.unit_number = r.room_number
  AND u.building_id = (
      SELECT f.building_id FROM floors f WHERE f.id = r.floor_id
  );
