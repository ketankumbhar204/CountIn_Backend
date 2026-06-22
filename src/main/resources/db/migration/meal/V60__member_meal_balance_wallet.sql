CREATE TABLE member_meal_balances (
    id UUID PRIMARY KEY,
    space_id UUID NOT NULL REFERENCES spaces (id),
    member_id UUID NOT NULL REFERENCES members (id),
    balance DECIMAL(12, 2) NOT NULL DEFAULT 0,
    unit VARCHAR(10) NOT NULL,
    currency_code VARCHAR(3) NOT NULL DEFAULT 'INR',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_member_meal_balances_space_member UNIQUE (space_id, member_id),
    CONSTRAINT chk_member_meal_balances_balance CHECK (balance >= 0),
    CONSTRAINT chk_member_meal_balances_unit CHECK (unit IN ('MEALS', 'CURRENCY'))
);

CREATE INDEX idx_member_meal_balances_space_id ON member_meal_balances (space_id);

CREATE TABLE member_meal_balance_ledger (
    id UUID PRIMARY KEY,
    balance_id UUID NOT NULL REFERENCES member_meal_balances (id),
    entry_type VARCHAR(20) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    requested_amount DECIMAL(12, 2),
    meal_count INTEGER,
    poll_id UUID REFERENCES meal_polls (id),
    poll_date DATE,
    meal_type VARCHAR(20),
    idempotency_key VARCHAR(160) NOT NULL,
    remarks TEXT,
    created_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_member_meal_balance_ledger_idempotency UNIQUE (idempotency_key),
    CONSTRAINT chk_member_meal_balance_ledger_entry_type
        CHECK (entry_type IN ('PURCHASE', 'DEBIT', 'ADJUSTMENT')),
    CONSTRAINT chk_member_meal_balance_ledger_amount CHECK (amount >= 0)
);

CREATE INDEX idx_member_meal_balance_ledger_balance_id ON member_meal_balance_ledger (balance_id);
CREATE INDEX idx_member_meal_balance_ledger_poll_date ON member_meal_balance_ledger (poll_date);
