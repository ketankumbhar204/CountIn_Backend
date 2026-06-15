-- Backfill any missing preset meal plan codes per space (not only when a space has zero plans).
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
WHERE NOT EXISTS (
    SELECT 1
    FROM meal_plans mp
    WHERE mp.space_id = s.id
      AND mp.code = v.code
);
