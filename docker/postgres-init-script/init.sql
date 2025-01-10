CREATE
    USER app_fellow_up WITH PASSWORD 'app_fellow_up';

CREATE
    DATABASE app_fellow_up OWNER app_fellow_up;

GRANT ALL PRIVILEGES ON DATABASE
    app_fellow_up TO app_fellow_up;

CREATE
    USER keycloak WITH PASSWORD 'keycloak';

CREATE
    DATABASE keycloak OWNER keycloak;

GRANT ALL PRIVILEGES ON DATABASE
    keycloak TO keycloak;
