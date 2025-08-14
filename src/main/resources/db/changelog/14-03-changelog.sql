-- liquibase formatted sql

-- changeset danilpechkin:1755190967733-1
ALTER TABLE voting_sessions
    ADD end_time TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE voting_sessions
    ADD first_round_start_time TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE voting_sessions
    ADD second_round_start_time TIMESTAMP WITHOUT TIME ZONE;

-- changeset danilpechkin:1755190967733-5
ALTER TABLE voting_sessions
    DROP COLUMN ends_at;
ALTER TABLE voting_sessions
    DROP COLUMN first_round_starts_at;
ALTER TABLE voting_sessions
    DROP COLUMN second_round_starts_at;

