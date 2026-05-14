import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">🏠</div>
          <h1>Crea tu cuenta</h1>
          <p>Únete a nuestra plataforma de reservas</p>
        </div>

        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="login-form">
          <div class="field">
            <label for="name">Nombre completo</label>
            <input id="name" type="text" formControlName="name" placeholder="Ej: Juan Pérez">
          </div>
          
          <div class="field">
            <label for="email">Correo electrónico</label>
            <input id="email" type="email" formControlName="email" placeholder="correo@ejemplo.com">
          </div>

          <div class="field">
            <label for="password">Contraseña</label>
            <input id="password" type="password" formControlName="password" placeholder="••••••••">
          </div>

          <div class="error-msg" *ngIf="errorMsg">{{ errorMsg }}</div>
          <div class="success-msg" *ngIf="successMsg">{{ successMsg }}</div>

          <button type="submit" [disabled]="loading || form.invalid" class="btn-login">
            <span *ngIf="!loading">Registrarse</span>
            <span *ngIf="loading">Procesando...</span>
          </button>

          <p class="footer-text">
            ¿Ya tienes cuenta? <a routerLink="/login">Inicia sesión</a>
          </p>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .login-wrapper {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
    }
    .login-card {
      width: 100%;
      max-width: 450px;
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 32px;
      padding: 3rem;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.08);
    }
    .login-header { text-align: center; margin-bottom: 2.5rem; }
    .logo { font-size: 3rem; margin-bottom: 1rem; filter: drop-shadow(0 4px 6px rgba(0,0,0,0.1)); }
    h1 { color: var(--text); font-size: 2rem; margin: 0 0 0.5rem; font-weight: 800; letter-spacing: -1px; }
    p { color: var(--text-muted); font-size: 1rem; margin: 0; }
    .login-form { display: flex; flex-direction: column; gap: 1.5rem; }
    .field { display: flex; flex-direction: column; gap: 0.6rem; }
    label { color: var(--text); font-size: 0.9rem; font-weight: 600; }
    input {
      background: #f8fafc;
      border: 2px solid #f1f5f9;
      border-radius: 16px;
      padding: 1rem 1.25rem;
      color: var(--text);
      font-size: 1rem;
      outline: none;
      transition: all .2s;
    }
    input:focus { border-color: var(--accent); background: #fff; box-shadow: 0 0 0 4px var(--accent-glow); }
    .error-msg {
      background: #fee2e2; border: 1px solid #fecaca;
      color: #b91c1c; border-radius: 12px; padding: 0.8rem 1.25rem; font-size: 0.9rem; font-weight: 500;
    }
    .success-msg {
      background: #dcfce7; border: 1px solid #bbf7d0;
      color: #15803d; border-radius: 12px; padding: 0.8rem 1.25rem; font-size: 0.9rem; font-weight: 500;
    }
    .btn-login {
      background: var(--accent);
      border: none;
      color: #fff;
      padding: 1.1rem;
      border-radius: 16px;
      font-size: 1.1rem;
      font-weight: 700;
      cursor: pointer;
      transition: all .2s;
      margin-top: 0.5rem;
      box-shadow: 0 10px 15px -3px rgba(255, 90, 95, 0.3);
    }
    .btn-login:hover:not(:disabled) { transform: scale(1.02); opacity: 0.95; box-shadow: 0 20px 25px -5px rgba(255, 90, 95, 0.4); }
    .btn-login:disabled { background: #cbd5e1; box-shadow: none; cursor: not-allowed; }
    .footer-text { text-align: center; color: var(--text-muted); font-size: 0.95rem; margin-top: 1.5rem; font-weight: 500; }
    .footer-text a { color: var(--accent); text-decoration: none; font-weight: 700; }
  `]
})
export class RegisterComponent {
  form: FormGroup;
  loading = false;
  errorMsg = '';
  successMsg = '';

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMsg = '';
    
    this.http.post(`${environment.apiUrl}/api/v1/user`, this.form.value).subscribe({
      next: () => {
        this.successMsg = '¡Cuenta creada! Redirigiendo al login...';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.errorMsg = err.error?.message || 'Error al crear la cuenta. El email podría estar en uso.';
        this.loading = false;
      }
    });
  }
}
