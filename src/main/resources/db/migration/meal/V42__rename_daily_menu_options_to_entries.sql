ALTER TABLE daily_menu_options RENAME TO daily_menu_entries;

ALTER TABLE daily_menu_entries
    ADD COLUMN entry_type VARCHAR(10) NOT NULL DEFAULT 'COMBO',
    ADD COLUMN item_id UUID,
    ADD CONSTRAINT fk_daily_menu_entries_item FOREIGN KEY (item_id) REFERENCES food_items (id),
    ADD CONSTRAINT chk_daily_menu_entries_type CHECK (entry_type IN ('COMBO', 'ITEM'));

UPDATE daily_menu_entries
SET entry_type = CASE WHEN combo_id IS NOT NULL THEN 'COMBO' ELSE 'ITEM' END;
