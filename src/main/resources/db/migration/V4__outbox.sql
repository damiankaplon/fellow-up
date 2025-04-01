CREATE TABLE outbox
(
    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    destination TEXT                                         NOT NULL,
    message     JSONB                                        NOT NULL,
    created_on  TIMESTAMP                                    NOT NULL DEFAULT NOW()
);

CREATE TABLE outbox_published
(
    id         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    sent_to    TEXT                                         NOT NULL,
    message    JSONB                                        NOT NULL,
    created_on TIMESTAMP                                    NOT NULL DEFAULT NOW()
);
