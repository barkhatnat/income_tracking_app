-- liquibase formatted sql

-- changeset Mvideo:1721211047635-1
CREATE TABLE account
(
    id         UUID        NOT NULL,
    title      VARCHAR(32) NOT NULL,
    balance    DECIMAL     NOT NULL,
    user_id    UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_account PRIMARY KEY (id)
);

-- changeset Mvideo:1721211047635-2
CREATE TABLE category
(
    id            UUID        NOT NULL,
    title         VARCHAR(32) NOT NULL,
    category_type BOOLEAN     NOT NULL,
    user_id       UUID,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

-- changeset Mvideo:1721211047635-3
CREATE TABLE operation
(
    id            UUID    NOT NULL,
    amount        DECIMAL NOT NULL,
    date_purchase TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    note          VARCHAR(512),
    category_id   UUID,
    account_id    UUID,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_operation PRIMARY KEY (id)
);

-- changeset Mvideo:1721211047635-4
CREATE TABLE users
(
    id         UUID         NOT NULL,
    username   VARCHAR(64)  NOT NULL,
    password   VARCHAR(256) NOT NULL,
    email      VARCHAR(128) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    role       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset Mvideo:1721211047635-5
ALTER TABLE account
    ADD CONSTRAINT FK_ACCOUNT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset Mvideo:1721211047635-6
ALTER TABLE category
    ADD CONSTRAINT FK_CATEGORY_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset Mvideo:1721211047635-7
ALTER TABLE operation
    ADD CONSTRAINT FK_OPERATION_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

-- changeset Mvideo:1721211047635-8
ALTER TABLE operation
    ADD CONSTRAINT FK_OPERATION_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

