-- Seed preset meal plans for spaces that do not have any plans yet
INSERT INTO meal_plans (
    id, space_id, code, name,
    breakfast_included, lunch_included, dinner_included,
    is_active, sort_order, created_at, updated_at
)
SELECT gen_random_uuid(), s.id, v.code, v.name, v.breakfast, v.lunch, v.dinner, TRUE, v.sort_order, NOW(), NOW()
FROM spaces s
CROSS JOIN (
    VALUES
        ('NONE', 'No Meals', FALSE, FALSE, FALSE, 0),
        ('BREAKFAST', 'Breakfast Only', TRUE, FALSE, FALSE, 1),
        ('LUNCH', 'Lunch Only', FALSE, TRUE, FALSE, 2),
        ('DINNER', 'Dinner Only', FALSE, FALSE, TRUE, 3),
        ('FULL', 'Full Meals', TRUE, TRUE, TRUE, 4),
        ('CUSTOM', 'Custom Plan', FALSE, FALSE, FALSE, 5)
) AS v(code, name, breakfast, lunch, dinner, sort_order)
WHERE NOT EXISTS (SELECT 1 FROM meal_plans mp WHERE mp.space_id = s.id);

-- Backfill ACTIVE participations from food-enabled occupancies
INSERT INTO meal_participations (
    id, space_id, member_id, meal_plan_id, status,
    effective_from, effective_to, source_occupancy_id, entitlement_id,
    stopped_at, created_at, updated_at
)
SELECT
    gen_random_uuid(),
    o.space_id,
    o.member_id,
    mp.id,
    'ACTIVE',
    COALESCE(o.move_in_date, CURRENT_DATE),
    NULL,
    o.id,
    NULL,
    NULL,
    NOW(),
    NOW()
FROM occupancies o
JOIN meal_plans mp ON mp.space_id = o.space_id AND mp.code = 'FULL'
WHERE o.status = 'ACTIVE'
  AND (o.food_enabled = TRUE OR o.food_included_in_rent = TRUE)
  AND NOT EXISTS (
      SELECT 1 FROM meal_participations p
      WHERE p.space_id = o.space_id
        AND p.member_id = o.member_id
        AND p.status = 'ACTIVE'
  );
