ALTER TABLE daily_menu_entries
    DROP CONSTRAINT IF EXISTS chk_daily_menu_entries_type;

ALTER TABLE daily_menu_entries
    ADD CONSTRAINT chk_daily_menu_entries_type CHECK (entry_type IN ('COMBO', 'ITEM', 'PACKAGE'));

CREATE TABLE daily_menu_package_items (
    id         UUID      NOT NULL,
    entry_id   UUID      NOT NULL,
    item_id    UUID      NOT NULL,
    sort_order INTEGER   NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_daily_menu_package_items PRIMARY KEY (id),
    CONSTRAINT fk_daily_menu_package_items_entry FOREIGN KEY (entry_id) REFERENCES daily_menu_entries (id) ON DELETE CASCADE,
    CONSTRAINT fk_daily_menu_package_items_item FOREIGN KEY (item_id) REFERENCES food_items (id)
);

CREATE INDEX idx_daily_menu_package_items_entry_id ON daily_menu_package_items (entry_id);
