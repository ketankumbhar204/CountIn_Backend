CREATE TABLE meal_polls (
    id              UUID         NOT NULL,
    space_id        UUID         NOT NULL,
    daily_menu_id   UUID         NOT NULL,
    meal_type       VARCHAR(20)  NOT NULL,
    poll_date       DATE         NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    opened_at       TIMESTAMP,
    closed_at       TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_polls PRIMARY KEY (id),
    CONSTRAINT fk_meal_polls_space FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT fk_meal_polls_daily_menu FOREIGN KEY (daily_menu_id) REFERENCES daily_menus (id),
    CONSTRAINT chk_meal_polls_status CHECK (status IN ('OPEN', 'CLOSED')),
    CONSTRAINT chk_meal_polls_meal_type CHECK (meal_type IN ('BREAKFAST', 'LUNCH', 'DINNER'))
);

CREATE UNIQUE INDEX uq_meal_polls_space_date_type ON meal_polls (space_id, poll_date, meal_type);
CREATE INDEX idx_meal_polls_space_date ON meal_polls (space_id, poll_date);

CREATE TABLE meal_poll_options (
    id                    UUID         NOT NULL,
    poll_id               UUID         NOT NULL,
    option_type           VARCHAR(20)  NOT NULL,
    daily_menu_entry_id   UUID,
    sort_order            INT          NOT NULL,
    label                 VARCHAR(200) NOT NULL,
    detail                TEXT,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_poll_options PRIMARY KEY (id),
    CONSTRAINT fk_meal_poll_options_poll FOREIGN KEY (poll_id) REFERENCES meal_polls (id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_poll_options_entry FOREIGN KEY (daily_menu_entry_id) REFERENCES daily_menu_entries (id),
    CONSTRAINT chk_meal_poll_options_type CHECK (option_type IN ('MENU_ENTRY', 'NOT_AVAILABLE'))
);

CREATE INDEX idx_meal_poll_options_poll ON meal_poll_options (poll_id);

CREATE TABLE meal_poll_responses (
    id                  UUID         NOT NULL,
    poll_id             UUID         NOT NULL,
    member_id           UUID         NOT NULL,
    selected_option_id  UUID         NOT NULL,
    responded_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    source              VARCHAR(20)  NOT NULL DEFAULT 'APP',
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_poll_responses PRIMARY KEY (id),
    CONSTRAINT fk_meal_poll_responses_poll FOREIGN KEY (poll_id) REFERENCES meal_polls (id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_poll_responses_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_meal_poll_responses_option FOREIGN KEY (selected_option_id) REFERENCES meal_poll_options (id),
    CONSTRAINT chk_meal_poll_responses_source CHECK (source IN ('APP', 'WHATSAPP')),
    CONSTRAINT uq_meal_poll_responses_poll_member UNIQUE (poll_id, member_id)
);

CREATE INDEX idx_meal_poll_responses_poll ON meal_poll_responses (poll_id);
