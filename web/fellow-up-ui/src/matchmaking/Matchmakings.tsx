import {IconButton, Typography} from "@mui/material";
import AddBoxIcon from '@mui/icons-material/AddBox';
import {MatchmakingWizardDialog} from "./wizard/MatchmakingWizardDialog.tsx";
import React from "react";
import {MatchmakingWizardResult} from "./wizard/MatchmakingWizard.tsx";
import {MatchmakingsOperations} from "./MatchmakingsOperations.ts";
import MatchmakingsGrid from "./MatchmakingsGrid.tsx";
import Matchmaking from "./Matchmaking.ts";

export interface MatchmakingsProps {
  operations: MatchmakingsOperations
}

export default function Matchmakings({operations}: MatchmakingsProps) {
  const [showMatchmakingWizard, setShowMatchmakingWizard] = React.useState(false);
  const [matchmakings, setMatchmakings] = React.useState<Matchmaking[] | undefined>();
  React.useEffect(
    () => {
      if (matchmakings === undefined) {
        operations.findMatchmakings()
          .then((matchmakings: Matchmaking[]) => setMatchmakings(matchmakings));
      }
    }, [matchmakings]);
  return (
    <div style={{display: 'flex', flexDirection: 'column'}}>
      <div style={{display: 'flex', width: '100%', alignItems: 'center', justifyContent: 'space-between'}}>
        <Typography variant={"h6"}>Matchmakings</Typography>
        <MatchmakingWizardDialog open={showMatchmakingWizard}
                                 onComplete={(result: MatchmakingWizardResult) =>
                                   operations.createMatchmaking(result)
                                     .then(() => setShowMatchmakingWizard(false))
                                     .then(() => operations.findMatchmakings())
                                     .then((matchmakings: Matchmaking[]) => setMatchmakings(matchmakings))
                                 }
                                 onClose={() => setShowMatchmakingWizard(false)}/>
        <IconButton onClick={() => setShowMatchmakingWizard(!showMatchmakingWizard)}>
          <AddBoxIcon fontSize="large" color="primary"/>
        </IconButton>
      </div>
      <MatchmakingsGrid matchmakings={matchmakings ?? []}/>
    </div>
  );
}