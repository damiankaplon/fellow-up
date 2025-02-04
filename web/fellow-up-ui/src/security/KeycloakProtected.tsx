import React from "react";
import {useKeycloak} from "@react-keycloak/web";

interface KeycloakProtectedProps {
  children: React.ReactNode;
  loadingComponent?: React.ReactNode; // Optional loading component
}

export default function KeycloakProtected(props: KeycloakProtectedProps) {
  const {keycloak, initialized} = useKeycloak();

  if (!initialized) {
    return <>{props.loadingComponent}</>;
  }

  if (!keycloak.authenticated) {
    keycloak.login();
    return null;
  }

  return <>{props.children}</>;
}
