
import {MatchmakingWizardResult} from "./wizard/MatchmakingWizard.tsx";
import {MatchmakingsOperations} from "./MatchmakingsOperations.ts";
import {Matchmaking} from "./Matchmaking.ts";

export interface MatchmakingsApiJwtProvider {
  provide: () => string;
}

export default class MatchmakingsOperationsFetch implements MatchmakingsOperations {

  private readonly jwtProvider: MatchmakingsApiJwtProvider;

  constructor(jwtProvider: MatchmakingsApiJwtProvider) {
    this.jwtProvider = jwtProvider;
  }

 private createRequestBody(from: MatchmakingWizardResult): CreateMatchmakingBody {
    return {
      category: from.category,
      at: from.date.toISOString(),
      location: from.location
    };
  }

  async createMatchmaking(matchmakingWizardResult: MatchmakingWizardResult): Promise<void> {
    const requestBody = this.createRequestBody(matchmakingWizardResult);
    const jwt = this.jwtProvider.provide();
    await fetch(
      '/api/matchmakings',
      {
        method: 'POST',
        body: JSON.stringify(requestBody),
        headers: {'Authorization': `Bearer ${jwt}`, 'Content-Type': 'application/json', 'accept': 'application/json'}
      }
    );
  }


  async findMatchmakings(): Promise<Matchmaking[]> {
    return fetch('/api/matchmakings').then(response => response.json());
  }
}

interface CreateMatchmakingBody {
  category: string;
  at: string;
  location: {
    lat: number;
    lng: number;
  }
}
