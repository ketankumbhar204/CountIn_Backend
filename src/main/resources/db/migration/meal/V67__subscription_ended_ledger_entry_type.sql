-- Allow ENDED ledger entries when a subscription is manually ended.
ALTER TABLE member_meal_balance_ledger
    DROP CONSTRAINT chk_member_meal_balance_ledger_entry_type;

ALTER TABLE member_meal_balance_ledger
    ADD CONSTRAINT chk_member_meal_balance_ledger_entry_type
        CHECK (entry_type IN ('PURCHASE', 'DEBIT', 'ADJUSTMENT', 'ENDED'));
