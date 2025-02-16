import {Container} from "@mui/material";
import Matchmakings from "./Matchmakings.tsx";
import MatchmakingsOperationsFetch, {MatchmakingsApiJwtProvider} from "./MatchmakingsOperationsFetch.ts";
import {FellowUpAuthContext} from "../security/FellowUpAuthContext.tsx";
import React from "react";

export default function Overview() {
  const authContext = React.useContext(FellowUpAuthContext);
  const matchmakingsApiJwtProvider: MatchmakingsApiJwtProvider = {provide: () => authContext.jwt!};
  const matchmakingsOperationsFetch = new MatchmakingsOperationsFetch(matchmakingsApiJwtProvider);
  return (
    <Container maxWidth={false}>
      <Matchmakings operations={matchmakingsOperationsFetch}/>
    </Container>
  );
}
