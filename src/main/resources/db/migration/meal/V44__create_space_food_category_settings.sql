CREATE TABLE space_food_category_settings (
    space_id    UUID      NOT NULL,
    category_id UUID      NOT NULL,
    is_enabled  BOOLEAN   NOT NULL DEFAULT TRUE,
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_space_food_category_settings PRIMARY KEY (space_id, category_id),
    CONSTRAINT fk_space_food_category_settings_space FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT fk_space_food_category_settings_category FOREIGN KEY (category_id) REFERENCES food_categories (id)
);
