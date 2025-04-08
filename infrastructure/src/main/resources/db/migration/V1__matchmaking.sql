CREATE TABLE matchmaking
(
    id       UUID PRIMARY KEY NOT NULL,
    category TEXT             NOT NULL,
--! This should be not nullable. This should be a currently authenticated user id
    user_id  UUID,
    at       TIMESTAMP        NOT NULL
);
