-- Phase 4.3a: space gender policy

ALTER TABLE spaces
    ADD COLUMN gender_policy VARCHAR(20);

ALTER TABLE spaces
    ADD CONSTRAINT chk_spaces_gender_policy
        CHECK (gender_policy IS NULL OR gender_policy IN ('MALE', 'FEMALE', 'MIXED'));
