ALTER TABLE spaces
    ADD COLUMN meal_billing_type VARCHAR(20) NOT NULL DEFAULT 'PAY_PER_MEAL';

ALTER TABLE spaces
    ADD COLUMN prepaid_balance_unit VARCHAR(10);

ALTER TABLE spaces
    ADD COLUMN prepaid_fallback_to_pay_per_meal BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE spaces
    ADD CONSTRAINT chk_spaces_meal_billing_type
        CHECK (meal_billing_type IN ('PAY_PER_MEAL', 'PREPAID_BALANCE'));

ALTER TABLE spaces
    ADD CONSTRAINT chk_spaces_prepaid_balance_unit
        CHECK (
            prepaid_balance_unit IS NULL
            OR prepaid_balance_unit IN ('MEALS', 'CURRENCY')
        );
