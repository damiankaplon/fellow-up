CREATE TABLE activity
(
    id       UUID PRIMARY KEY NOT NULL,
    category TEXT             NOT NULL,
    at       TIMESTAMP        NOT NULL,
    latitude DECIMAL          NOT NULL,
    longitude DECIMAL         NOT NULL
);
