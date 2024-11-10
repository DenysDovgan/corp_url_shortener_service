CREATE TABLE url (
    hash CHAR(6) PRIMARY KEY,
    url VARCHAR(4096) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE hash (
    hash CHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START 1
    INCREMENT 1
    MINVALUE 1
    CACHE 10000;