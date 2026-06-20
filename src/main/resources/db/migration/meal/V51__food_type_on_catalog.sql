-- Food type classification for menu items and combos (Veg / Non-Veg / Egg)

ALTER TABLE food_items
    ADD COLUMN food_type VARCHAR(20) NOT NULL DEFAULT 'VEG';

ALTER TABLE food_items
    ADD CONSTRAINT chk_food_items_food_type
        CHECK (food_type IN ('VEG', 'NON_VEG', 'EGG'));

ALTER TABLE meal_combos
    ADD COLUMN food_type VARCHAR(20) NOT NULL DEFAULT 'VEG';

ALTER TABLE meal_combos
    ADD CONSTRAINT chk_meal_combos_food_type
        CHECK (food_type IN ('VEG', 'NON_VEG', 'EGG'));

UPDATE meal_combos
SET food_type = 'VEG'
WHERE name IN ('Standard Lunch Thali', 'Dal Rice Combo');

-- Global non-veg and egg items
INSERT INTO food_items (id, category_id, name, scope, space_id, is_active, is_custom, food_type, created_at, updated_at) VALUES
    ('22222222-2222-2222-2222-222222220113', '11111111-1111-1111-1111-111111110004', 'Chicken Curry',   'GLOBAL', NULL, TRUE, FALSE, 'NON_VEG', NOW(), NOW()),
    ('22222222-2222-2222-2222-222222220114', '11111111-1111-1111-1111-111111110004', 'Mutton Curry',    'GLOBAL', NULL, TRUE, FALSE, 'NON_VEG', NOW(), NOW()),
    ('22222222-2222-2222-2222-222222220115', '11111111-1111-1111-1111-111111110004', 'Fish Fry',        'GLOBAL', NULL, TRUE, FALSE, 'NON_VEG', NOW(), NOW()),
    ('22222222-2222-2222-2222-222222220116', '11111111-1111-1111-1111-111111110002', 'Chicken Biryani', 'GLOBAL', NULL, TRUE, FALSE, 'NON_VEG', NOW(), NOW()),
    ('22222222-2222-2222-2222-222222220117', '11111111-1111-1111-1111-111111110005', 'Boiled Egg',      'GLOBAL', NULL, TRUE, FALSE, 'EGG', NOW(), NOW()),
    ('22222222-2222-2222-2222-222222220118', '11111111-1111-1111-1111-111111110005', 'Egg Bhurji',      'GLOBAL', NULL, TRUE, FALSE, 'EGG', NOW(), NOW()),
    ('22222222-2222-2222-2222-222222220119', '11111111-1111-1111-1111-111111110005', 'Omelette',        'GLOBAL', NULL, TRUE, FALSE, 'EGG', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    food_type = EXCLUDED.food_type,
    is_active = TRUE,
    updated_at = NOW();
