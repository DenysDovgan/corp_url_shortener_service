CREATE TABLE IF NOT EXISTS hash(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS url(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    hash VARCHAR(6) NOT NULL,
    url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1