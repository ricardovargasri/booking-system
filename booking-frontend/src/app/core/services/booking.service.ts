import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Booking, BookingPage, BookingRequest } from '../models/booking.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly baseUrl = `${environment.apiUrl}/api/v1/bookings`;

  constructor(private http: HttpClient) {}

  create(request: BookingRequest): Observable<Booking> {
    return this.http.post<Booking>(this.baseUrl, request);
  }

  getMyBookings(page = 0, size = 10): Observable<BookingPage> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<BookingPage>(`${this.baseUrl}/my`, { params });
  }

  cancel(id: number): Observable<Booking> {
    return this.http.patch<Booking>(`${this.baseUrl}/${id}/cancel`, {});
  }
}
