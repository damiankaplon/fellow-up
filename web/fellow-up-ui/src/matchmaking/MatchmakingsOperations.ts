import {MatchmakingWizardResult} from "./wizard/MatchmakingWizard.tsx";
import Matchmaking from "./Matchmaking.ts";

export interface MatchmakingsOperations {
  findMatchmakings: () => Promise<Matchmaking[]>;
  createMatchmaking: (result: MatchmakingWizardResult) => Promise<void>;
}
