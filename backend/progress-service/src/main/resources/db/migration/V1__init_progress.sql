CREATE SCHEMA IF NOT EXISTS progress;
CREATE TABLE IF NOT EXISTS progress.progress_logs (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    logged_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    weight_kg NUMERIC(8,2),
    body_fat NUMERIC(5,2),
    calories NUMERIC(8,2),
    protein_grams NUMERIC(8,2),
    squat_kg NUMERIC(8,2),
    bench_kg NUMERIC(8,2),
    compliance_score INTEGER
);
