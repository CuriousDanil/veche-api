-- liquibase formatted sql

-- changeset danilpechkin:1755765267945-3
CREATE TABLE invitations
(
    id              UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at      TIMESTAMP WITHOUT TIME ZONE,
    token           VARCHAR(64)                 NOT NULL,
    party_id        UUID                        NOT NULL,
    suggested_name  VARCHAR(50),
    suggested_bio   VARCHAR(400),
    suggested_email VARCHAR(255),
    expires_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    creator_id      UUID                        NOT NULL,
    used_at         TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_invitations PRIMARY KEY (id)
);

-- changeset danilpechkin:1755765267945-4
ALTER TABLE invitations
    ADD CONSTRAINT uc_invitations_token UNIQUE (token);

-- changeset danilpechkin:1755765267945-5
CREATE UNIQUE INDEX idx_inv_token ON invitations (token);

-- changeset danilpechkin:1755765267945-6
ALTER TABLE invitations
    ADD CONSTRAINT FK_INVITATIONS_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);

-- changeset danilpechkin:1755765267945-7
ALTER TABLE invitations
    ADD CONSTRAINT FK_INVITATIONS_ON_PARTY FOREIGN KEY (party_id) REFERENCES parties (id);

-- changeset danilpechkin:1755765267945-1
ALTER TABLE users
    ALTER COLUMN bio TYPE VARCHAR(400) USING (bio::VARCHAR(400));

-- changeset danilpechkin:1755765267945-2
ALTER TABLE users
    ALTER COLUMN name TYPE VARCHAR(50) USING (name::VARCHAR(50));

