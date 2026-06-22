ALTER TABLE member_meal_balance_ledger
    ADD COLUMN paid_amount DECIMAL(12, 2);

ALTER TABLE member_meal_balance_ledger
    ADD CONSTRAINT chk_member_meal_balance_ledger_paid_amount
        CHECK (paid_amount IS NULL OR paid_amount >= 0);
