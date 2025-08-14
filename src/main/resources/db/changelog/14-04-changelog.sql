-- liquibase formatted sql

-- changeset danilpechkin:1755191473825-7
ALTER TABLE voting_sessions
    DROP COLUMN status;

-- changeset danilpechkin:1755191473825-2
ALTER TABLE voting_sessions
    ADD status VARCHAR(255);

