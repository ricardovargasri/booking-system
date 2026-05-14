export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED';
export type PaymentStatus = 'PENDING_PAYMENT' | 'PAID' | 'REFUNDED';

export interface Booking {
  id: number;
  guestId: string;
  spotId: number;
  checkInDate: string;
  checkOutDate: string;
  createdAt: string;
  status: BookingStatus;
  totalPrice: number;
  paymentStatus: PaymentStatus;
  numberOfGuests: number;
  specialRequests: string;
}

export interface BookingRequest {
  guestId: string;
  spotId: number;
  checkInDate: string;
  checkOutDate: string;
  numberOfGuests: number;
  specialRequests: string;
}

export interface BookingPage {
  content: Booking[];
  totalElements: number;
  totalPages: number;
  number: number;
}
