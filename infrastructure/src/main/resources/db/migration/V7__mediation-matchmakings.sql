CREATE TABLE mediation_matchmakings
(
    mediation_id   uuid NOT NULL,
    matchmaking_id uuid NOT NULL,
    PRIMARY KEY (mediation_id, matchmaking_id)
);
