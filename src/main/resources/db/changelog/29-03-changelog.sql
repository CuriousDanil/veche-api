-- liquibase formatted sql

-- changeset danilpechkin:1751212918783-1
CREATE TABLE pending_actions
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at    TIMESTAMP WITHOUT TIME ZONE,
    discussion_id UUID                        NOT NULL,
    action_type   VARCHAR(255),
    payload       JSONB                       NOT NULL,
    executed      BOOLEAN                     NOT NULL,
    CONSTRAINT pk_pending_actions PRIMARY KEY (id)
);

-- changeset danilpechkin:1751212918783-3
ALTER TABLE pending_actions
    ADD CONSTRAINT FK_PENDING_ACTIONS_ON_DISCUSSION FOREIGN KEY (discussion_id) REFERENCES discussions (id);

