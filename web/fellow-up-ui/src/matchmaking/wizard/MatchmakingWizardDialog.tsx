import {Dialog, DialogContent, DialogTitle} from "@mui/material";
import {MatchmakingWizard, MatchmakingWizardResult} from "./MatchmakingWizard.tsx";

export function MatchmakingWizardDialog({open, onComplete, onClose}: {
  open: boolean;
  onComplete: (result: MatchmakingWizardResult) => void;
  onClose: () => void
}) {
  return (
    <Dialog maxWidth={false} open={open} onClose={() => onClose()}>
      <DialogTitle>Start matchmaking</DialogTitle>
      <DialogContent dividers>
        <MatchmakingWizard onComplete={onComplete}/>
      </DialogContent>
    </Dialog>
  );
}