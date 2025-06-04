import {Mediation} from "./Mediation.ts";

export interface MediationApiJwtProvider {
  provide: () => string;
}

export interface MediationOperations {
  getMediationByMatchmakingId: (matchmakingId: string) => Promise<Mediation>;
}

export default class MediationOperationsFetch implements MediationOperations {

  private readonly jwtProvider: MediationApiJwtProvider;

  constructor(jwtProvider: MediationApiJwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  async getMediationByMatchmakingId(matchmakingId: string): Promise<Mediation> {
    const jwt = this.jwtProvider.provide();
    const headers = this.createRequestHeaders(jwt);
    const response = await fetch(`/api/matchmakings/${matchmakingId}/mediation`, {headers: headers});
    return await response.json();
  }

  private createRequestHeaders(jwt: string) {
    return {
      'accept': 'application/json',
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${jwt}`
    };
  }
}
