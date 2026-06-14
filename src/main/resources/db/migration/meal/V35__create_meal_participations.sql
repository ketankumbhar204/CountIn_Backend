CREATE TABLE meal_participations (
    id                  UUID         NOT NULL,
    space_id            UUID         NOT NULL,
    member_id           UUID         NOT NULL,
    meal_plan_id        UUID         NOT NULL,
    status              VARCHAR(20)  NOT NULL,
    effective_from      DATE         NOT NULL,
    effective_to        DATE,
    source_occupancy_id UUID,
    entitlement_id    UUID,
    stopped_at          TIMESTAMP,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_participations PRIMARY KEY (id),
    CONSTRAINT fk_meal_participations_space FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT fk_meal_participations_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_meal_participations_plan FOREIGN KEY (meal_plan_id) REFERENCES meal_plans (id),
    CONSTRAINT fk_meal_participations_occupancy FOREIGN KEY (source_occupancy_id) REFERENCES occupancies (id),
    CONSTRAINT chk_meal_participations_status CHECK (status IN ('ACTIVE', 'PAUSED', 'STOPPED'))
);

CREATE INDEX idx_meal_participations_space_id ON meal_participations (space_id);
CREATE INDEX idx_meal_participations_member_id ON meal_participations (member_id);
CREATE INDEX idx_meal_participations_status ON meal_participations (space_id, status);

CREATE UNIQUE INDEX uq_meal_participations_member_active
    ON meal_participations (space_id, member_id)
    WHERE status = 'ACTIVE';

CREATE TABLE meal_participation_history (
    id                UUID         NOT NULL,
    participation_id  UUID         NOT NULL,
    space_id          UUID         NOT NULL,
    action            VARCHAR(30)  NOT NULL,
    old_value         TEXT,
    new_value         TEXT,
    changed_by        UUID         NOT NULL,
    changed_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_participation_history PRIMARY KEY (id),
    CONSTRAINT fk_meal_participation_history_participation
        FOREIGN KEY (participation_id) REFERENCES meal_participations (id),
    CONSTRAINT fk_meal_participation_history_space FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT fk_meal_participation_history_user FOREIGN KEY (changed_by) REFERENCES users (id),
    CONSTRAINT chk_meal_participation_history_action CHECK (action IN (
        'CREATED', 'PLAN_CHANGED', 'STATUS_CHANGED', 'STOPPED'
    ))
);

CREATE INDEX idx_meal_participation_history_participation
    ON meal_participation_history (participation_id);
