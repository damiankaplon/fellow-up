CREATE TABLE mediation
(
    id           UUID PRIMARY KEY NOT NULL,
    category     TEXT             NOT NULL,
    is_finished  BOOLEAN          NOT NULL,
    participants JSONB            NOT NULL
);

CREATE TABLE activity_proposal
(
    id                          UUID PRIMARY KEY NOT NULL,
    order_number                INT              NOT NULL,
    mediation_id                UUID REFERENCES mediation (id),
    time                        TIMESTAMP        NOT NULL,
    longitude                   decimal          NOT NULL,
    latitude                    decimal          NOT NULL,
    accepted_by_participant_ids JSONB            NOT NULL
);
