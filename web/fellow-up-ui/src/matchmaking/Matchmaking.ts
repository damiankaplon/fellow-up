export default interface Matchmaking {
  id: string;
  status: 'STILL_LOOKING' | 'MEDIATING';
  category: string;
  mediationId?: string,
  at: Date;
  location: {
    lat: number;
    lng: number;
  }
}
