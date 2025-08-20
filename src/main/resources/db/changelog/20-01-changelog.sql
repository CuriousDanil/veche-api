-- liquibase formatted sql

-- changeset danilpechkin:1755686067434-1
CREATE TABLE summary
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at    TIMESTAMP WITHOUT TIME ZONE,
    discussion_id UUID                        NOT NULL,
    content       VARCHAR(4000),
    CONSTRAINT pk_summary PRIMARY KEY (id)
);


-- changeset danilpechkin:1755686067434-6
ALTER TABLE summary
    ADD CONSTRAINT FK_SUMMARY_ON_DISCUSSION FOREIGN KEY (discussion_id) REFERENCES discussions (id);

