export interface Matchmaking {
  id: string;
  category: string;
  at: Date;
  location: {
    lat: number;
    lng: number;
  }
}
