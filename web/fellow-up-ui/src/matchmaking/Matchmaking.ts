export default interface Matchmaking {
  id: string;
  category: string;
  mediationId?: string,
  at: Date;
  location: {
    lat: number;
    lng: number;
  }
}
