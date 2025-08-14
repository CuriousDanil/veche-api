-- liquibase formatted sql

-- changeset danilpechkin:1755187694029-1
ALTER TABLE voting_sessions
    ADD status SMALLINT;

