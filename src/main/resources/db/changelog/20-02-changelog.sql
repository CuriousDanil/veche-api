-- liquibase formatted sql

-- changeset danilpechkin:1755705480184-1
ALTER TABLE summary
    ALTER COLUMN content TYPE VARCHAR(400000) USING (content::VARCHAR(400000));

