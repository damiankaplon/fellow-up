import {MatchmakingWizardResult} from "./wizard/MatchmakingWizard.tsx";
import {MatchmakingsOperations} from "./MatchmakingsOperations.ts";
import Matchmaking from "./Matchmaking.ts";

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
    const headers = this.createRequestHeaders(jwt);
    await fetch(
      '/api/matchmakings',
      {
        method: 'POST',
        body: JSON.stringify(requestBody),
        headers: headers
      }
    );
  }


  async findMatchmakings(): Promise<Matchmaking[]> {
    const jwt = this.jwtProvider.provide();
    const headers = this.createRequestHeaders(jwt);
    return fetch('/api/matchmakings', {headers: headers})
      .then(response => response.json())
      .then((matchmakings: MatchmakingDto[]) =>
        matchmakings.map((matchmaking) =>
          this.toDomain(matchmaking))
      );
  }

  private createRequestHeaders(jwt: string) {
    return {
      'accept': 'application/json',
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${jwt}`
    };
  }

  private toDomain(dto: MatchmakingDto): Matchmaking {
    return {
      id: dto.id,
      category: dto.category,
      at: new Date(dto.at),
      location: dto.location
    }
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

interface MatchmakingDto {
  id: string;
  category: string;
  at: string;
  location: {
    lat: number;
    lng: number;
  }
}
