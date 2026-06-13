-- Phase 4.3a: member gender and category

ALTER TABLE members
    ADD COLUMN gender VARCHAR(20),
    ADD COLUMN member_category VARCHAR(30);

ALTER TABLE members
    DROP CONSTRAINT chk_members_occupancy_status;

ALTER TABLE members
    ADD CONSTRAINT chk_members_occupancy_status
        CHECK (occupancy_status IN ('ALLOCATED', 'VACATED', 'RESERVED'));

ALTER TABLE members
    ADD CONSTRAINT chk_members_gender
        CHECK (gender IS NULL OR gender IN ('MALE', 'FEMALE', 'OTHER', 'UNSPECIFIED'));

ALTER TABLE members
    ADD CONSTRAINT chk_members_member_category
        CHECK (member_category IS NULL OR member_category IN (
            'STUDENT', 'WORKING_PROFESSIONAL', 'FAMILY', 'GUEST', 'INTERN'
        ));
