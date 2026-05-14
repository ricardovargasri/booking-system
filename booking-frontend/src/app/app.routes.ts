import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/spots', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/register.component').then(m => m.RegisterComponent) },
  { path: 'spots', loadComponent: () => import('./features/spots/spot-list.component').then(m => m.SpotListComponent), canActivate: [authGuard] },
  { path: 'spots/new', loadComponent: () => import('./features/spots/spot-form.component').then(m => m.SpotFormComponent), canActivate: [authGuard] },
  { path: 'my-spots', loadComponent: () => import('./features/spots/my-spots.component').then(m => m.MySpotsComponent), canActivate: [authGuard] },
  { path: 'book/:spotId', loadComponent: () => import('./features/bookings/booking-form.component').then(m => m.BookingFormComponent), canActivate: [authGuard] },
  { path: 'my-bookings', loadComponent: () => import('./features/bookings/my-bookings.component').then(m => m.MyBookingsComponent), canActivate: [authGuard] },
  { path: '**', redirectTo: '/spots' }
];
