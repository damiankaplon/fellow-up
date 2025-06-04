import React, {useEffect} from "react";
import {FellowUpAuthContext} from "../security/FellowUpAuthContext.tsx";
import {MatchmakingsApiJwtProvider} from "../matchmaking/MatchmakingsOperationsFetch.ts";
import MediationComponent from "./MediationComponent.tsx";
import MediationOperationsFetch from "./MediationOperations.ts";
import {Mediation} from "./Mediation.ts";
import {Box, CircularProgress} from "@mui/material";
import {useParams} from "react-router-dom";

const LOADING_COMPONENT = (
  <Box sx={{display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100vh'}}>
    <CircularProgress/>
  </Box>
)

export default function MatchmakingMediation() {
  const authContext = React.useContext(FellowUpAuthContext);
  const matchmakingsApiJwtProvider: MatchmakingsApiJwtProvider = {provide: () => authContext.jwt!};
  const operations = new MediationOperationsFetch(matchmakingsApiJwtProvider);
  const matchmakingId: string | undefined = useParams().matchmakingId;
  const [component, setComponent] = React.useState<React.ReactNode>(LOADING_COMPONENT);

  useEffect(() => {
    if (matchmakingId && component == LOADING_COMPONENT) {
      operations.getMediationByMatchmakingId(matchmakingId).then((mediation: Mediation) => {
          setComponent(<MediationComponent mediation={mediation}
                                           googleMapsApiKey={import.meta.env.VITE_GOOGLE_MAPS_API_KEY}
                                           onAcceptProposal={() => {
                                           }}/>
          );
        }
      );
    }
  }, [component]);

  return component;
}
