-- liquibase formatted sql

-- changeset danilpechkin:1755187422950-3
ALTER TABLE discussions
    DROP CONSTRAINT fk_discussions_on_session;

-- changeset danilpechkin:1755187422950-1
CREATE TABLE voting_sessions
(
    id                     UUID                        NOT NULL,
    created_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at             TIMESTAMP WITHOUT TIME ZONE,
    first_round_starts_at  TIMESTAMP WITHOUT TIME ZONE,
    second_round_starts_at TIMESTAMP WITHOUT TIME ZONE,
    ends_at                TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_voting_sessions PRIMARY KEY (id)
);

-- changeset danilpechkin:1755187422950-2
ALTER TABLE discussions
    ADD CONSTRAINT FK_DISCUSSIONS_ON_SESSION FOREIGN KEY (session_id) REFERENCES voting_sessions (id);

-- changeset danilpechkin:1755187422950-4
DROP TABLE sessions CASCADE;

