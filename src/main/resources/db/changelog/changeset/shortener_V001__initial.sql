CREATE TABLE url
(
    hash VARCHAR(6) NOT NULL PRIMARY KEY,
    url VARCHAR(4096) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hash
(
    hash VARCHAR(6) NOT NUll PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq;