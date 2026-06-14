CREATE TABLE daily_menus (
    id           UUID         NOT NULL,
    space_id     UUID         NOT NULL,
    menu_date    DATE         NOT NULL,
    meal_type    VARCHAR(20)  NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    published_at TIMESTAMP,
    notes        TEXT,
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_daily_menus PRIMARY KEY (id),
    CONSTRAINT fk_daily_menus_space FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT chk_daily_menus_meal_type CHECK (meal_type IN ('BREAKFAST', 'LUNCH', 'DINNER')),
    CONSTRAINT chk_daily_menus_status CHECK (status IN ('DRAFT', 'PUBLISHED')),
    CONSTRAINT uq_daily_menus_space_date_type UNIQUE (space_id, menu_date, meal_type)
);

CREATE INDEX idx_daily_menus_space_date ON daily_menus (space_id, menu_date);

CREATE TABLE daily_menu_options (
    id             UUID         NOT NULL,
    daily_menu_id  UUID         NOT NULL,
    combo_id       UUID,
    label          VARCHAR(150) NOT NULL,
    sort_order     INTEGER      NOT NULL DEFAULT 0,
    is_available   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_daily_menu_options PRIMARY KEY (id),
    CONSTRAINT fk_daily_menu_options_menu FOREIGN KEY (daily_menu_id) REFERENCES daily_menus (id),
    CONSTRAINT fk_daily_menu_options_combo FOREIGN KEY (combo_id) REFERENCES meal_combos (id)
);

CREATE INDEX idx_daily_menu_options_menu_id ON daily_menu_options (daily_menu_id);
