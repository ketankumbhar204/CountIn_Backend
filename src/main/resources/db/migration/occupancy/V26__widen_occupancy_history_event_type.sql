-- RESERVATION_CANCELLED (22 chars) exceeds original VARCHAR(20)

ALTER TABLE occupancy_history
    ALTER COLUMN event_type TYPE VARCHAR(30);
