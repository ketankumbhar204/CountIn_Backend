-- Subscription plan catalog (space-scoped commercial plans)
CREATE TABLE subscription_plans (
    id UUID PRIMARY KEY,
    space_id UUID NOT NULL REFERENCES spaces(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    meals_included INTEGER NOT NULL CHECK (meals_included > 0),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    currency_code VARCHAR(3) NOT NULL DEFAULT 'INR',
    validity_days INTEGER NOT NULL CHECK (validity_days > 0),
    carry_forward_unused BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_subscription_plans_space_active ON subscription_plans(space_id, is_active);

-- Customer activation requests (owner approves → wallet purchase)
CREATE TABLE subscription_activation_requests (
    id UUID PRIMARY KEY,
    space_id UUID NOT NULL REFERENCES spaces(id) ON DELETE CASCADE,
    member_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES subscription_plans(id),
    status VARCHAR(20) NOT NULL,
    payment_reference VARCHAR(120),
    customer_notes TEXT,
    owner_notes TEXT,
    resolved_by UUID REFERENCES users(id),
    resolved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_subscription_activation_status CHECK (
        status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')
    )
);

CREATE INDEX idx_subscription_activation_space_status ON subscription_activation_requests(space_id, status);
CREATE INDEX idx_subscription_activation_member ON subscription_activation_requests(member_id, status);

-- Customer requests to switch billing type (owner approves)
CREATE TABLE meal_billing_change_requests (
    id UUID PRIMARY KEY,
    space_id UUID NOT NULL REFERENCES spaces(id) ON DELETE CASCADE,
    member_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    requested_billing_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    customer_notes TEXT,
    owner_notes TEXT,
    resolved_by UUID REFERENCES users(id),
    resolved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_meal_billing_change_status CHECK (
        status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')
    )
);

CREATE INDEX idx_meal_billing_change_space_status ON meal_billing_change_requests(space_id, status);
CREATE INDEX idx_meal_billing_change_member ON meal_billing_change_requests(member_id, status);
