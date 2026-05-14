import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">✨</div>
          <h1>¡Hola de nuevo!</h1>
          <p>Inicia sesión para continuar con tus planes</p>
        </div>

        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="login-form">
          <div class="field">
            <label for="email">Correo electrónico</label>
            <input id="email" type="email" formControlName="email" placeholder="tu@email.com">
          </div>
          <div class="field">
            <label for="password">Contraseña</label>
            <input id="password" type="password" formControlName="password" placeholder="••••••••">
          </div>

          <div class="error-msg" *ngIf="errorMsg">
            <span>⚠️</span> {{ errorMsg }}
          </div>

          <button type="submit" [disabled]="loading || form.invalid" class="btn-login">
            <span *ngIf="!loading">Entrar ahora</span>
            <span *ngIf="loading">Verificando...</span>
          </button>

          <p class="footer-text">
            ¿No tienes cuenta? <a routerLink="/register">Crea una aquí</a>
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
      padding: 2rem;
    }
    .login-card {
      width: 100%;
      max-width: 420px;
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 32px;
      padding: 3rem;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.08);
      animation: fadeIn 0.5s ease-out;
    }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
    .login-header { text-align: center; margin-bottom: 2.5rem; }
    .logo { font-size: 3.5rem; margin-bottom: 1rem; }
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
      background: #fee2e2;
      border: 1px solid #fecaca;
      color: #b91c1c;
      border-radius: 16px;
      padding: 1rem;
      font-size: 0.9rem;
      font-weight: 600;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      animation: shake 0.4s cubic-bezier(.36,.07,.19,.97) both;
    }
    @keyframes shake {
      10%, 90% { transform: translate3d(-1px, 0, 0); }
      20%, 80% { transform: translate3d(2px, 0, 0); }
      30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
      40%, 60% { transform: translate3d(4px, 0, 0); }
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
export class LoginComponent {
  form: FormGroup;
  loading = false;
  errorMsg = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMsg = '';
    
    this.authService.login(this.form.value).subscribe({
      next: () => this.router.navigate(['/spots']),
      error: (err) => {
        console.error('Login error:', err);
        this.loading = false;
        if (err.status === 401 || err.status === 403) {
          this.errorMsg = 'Credenciales incorrectas. Verifica tu email y contraseña.';
        } else if (err.status === 0) {
          this.errorMsg = 'No hay conexión con el servidor.';
        } else {
          this.errorMsg = 'Ocurrió un error inesperado (Error ' + err.status + ').';
        }
      }
    });
  }
}
