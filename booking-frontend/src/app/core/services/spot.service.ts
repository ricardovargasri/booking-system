import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Spot, SpotPage } from '../models/spot.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SpotService {
  private readonly baseUrl = `${environment.apiUrl}/api/v1/spot`;

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 9): Observable<SpotPage> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<SpotPage>(this.baseUrl, { params });
  }

  getMySpots(page = 0, size = 10): Observable<SpotPage> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<SpotPage>(`${this.baseUrl}/my`, { params });
  }

  getById(id: number): Observable<Spot> {
    return this.http.get<Spot>(`${this.baseUrl}/${id}`);
  }

  create(spot: any): Observable<Spot> {
    return this.http.post<Spot>(this.baseUrl, spot);
  }

  update(id: number, spot: any): Observable<Spot> {
    return this.http.put<Spot>(`${this.baseUrl}/${id}`, spot);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
