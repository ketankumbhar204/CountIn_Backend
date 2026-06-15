-- Backfill sample combos for existing spaces (PG, HOSTEL, CO_LIVING, RENTAL, MESS) that have none.
DO $$
DECLARE
    target_space RECORD;
    thali_combo_id UUID;
    dal_rice_combo_id UUID;
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM food_items
        WHERE id = '22222222-2222-2222-2222-222222220001' AND scope = 'GLOBAL'
    ) THEN
        RETURN;
    END IF;

    FOR target_space IN
        SELECT s.id
        FROM spaces s
        WHERE s.is_active = TRUE
          AND NOT EXISTS (
              SELECT 1 FROM meal_combos mc
              WHERE mc.space_id = s.id AND mc.is_active = TRUE
          )
    LOOP
        thali_combo_id := gen_random_uuid();
        INSERT INTO meal_combos (id, space_id, name, description, is_active, created_at, updated_at)
        VALUES (
            thali_combo_id,
            target_space.id,
            'Standard Lunch Thali',
            'Daily lunch combo',
            TRUE,
            NOW(),
            NOW()
        );

        INSERT INTO meal_combo_items (id, combo_id, item_id, sort_order) VALUES
            (gen_random_uuid(), thali_combo_id, '22222222-2222-2222-2222-222222220001', 0),
            (gen_random_uuid(), thali_combo_id, '22222222-2222-2222-2222-222222220020', 1),
            (gen_random_uuid(), thali_combo_id, '22222222-2222-2222-2222-222222220010', 2),
            (gen_random_uuid(), thali_combo_id, '22222222-2222-2222-2222-222222220100', 3);

        dal_rice_combo_id := gen_random_uuid();
        INSERT INTO meal_combos (id, space_id, name, description, is_active, created_at, updated_at)
        VALUES (
            dal_rice_combo_id,
            target_space.id,
            'Dal Rice Combo',
            'Simple dal rice',
            TRUE,
            NOW(),
            NOW()
        );

        INSERT INTO meal_combo_items (id, combo_id, item_id, sort_order) VALUES
            (gen_random_uuid(), dal_rice_combo_id, '22222222-2222-2222-2222-222222220020', 0),
            (gen_random_uuid(), dal_rice_combo_id, '22222222-2222-2222-2222-222222220010', 1);
    END LOOP;
END $$;
