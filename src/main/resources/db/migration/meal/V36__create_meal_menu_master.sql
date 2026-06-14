CREATE TABLE meal_items (
    id          UUID         NOT NULL,
    space_id    UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_items PRIMARY KEY (id),
    CONSTRAINT fk_meal_items_space FOREIGN KEY (space_id) REFERENCES spaces (id)
);

CREATE INDEX idx_meal_items_space_id ON meal_items (space_id);

CREATE TABLE meal_combos (
    id          UUID         NOT NULL,
    space_id    UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_combos PRIMARY KEY (id),
    CONSTRAINT fk_meal_combos_space FOREIGN KEY (space_id) REFERENCES spaces (id)
);

CREATE INDEX idx_meal_combos_space_id ON meal_combos (space_id);

CREATE TABLE meal_combo_items (
    id          UUID    NOT NULL,
    combo_id    UUID    NOT NULL,
    item_id     UUID    NOT NULL,
    sort_order  INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT pk_meal_combo_items PRIMARY KEY (id),
    CONSTRAINT fk_meal_combo_items_combo FOREIGN KEY (combo_id) REFERENCES meal_combos (id),
    CONSTRAINT fk_meal_combo_items_item FOREIGN KEY (item_id) REFERENCES meal_items (id),
    CONSTRAINT uq_meal_combo_items_combo_item UNIQUE (combo_id, item_id)
);
