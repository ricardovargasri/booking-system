import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, User } from '../models/auth.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/api/v1/auth`;

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, credentials).pipe(
      tap(res => {
        localStorage.setItem('accessToken', res.accessToken);
        localStorage.setItem('refreshToken', res.refreshToken);
        localStorage.setItem('user', JSON.stringify(res.user));
      })
    );
  }

  logout(): void {
    const token = this.getToken();
    const refreshToken = localStorage.getItem('refreshToken') ?? '';
    if (token) {
      this.http.post(`${this.baseUrl}/logout`, { refreshToken }, {
        headers: { Authorization: `Bearer ${token}` }
      }).subscribe({ error: () => {} });
    }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }

  getUser(): User | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isOwner(): boolean {
    const user = this.getUser();
    return user?.rol === 'OWNER' || user?.rol === 'ADMIN';
  }

  getProfile(): Observable<User> {
    const token = this.getToken();
    return this.http.get<User>(`${environment.apiUrl}/api/v1/user/me`, {
      headers: { Authorization: `Bearer ${token}` }
    }).pipe(
      tap(user => {
        localStorage.setItem('user', JSON.stringify(user));
      })
    );
  }
}
