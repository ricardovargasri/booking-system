export type TypeSpot = 'CABANA' | 'APARTAMENT' | 'HOUSE' | 'ROOM' | 'GLAMPING';

export interface Spot {
  id: number;
  name: string;
  location: string;
  pricePerNight: number;
  maxCapacity: number;
  typeSpot: TypeSpot;
  isAvailable: boolean;
}

export interface SpotPage {
  content: Spot[];
  totalElements: number;
  totalPages: number;
  number: number;
}
