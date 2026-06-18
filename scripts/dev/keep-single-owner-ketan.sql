-- Keep only Ketan (9876543210) as user; reassign spaces owned by other test owner.
BEGIN;

UPDATE spaces
SET owner_id = '92743bf1-4658-4f7d-880b-e8eb1fed4e69',
    updated_at = NOW()
WHERE owner_id = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

UPDATE members
SET membership_id = NULL,
    user_id = NULL,
    updated_at = NOW()
WHERE user_id = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

UPDATE invitations
SET invited_by_user_id = '92743bf1-4658-4f7d-880b-e8eb1fed4e69',
    updated_at = NOW()
WHERE invited_by_user_id = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

UPDATE member_notes
SET created_by = '92743bf1-4658-4f7d-880b-e8eb1fed4e69'
WHERE created_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

UPDATE member_history
SET changed_by = '92743bf1-4658-4f7d-880b-e8eb1fed4e69'
WHERE changed_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

UPDATE meal_participation_history
SET changed_by = '92743bf1-4658-4f7d-880b-e8eb1fed4e69'
WHERE changed_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

UPDATE occupancies
SET allocated_by = CASE
        WHEN allocated_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353' THEN '92743bf1-4658-4f7d-880b-e8eb1fed4e69'
        ELSE allocated_by
    END,
    vacated_by = CASE
        WHEN vacated_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353' THEN '92743bf1-4658-4f7d-880b-e8eb1fed4e69'
        ELSE vacated_by
    END,
    created_by = CASE
        WHEN created_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353' THEN '92743bf1-4658-4f7d-880b-e8eb1fed4e69'
        ELSE created_by
    END,
    updated_by = CASE
        WHEN updated_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353' THEN '92743bf1-4658-4f7d-880b-e8eb1fed4e69'
        ELSE updated_by
    END
WHERE allocated_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353'
   OR vacated_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353'
   OR created_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353'
   OR updated_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

UPDATE occupancy_history
SET performed_by = '92743bf1-4658-4f7d-880b-e8eb1fed4e69'
WHERE performed_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

UPDATE accommodation_setup_idempotency
SET created_by = '92743bf1-4658-4f7d-880b-e8eb1fed4e69'
WHERE created_by = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

DELETE FROM space_memberships
WHERE user_id = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

DELETE FROM users
WHERE id = 'fc9c6cd4-b538-47e5-b53b-f58b220c7353';

COMMIT;
