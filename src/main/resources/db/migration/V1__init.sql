CREATE TABLE comments
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    subject       VARCHAR(4000),
    file_name     VARCHAR(255),
    file_url      VARCHAR(500),
    file_size     BIGINT,
    discussion_id UUID                        NOT NULL,
    creator_id    UUID                        NOT NULL,
    type          VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

CREATE TABLE companies
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_companies PRIMARY KEY (id)
);

CREATE TABLE discussion_votes
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    discussion_id UUID                        NOT NULL,
    user_id       UUID                        NOT NULL,
    vote_value    VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_discussion_votes PRIMARY KEY (id)
);

CREATE TABLE discussions
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    subject    VARCHAR(200)                NOT NULL,
    content    VARCHAR(4000)               NOT NULL,
    file_url   VARCHAR(500),
    file_name  VARCHAR(255),
    file_size  BIGINT,
    party_id   UUID                        NOT NULL,
    creator_id UUID                        NOT NULL,
    status     VARCHAR(255)                NOT NULL,
    session_id UUID,
    CONSTRAINT pk_discussions PRIMARY KEY (id)
);

CREATE TABLE parties
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    company_id UUID                        NOT NULL,
    CONSTRAINT pk_parties PRIMARY KEY (id)
);

CREATE TABLE parties_users
(
    party_id UUID NOT NULL,
    user_id  UUID NOT NULL,
    CONSTRAINT pk_parties_users PRIMARY KEY (party_id, user_id)
);

CREATE TABLE sessions
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_sessions PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                          UUID                        NOT NULL,
    created_at                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name                        VARCHAR(255)                NOT NULL,
    email                       VARCHAR(255)                NOT NULL,
    password_hash               VARCHAR(255)                NOT NULL,
    bio                         VARCHAR(255),
    is_able_to_post_discussions BOOLEAN,
    is_able_to_manage_sessions  BOOLEAN,
    is_able_to_manage_users     BOOLEAN,
    company_id                  UUID                        NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE INDEX idx_discussion_votes_discussion_user_createdat ON discussion_votes (discussion_id, user_id, created_at DESC);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_DISCUSSION FOREIGN KEY (discussion_id) REFERENCES discussions (id);

ALTER TABLE discussions
    ADD CONSTRAINT FK_DISCUSSIONS_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);

ALTER TABLE discussions
    ADD CONSTRAINT FK_DISCUSSIONS_ON_PARTY FOREIGN KEY (party_id) REFERENCES parties (id);

ALTER TABLE discussions
    ADD CONSTRAINT FK_DISCUSSIONS_ON_SESSION FOREIGN KEY (session_id) REFERENCES sessions (id);

ALTER TABLE discussion_votes
    ADD CONSTRAINT FK_DISCUSSION_VOTES_ON_DISCUSSION FOREIGN KEY (discussion_id) REFERENCES discussions (id);

ALTER TABLE discussion_votes
    ADD CONSTRAINT FK_DISCUSSION_VOTES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE parties
    ADD CONSTRAINT FK_PARTIES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES companies (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES companies (id);

ALTER TABLE parties_users
    ADD CONSTRAINT fk_paruse_on_party_entity FOREIGN KEY (party_id) REFERENCES parties (id);

ALTER TABLE parties_users
    ADD CONSTRAINT fk_paruse_on_user_entity FOREIGN KEY (user_id) REFERENCES users (id);