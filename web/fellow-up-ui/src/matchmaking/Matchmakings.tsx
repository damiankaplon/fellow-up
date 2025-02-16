import {IconButton, Typography} from "@mui/material";
import AddBoxIcon from '@mui/icons-material/AddBox';
import {MatchmakingWizardDialog} from "./wizard/MatchmakingWizardDialog.tsx";
import React from "react";
import {MatchmakingWizardResult} from "./wizard/MatchmakingWizard.tsx";
import {MatchmakingsOperations} from "./MatchmakingsOperations.ts";

export interface MatchmakingsProps {
  operations: MatchmakingsOperations
}

export default function Matchmakings({ operations }: MatchmakingsProps) {
  const [showMatchmakingWizard, setShowMatchmakingWizard] = React.useState(false);
  return (
    <div style={{display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
      <Typography variant={"h6"}>Matchmakings</Typography>
      <MatchmakingWizardDialog open={showMatchmakingWizard}
                               onComplete={(result: MatchmakingWizardResult) =>
                                 operations.createMatchmaking(result)
                                   .then(() => setShowMatchmakingWizard(false))
                               }
                               onClose={() => setShowMatchmakingWizard(false)}/>
      <IconButton onClick={() => setShowMatchmakingWizard(!showMatchmakingWizard)}>
        <AddBoxIcon fontSize="large" color="primary"/>
      </IconButton>
    </div>
  );
}