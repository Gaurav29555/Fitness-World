CREATE SCHEMA IF NOT EXISTS billing;
CREATE TABLE IF NOT EXISTS billing.payment_records (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    plan_name VARCHAR(255) NOT NULL,
    provider VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    invoice_number VARCHAR(64) NOT NULL UNIQUE,
    amount NUMERIC(10,2) NOT NULL,
    currency VARCHAR(8) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    external_reference VARCHAR(255)
);
