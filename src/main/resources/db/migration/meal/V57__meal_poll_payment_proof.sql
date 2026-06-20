ALTER TABLE meal_poll_day_payments
    ADD COLUMN proof_image_url   TEXT,
    ADD COLUMN proof_submitted_at TIMESTAMP,
    ADD COLUMN proof_reviewed_at  TIMESTAMP,
    ADD COLUMN proof_reviewed_by  UUID,
    ADD COLUMN rejection_reason     TEXT;

UPDATE meal_poll_day_payments
SET payment_choice = 'MARK_AS_PAID'
WHERE payment_choice = 'PAY_NOW';

ALTER TABLE meal_poll_day_payments DROP CONSTRAINT chk_meal_poll_day_payments_choice;
ALTER TABLE meal_poll_day_payments DROP CONSTRAINT chk_meal_poll_day_payments_status;

ALTER TABLE meal_poll_day_payments
    ADD CONSTRAINT chk_meal_poll_day_payments_choice
        CHECK (payment_choice IN ('MARK_AS_PAID', 'PAY_LATER'));

ALTER TABLE meal_poll_day_payments
    ADD CONSTRAINT chk_meal_poll_day_payments_status
        CHECK (payment_status IN ('PENDING', 'PENDING_APPROVAL', 'PAID', 'REJECTED'));
