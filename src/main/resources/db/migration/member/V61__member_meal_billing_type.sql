ALTER TABLE members
    ADD COLUMN meal_billing_type VARCHAR(20) NULL;

ALTER TABLE members
    ADD CONSTRAINT chk_members_meal_billing_type
        CHECK (meal_billing_type IS NULL OR meal_billing_type IN ('PAY_PER_MEAL', 'PREPAID_BALANCE'));
