CREATE TABLE meal_participation_last_delivery (
    id                      UUID         NOT NULL,
    participation_id        UUID         NOT NULL,
    meal_type               VARCHAR(20)  NOT NULL,
    delivery_location_id    UUID         NOT NULL,
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_participation_last_delivery PRIMARY KEY (id),
    CONSTRAINT fk_meal_part_last_delivery_participation
        FOREIGN KEY (participation_id) REFERENCES meal_participations (id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_part_last_delivery_location
        FOREIGN KEY (delivery_location_id) REFERENCES meal_delivery_locations (id),
    CONSTRAINT uq_meal_part_last_delivery_part_meal UNIQUE (participation_id, meal_type)
);

CREATE INDEX idx_meal_part_last_delivery_participation
    ON meal_participation_last_delivery (participation_id);

-- Backfill from most recent poll delivery per member, meal slot, and space.
INSERT INTO meal_participation_last_delivery (id, participation_id, meal_type, delivery_location_id, created_at, updated_at)
SELECT gen_random_uuid(),
       ranked.participation_id,
       ranked.meal_type,
       ranked.delivery_location_id,
       NOW(),
       NOW()
FROM (
    SELECT DISTINCT ON (part.id, p.meal_type)
        part.id AS participation_id,
        p.meal_type,
        mpmd.delivery_location_id,
        mpmd.updated_at
    FROM meal_poll_member_delivery mpmd
    JOIN meal_polls p ON p.id = mpmd.poll_id
    JOIN meal_participations part
        ON part.member_id = mpmd.member_id
       AND part.space_id = p.space_id
       AND part.status = 'ACTIVE'
    ORDER BY part.id, p.meal_type, mpmd.updated_at DESC
) ranked
ON CONFLICT (participation_id, meal_type) DO NOTHING;

-- Fallback: copy legacy single default to all meal slots when no poll history exists.
INSERT INTO meal_participation_last_delivery (id, participation_id, meal_type, delivery_location_id, created_at, updated_at)
SELECT gen_random_uuid(), mp.id, slot.meal_type, mp.default_delivery_location_id, NOW(), NOW()
FROM meal_participations mp
CROSS JOIN (VALUES ('BREAKFAST'), ('LUNCH'), ('DINNER')) AS slot(meal_type)
WHERE mp.default_delivery_location_id IS NOT NULL
  AND mp.status = 'ACTIVE'
  AND NOT EXISTS (
      SELECT 1
      FROM meal_participation_last_delivery existing
      WHERE existing.participation_id = mp.id
        AND existing.meal_type = slot.meal_type
  );
