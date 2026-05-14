import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar">
      <div class="nav-container">
        <div class="nav-brand" routerLink="/spots">
          <span class="brand-icon">✨</span>
          <span class="brand-name">BookingApp</span>
        </div>
        <div class="nav-links" *ngIf="authService.isLoggedIn()">
          <a routerLink="/spots" routerLinkActive="active">Explorar</a>
          <a routerLink="/my-bookings" routerLinkActive="active">Mis Viajes</a>
          <a *ngIf="authService.isOwner()" routerLink="/my-spots" routerLinkActive="active">Mis Alojamientos</a>
          
          <div class="user-menu">
            <button class="user-btn" (click)="toggleDropdown()">
              <div class="user-avatar">{{ authService.getUser()?.name?.charAt(0) }}</div>
              <span class="user-name">{{ authService.getUser()?.name }}</span>
              <span class="chevron">▾</span>
            </button>
            
            <div class="dropdown" *ngIf="showDropdown">
              <div class="dropdown-header">
                <strong>{{ authService.getUser()?.name }}</strong>
                <span>{{ authService.getUser()?.email }}</span>
              </div>
              <div class="dropdown-divider"></div>
              <a routerLink="/spots/new" (click)="showDropdown = false">✨ Convertirse en anfitrión</a>
              <a (click)="logout()">Cerrar Sesión</a>
            </div>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      background: rgba(255, 255, 255, 0.85);
      backdrop-filter: blur(20px);
      border-bottom: 1px solid var(--border);
      position: sticky;
      top: 0;
      z-index: 1000;
      padding: 0.75rem 0;
    }
    .nav-container {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 2rem;
    }
    .nav-brand { display: flex; align-items: center; gap: 0.5rem; cursor: pointer; }
    .brand-icon { font-size: 1.5rem; }
    .brand-name { 
      font-size: 1.25rem; 
      font-weight: 800; 
      color: var(--accent-secondary);
      letter-spacing: -0.5px;
    }
    .nav-links { display: flex; align-items: center; gap: 1.5rem; }
    .nav-links a { 
      color: var(--text-muted); 
      text-decoration: none; 
      font-size: 0.9rem; 
      font-weight: 600;
      transition: all .2s;
      cursor: pointer;
    }
    .nav-links a.active { color: var(--accent); }
    .nav-links a:hover { color: var(--text); }

    /* Dropdown Styles */
    .user-menu { position: relative; }
    .user-btn {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      background: #f8fafc;
      border: 1px solid var(--border);
      padding: 0.4rem 0.75rem;
      border-radius: 50px;
      cursor: pointer;
      transition: all 0.2s;
    }
    .user-btn:hover { background: #f1f5f9; border-color: #cbd5e1; }
    .user-avatar {
      width: 28px; height: 28px;
      background: var(--accent);
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
      font-size: 0.8rem;
    }
    .user-name { font-size: 0.9rem; font-weight: 600; color: var(--text); }
    .chevron { color: var(--text-muted); font-size: 0.8rem; }

    .dropdown {
      position: absolute;
      top: calc(100% + 0.5rem);
      right: 0;
      width: 240px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 10px 25px -5px rgba(0,0,0,0.1), 0 8px 10px -6px rgba(0,0,0,0.1);
      border: 1px solid var(--border);
      overflow: hidden;
      animation: slideIn 0.2s ease-out;
    }
    @keyframes slideIn {
      from { opacity: 0; transform: translateY(-10px); }
      to { opacity: 1; transform: translateY(0); }
    }
    .dropdown-header { padding: 1rem; display: flex; flex-direction: column; gap: 0.2rem; }
    .dropdown-header strong { font-size: 0.9rem; color: var(--text); }
    .dropdown-header span { font-size: 0.8rem; color: var(--text-muted); }
    .dropdown-divider { height: 1px; background: var(--border); }
    .dropdown a {
      display: block;
      padding: 0.75rem 1rem;
      color: var(--text) !important;
      font-size: 0.9rem !important;
      font-weight: 500 !important;
    }
    .dropdown a:hover { background: #f8fafc; color: var(--accent) !important; }
  `]
})
export class NavbarComponent {
  showDropdown = false;

  constructor(public authService: AuthService, private router: Router) {}

  toggleDropdown() {
    this.showDropdown = !this.showDropdown;
  }

  logout() {
    this.showDropdown = false;
    this.authService.logout();
  }
}
