import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
    "url": "http://localhost:8282",
    "realm": "fellow_up",
    "clientId": "fellow_up_ui_web"
});

export default keycloak;
