CREATE TABLE food_categories (
    id          UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    scope       VARCHAR(10)  NOT NULL,
    space_id    UUID,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_food_categories PRIMARY KEY (id),
    CONSTRAINT fk_food_categories_space FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT chk_food_categories_scope CHECK (scope IN ('GLOBAL', 'SPACE'))
);

CREATE INDEX idx_food_categories_scope ON food_categories (scope);
CREATE INDEX idx_food_categories_space_id ON food_categories (space_id);

CREATE TABLE food_items (
    id           UUID         NOT NULL,
    category_id  UUID         NOT NULL,
    name         VARCHAR(100) NOT NULL,
    scope        VARCHAR(10)  NOT NULL,
    space_id     UUID,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    is_custom    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_food_items PRIMARY KEY (id),
    CONSTRAINT fk_food_items_category FOREIGN KEY (category_id) REFERENCES food_categories (id),
    CONSTRAINT fk_food_items_space FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT chk_food_items_scope CHECK (scope IN ('GLOBAL', 'SPACE'))
);

CREATE INDEX idx_food_items_category_id ON food_items (category_id);
CREATE INDEX idx_food_items_scope ON food_items (scope);
CREATE INDEX idx_food_items_space_id ON food_items (space_id);

CREATE TABLE space_food_item_settings (
    space_id   UUID      NOT NULL,
    item_id    UUID      NOT NULL,
    is_enabled BOOLEAN   NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_space_food_item_settings PRIMARY KEY (space_id, item_id),
    CONSTRAINT fk_space_food_item_settings_space FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT fk_space_food_item_settings_item FOREIGN KEY (item_id) REFERENCES food_items (id)
);
