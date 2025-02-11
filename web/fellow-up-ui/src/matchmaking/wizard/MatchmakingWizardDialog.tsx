import {Dialog, DialogContent, DialogTitle} from "@mui/material";
import {MatchmakingWizard, MatchmakingWizardResult} from "./MatchmakingWizard.tsx";
import {FellowUpAuthContext} from "../../security/FellowUpAuthContext.tsx";
import React from "react";

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

export function MatchmakingWizardDialog({open, onClose}: {
  open: boolean;
  onClose: () => void
}) {
  const authContext = React.useContext(FellowUpAuthContext);
  const onComplete = (result: MatchmakingWizardResult) =>
    createMatchmaking(authContext.jwt!, result).catch((error) => console.error(error));

  return (
    <Dialog maxWidth={false} open={open} onClose={() => onClose()}>
      <DialogTitle>Start matchmaking</DialogTitle>
      <DialogContent dividers>
        <MatchmakingWizard onComplete={onComplete}/>
      </DialogContent>
    </Dialog>
  );
}
