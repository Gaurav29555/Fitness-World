CREATE SCHEMA IF NOT EXISTS profile;
CREATE TABLE IF NOT EXISTS profile.member_profiles (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    region VARCHAR(100) NOT NULL,
    preferred_language VARCHAR(16) NOT NULL,
    goal VARCHAR(255) NOT NULL,
    age INTEGER,
    height_cm NUMERIC(8,2),
    weight_kg NUMERIC(8,2)
);
INSERT INTO profile.member_profiles (id, full_name, region, preferred_language, goal, age, height_cm, weight_kg)
VALUES (3, 'Demo Member', 'Maharashtra', 'mr', 'Fat loss', 28, 172, 78)
ON CONFLICT (id) DO NOTHING;
