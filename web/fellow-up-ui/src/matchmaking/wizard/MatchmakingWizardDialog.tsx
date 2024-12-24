import {Dialog, DialogContent, DialogTitle} from "@mui/material";
import {MatchmakingWizard, MatchmakingWizardState} from "./MatchmakingWizard.tsx";

export function MatchmakingWizardDialog({open, onClose}: { open: boolean; onClose: () => void }) {
    const matchmakingWizardState: MatchmakingWizardState = {steps: 3, currentStep: 1};
    return (
        <Dialog open={open} onClose={() => onClose()}>
            <DialogTitle>Start matchmaking</DialogTitle>
            <DialogContent dividers>
                <MatchmakingWizard state={matchmakingWizardState}/>
            </DialogContent>
        </Dialog>
    );
}