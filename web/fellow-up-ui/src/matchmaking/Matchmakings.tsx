import {IconButton, Typography} from "@mui/material";
import AddBoxIcon from '@mui/icons-material/AddBox';
import {MatchmakingWizardDialog} from "./wizard/MatchmakingWizardDialog.tsx";
import React from "react";

export default function Matchmakings() {
  const [showMatchmakingWizard, setShowMatchmakingWizard] = React.useState(false);
  return (
    <div style={{display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
      <Typography variant={"h6"}>Matchmakings</Typography>
      <MatchmakingWizardDialog open={showMatchmakingWizard}
                               onClose={() => setShowMatchmakingWizard(false)}/>
      <IconButton onClick={() => setShowMatchmakingWizard(!showMatchmakingWizard)}>
        <AddBoxIcon fontSize="large" color="primary"/>
      </IconButton>
    </div>
  );
}