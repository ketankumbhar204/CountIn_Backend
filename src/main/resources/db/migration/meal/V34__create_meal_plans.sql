CREATE TABLE meal_plans (
    id                  UUID         NOT NULL,
    space_id            UUID         NOT NULL,
    code                VARCHAR(20)  NOT NULL,
    name                VARCHAR(100) NOT NULL,
    breakfast_included  BOOLEAN      NOT NULL DEFAULT FALSE,
    lunch_included      BOOLEAN      NOT NULL DEFAULT FALSE,
    dinner_included     BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    sort_order          INTEGER      NOT NULL DEFAULT 0,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_plans PRIMARY KEY (id),
    CONSTRAINT fk_meal_plans_space FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT chk_meal_plans_code CHECK (code IN (
        'NONE', 'BREAKFAST', 'LUNCH', 'DINNER', 'FULL', 'CUSTOM'
    )),
    CONSTRAINT uq_meal_plans_space_code UNIQUE (space_id, code)
);

CREATE INDEX idx_meal_plans_space_id ON meal_plans (space_id);
