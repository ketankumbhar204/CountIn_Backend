-- Support multiple combo selections with quantity per member (Mess spaces).

ALTER TABLE meal_poll_responses
    ADD COLUMN quantity INT NOT NULL DEFAULT 1;

ALTER TABLE meal_poll_responses
    ADD CONSTRAINT chk_meal_poll_responses_quantity CHECK (quantity >= 0);

ALTER TABLE meal_poll_responses
    DROP CONSTRAINT uq_meal_poll_responses_poll_member;

CREATE UNIQUE INDEX uq_meal_poll_responses_poll_member_option
    ON meal_poll_responses (poll_id, member_id, selected_option_id);
