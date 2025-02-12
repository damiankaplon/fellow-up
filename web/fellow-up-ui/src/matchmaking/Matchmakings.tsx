import {IconButton, Typography} from "@mui/material";
import AddBoxIcon from '@mui/icons-material/AddBox';
import {MatchmakingWizardDialog} from "./wizard/MatchmakingWizardDialog.tsx";
import React from "react";
import {MatchmakingWizardResult} from "./wizard/MatchmakingWizard.tsx";
import {FellowUpAuthContext} from "../security/FellowUpAuthContext.tsx";

interface CreateMatchmakingBody {
  category: string;
  at: string;
}

function createRequestBody(from: MatchmakingWizardResult): CreateMatchmakingBody {
  return {
    category: from.category,
    at: from.date.toISOString()
  };
}

async function createMatchmaking(
  jwt: string,
  result: MatchmakingWizardResult
) {
  await fetch(
    '/api/matchmakings',
    {
      method: 'POST',
      body: JSON.stringify(createRequestBody(result)),
      headers: {'Authorization': `Bearer ${jwt}`, 'Content-Type': 'application/json', 'accept': 'application/json'}
    }
  );
}

export default function Matchmakings() {
  const [showMatchmakingWizard, setShowMatchmakingWizard] = React.useState(false);
  const authContext = React.useContext(FellowUpAuthContext);
  return (
    <div style={{display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
      <Typography variant={"h6"}>Matchmakings</Typography>
      <MatchmakingWizardDialog open={showMatchmakingWizard}
                               onComplete={(result: MatchmakingWizardResult) =>
                                 createMatchmaking(authContext.jwt!, result)
                                   .then(() => setShowMatchmakingWizard(false))
                               }
                               onClose={() => setShowMatchmakingWizard(false)}/>
      <IconButton onClick={() => setShowMatchmakingWizard(!showMatchmakingWizard)}>
        <AddBoxIcon fontSize="large" color="primary"/>
      </IconButton>
    </div>
  );
}