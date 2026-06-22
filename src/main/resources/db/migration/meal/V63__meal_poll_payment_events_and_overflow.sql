ALTER TABLE meal_poll_day_payments
    ADD COLUMN prepaid_overflow_amount DECIMAL(12, 2) NULL,
    ADD COLUMN prepaid_debited_amount DECIMAL(12, 2) NULL,
    ADD COLUMN prepaid_overflow_payment BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE meal_poll_payment_events (
    id UUID PRIMARY KEY,
    space_id UUID NOT NULL REFERENCES spaces(id),
    member_id UUID NOT NULL REFERENCES members(id),
    poll_date DATE NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    payment_status VARCHAR(20) NULL,
    payment_choice VARCHAR(20) NULL,
    amount DECIMAL(12, 2) NULL,
    remarks TEXT NULL,
    actor_id UUID NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_meal_poll_payment_events_member_date
    ON meal_poll_payment_events (space_id, member_id, poll_date, created_at);

ALTER TABLE meal_poll_payment_events
    ADD CONSTRAINT chk_meal_poll_payment_events_type
        CHECK (event_type IN (
            'PAY_LATER_SELECTED',
            'MARK_AS_PAID_SELECTED',
            'PROOF_SUBMITTED',
            'APPROVED',
            'REJECTED',
            'REMINDER_SENT',
            'PREPAID_OVERFLOW_PAY_LATER'
        ));
