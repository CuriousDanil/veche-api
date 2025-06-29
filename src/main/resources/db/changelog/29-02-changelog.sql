-- liquibase formatted sql

-- changeset danilpechkin:1751207825764-1
ALTER TABLE discussion_votes
    ADD CONSTRAINT uc_discussion_user UNIQUE (discussion_id, user_id);

