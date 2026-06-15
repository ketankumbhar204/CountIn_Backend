-- Allow multiple operator-created CUSTOM plans per space while keeping preset codes unique.
ALTER TABLE meal_plans DROP CONSTRAINT IF EXISTS uq_meal_plans_space_code;

CREATE UNIQUE INDEX uq_meal_plans_space_preset_code
    ON meal_plans (space_id, code)
    WHERE code IN ('NONE', 'BREAKFAST', 'LUNCH', 'DINNER', 'FULL');

CREATE UNIQUE INDEX uq_meal_plans_space_custom_name
    ON meal_plans (space_id, name)
    WHERE code = 'CUSTOM' AND is_active = TRUE;
