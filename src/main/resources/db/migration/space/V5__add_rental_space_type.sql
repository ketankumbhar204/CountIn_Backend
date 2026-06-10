ALTER TABLE spaces DROP CONSTRAINT chk_spaces_type;

ALTER TABLE spaces
    ADD CONSTRAINT chk_spaces_type
        CHECK (type IN ('PG', 'MESS', 'HOSTEL', 'CO_LIVING', 'RENTAL'));
