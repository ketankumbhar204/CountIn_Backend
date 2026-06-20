CREATE TABLE meal_poll_day_payments (
    id              UUID         NOT NULL,
    space_id        UUID         NOT NULL,
    member_id       UUID         NOT NULL,
    poll_date       DATE         NOT NULL,
    payment_choice  VARCHAR(20)  NOT NULL,
    payment_status  VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_meal_poll_day_payments PRIMARY KEY (id),
    CONSTRAINT fk_meal_poll_day_payments_space FOREIGN KEY (space_id) REFERENCES spaces (id),
    CONSTRAINT fk_meal_poll_day_payments_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT uq_meal_poll_day_payments UNIQUE (space_id, member_id, poll_date),
    CONSTRAINT chk_meal_poll_day_payments_choice CHECK (payment_choice IN ('PAY_NOW', 'PAY_LATER')),
    CONSTRAINT chk_meal_poll_day_payments_status CHECK (payment_status IN ('PENDING', 'PAID'))
);

CREATE INDEX idx_meal_poll_day_payments_space_date ON meal_poll_day_payments (space_id, poll_date);
