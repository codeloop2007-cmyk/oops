-- Migration: Embed address into app_user and remove legacy address table
-- 1. Ensure address columns exist on app_user
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS label VARCHAR(255);
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS address_line1 VARCHAR(255);
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS address_line2 VARCHAR(255);
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS city VARCHAR(255);
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS state VARCHAR(255);
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS postal_code VARCHAR(255);
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS country VARCHAR(255);
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS latitude NUMERIC(9, 6);
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS longitude NUMERIC(9, 6);
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS is_default BOOLEAN;

-- 2. Migrate existing records from legacy address table into app_user
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'address') THEN
        UPDATE app_user u
        SET label = a.label,
            address_line1 = a.address_line1,
            address_line2 = a.address_line2,
            city = a.city,
            state = a.state,
            postal_code = a.postal_code,
            country = a.country,
            latitude = a.latitude,
            longitude = a.longitude,
            is_default = a.is_default
        FROM (
            SELECT DISTINCT ON (user_id)
                user_id, label, address_line1, address_line2, city, state, postal_code, country, latitude, longitude, is_default
            FROM address
            ORDER BY user_id, is_default DESC, address_id ASC
        ) a
        WHERE u.user_id = a.user_id AND u.address_line1 IS NULL;

        -- 3. Drop legacy address table safely
        DROP TABLE address CASCADE;
    END IF;
END $$;

