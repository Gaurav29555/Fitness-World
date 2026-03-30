CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE IF NOT EXISTS auth.user_credentials (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    member_id BIGINT NOT NULL,
    preferred_language VARCHAR(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS auth.user_roles (
    credential_id BIGINT NOT NULL REFERENCES auth.user_credentials (id) ON DELETE CASCADE,
    role_name VARCHAR(64) NOT NULL,
    CONSTRAINT uq_user_role UNIQUE (credential_id, role_name)
);

INSERT INTO auth.user_credentials (email, password_hash, member_id, preferred_language)
VALUES
    ('admin@fitnessworld.local', '$2a$10$2IEVU2i0UJ0UxcN0FHQ0V.OmDRbugzuDUFcPRl1BDpRP70dNDO7x6', 1, 'en'),
    ('trainer@fitnessworld.local', '$2a$10$2IEVU2i0UJ0UxcN0FHQ0V.OmDRbugzuDUFcPRl1BDpRP70dNDO7x6', 2, 'en'),
    ('member@fitnessworld.local', '$2a$10$2IEVU2i0UJ0UxcN0FHQ0V.OmDRbugzuDUFcPRl1BDpRP70dNDO7x6', 3, 'mr')
ON CONFLICT (email) DO NOTHING;

INSERT INTO auth.user_roles (credential_id, role_name)
SELECT id, 'ROLE_ADMIN' FROM auth.user_credentials WHERE email = 'admin@fitnessworld.local'
ON CONFLICT DO NOTHING;

INSERT INTO auth.user_roles (credential_id, role_name)
SELECT id, 'ROLE_TRAINER' FROM auth.user_credentials WHERE email = 'trainer@fitnessworld.local'
ON CONFLICT DO NOTHING;

INSERT INTO auth.user_roles (credential_id, role_name)
SELECT id, 'ROLE_MEMBER' FROM auth.user_credentials WHERE email = 'member@fitnessworld.local'
ON CONFLICT DO NOTHING;
