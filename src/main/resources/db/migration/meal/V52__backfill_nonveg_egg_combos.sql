-- Backfill Chicken Thali and Egg Combo for spaces that do not have them yet.

DO $$
DECLARE
    target_space RECORD;
    chicken_combo_id UUID;
    egg_combo_id UUID;
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM food_items
        WHERE id = '22222222-2222-2222-2222-222222220113' AND scope = 'GLOBAL'
    ) THEN
        RETURN;
    END IF;

    FOR target_space IN
        SELECT s.id
        FROM spaces s
        WHERE s.is_active = TRUE
    LOOP
        IF NOT EXISTS (
            SELECT 1 FROM meal_combos mc
            WHERE mc.space_id = target_space.id
              AND mc.is_active = TRUE
              AND mc.name = 'Chicken Thali'
        ) THEN
            chicken_combo_id := gen_random_uuid();
            INSERT INTO meal_combos (id, space_id, name, description, is_active, food_type, created_at, updated_at)
            VALUES (
                chicken_combo_id,
                target_space.id,
                'Chicken Thali',
                'Non-veg lunch combo',
                TRUE,
                'NON_VEG',
                NOW(),
                NOW()
            );

            INSERT INTO meal_combo_items (id, combo_id, item_id, sort_order) VALUES
                (gen_random_uuid(), chicken_combo_id, '22222222-2222-2222-2222-222222220001', 0),
                (gen_random_uuid(), chicken_combo_id, '22222222-2222-2222-2222-222222220113', 1),
                (gen_random_uuid(), chicken_combo_id, '22222222-2222-2222-2222-222222220010', 2);
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM meal_combos mc
            WHERE mc.space_id = target_space.id
              AND mc.is_active = TRUE
              AND mc.name = 'Egg Combo'
        ) THEN
            egg_combo_id := gen_random_uuid();
            INSERT INTO meal_combos (id, space_id, name, description, is_active, food_type, created_at, updated_at)
            VALUES (
                egg_combo_id,
                target_space.id,
                'Egg Combo',
                'Egg meal combo',
                TRUE,
                'EGG',
                NOW(),
                NOW()
            );

            INSERT INTO meal_combo_items (id, combo_id, item_id, sort_order) VALUES
                (gen_random_uuid(), egg_combo_id, '22222222-2222-2222-2222-222222220001', 0),
                (gen_random_uuid(), egg_combo_id, '22222222-2222-2222-2222-222222220118', 1),
                (gen_random_uuid(), egg_combo_id, '22222222-2222-2222-2222-222222220010', 2);
        END IF;
    END LOOP;
END $$;
