-- liquibase formatted sql

-- changeset danilpechkin:1755216953203-1
ALTER TABLE voting_sessions
    ADD name VARCHAR(255);
ALTER TABLE voting_sessions
    ADD party_id UUID;

-- changeset danilpechkin:1755216953203-2
ALTER TABLE voting_sessions
    ALTER COLUMN name SET NOT NULL;

-- changeset danilpechkin:1755216953203-4
ALTER TABLE voting_sessions
    ALTER COLUMN party_id SET NOT NULL;

-- changeset danilpechkin:1755216953203-5
ALTER TABLE voting_sessions
    ADD CONSTRAINT FK_VOTING_SESSIONS_ON_PARTY FOREIGN KEY (party_id) REFERENCES parties (id);

