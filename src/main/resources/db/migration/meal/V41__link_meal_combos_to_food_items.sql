-- Point meal_combo_items at food_items instead of legacy meal_items
ALTER TABLE meal_combo_items DROP CONSTRAINT IF EXISTS fk_meal_combo_items_item;

DROP TABLE IF EXISTS meal_items;

ALTER TABLE meal_combo_items
    ADD CONSTRAINT fk_meal_combo_items_food_item
        FOREIGN KEY (item_id) REFERENCES food_items (id);
