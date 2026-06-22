ALTER TABLE member_meal_balances
    ADD COLUMN subscription_ended_at TIMESTAMP,
    ADD COLUMN subscription_ended_by UUID;
