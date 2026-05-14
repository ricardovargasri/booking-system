import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-spot-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="page-wrapper">
      <div class="header">
        <button class="btn-back" routerLink="/spots">← Volver</button>
        <h1>Publicar un nuevo alojamiento</h1>
        <p>Cuéntanos los detalles de tu espacio para empezar a recibir huéspedes.</p>
      </div>

      <div class="form-container">
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="vibrant-form">
          <div class="form-section">
            <h3>Información Básica</h3>
            <div class="field">
              <label for="name">Nombre del alojamiento</label>
              <input id="name" type="text" formControlName="name" placeholder="Ej: Cabaña acogedora frente al mar">
            </div>
            
            <div class="field">
              <label for="location">Ubicación</label>
              <input id="location" type="text" formControlName="location" placeholder="Ciudad, País">
            </div>
          </div>

      <div class="form-row">
            <div class="field">
              <label for="typeSpot">Tipo de alojamiento</label>
              <select id="typeSpot" formControlName="typeSpot">
                <option value="CABANA">Cabaña</option>
                <option value="APARTAMENT">Apartamento</option>
                <option value="HOUSE">Casa</option>
                <option value="ROOM">Habitación</option>
                <option value="GLAMPING">Glamping</option>
              </select>
            </div>
            
            <div class="field">
              <label for="maxCapacity">Capacidad máxima</label>
              <input id="maxCapacity" type="number" formControlName="maxCapacity" min="1" max="50">
            </div>
          </div>

          <div class="form-section">
            <h3>Precio</h3>
            <div class="field">
              <label for="pricePerNight">Precio por noche (USD) - Entre $20 y $10,000</label>
              <div class="price-input">
                <span class="currency">$</span>
                <input id="pricePerNight" type="number" formControlName="pricePerNight" min="20" max="10000">
              </div>
            </div>
          </div>

          <div class="error-msg" *ngIf="errorMsg">⚠️ {{ errorMsg }}</div>

          <div class="form-actions">
            <button type="submit" [disabled]="loading || form.invalid" class="btn-submit">
              <span *ngIf="!loading">Publicar Alojamiento ✨</span>
              <span *ngIf="loading">Publicando...</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .page-wrapper { max-width: 800px; margin: 0 auto; padding: 3rem 2rem; }
    .header { margin-bottom: 2.5rem; }
    .btn-back { 
      background: none; border: none; color: var(--accent); 
      font-weight: 700; cursor: pointer; padding: 0; margin-bottom: 1rem;
      font-family: inherit; font-size: 0.95rem;
    }
    h1 { font-size: 2.5rem; font-weight: 800; color: var(--text); letter-spacing: -1px; margin-bottom: 0.5rem; }
    p { color: var(--text-muted); font-size: 1.1rem; }

    .form-container {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 32px;
      padding: 3rem;
      box-shadow: var(--shadow);
    }

    .vibrant-form { display: flex; flex-direction: column; gap: 2rem; }
    .form-section h3 { font-size: 1.1rem; font-weight: 700; margin-bottom: 1.25rem; color: var(--accent-secondary); }
    
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; }
    .field { display: flex; flex-direction: column; gap: 0.6rem; }
    label { font-weight: 600; color: var(--text); font-size: 0.9rem; }
    
    input, select {
      background: #f8fafc;
      border: 2px solid #f1f5f9;
      border-radius: 16px;
      padding: 1rem 1.25rem;
      font-size: 1rem;
      color: var(--text);
      outline: none;
      transition: all .2s;
      font-family: inherit;
    }
    input:focus, select:focus { border-color: var(--accent); background: #fff; box-shadow: 0 0 0 4px var(--accent-glow); }

    .price-input { position: relative; display: flex; align-items: center; }
    .currency { position: absolute; left: 1.25rem; font-weight: 700; color: var(--text-muted); }
    .price-input input { padding-left: 2.5rem; width: 100%; }

    .error-msg { background: #fee2e2; color: #b91c1c; padding: 1rem; border-radius: 16px; font-weight: 600; font-size: 0.9rem; }
    
    .btn-submit {
      background: var(--accent);
      border: none;
      color: #fff;
      padding: 1.25rem;
      border-radius: 20px;
      font-size: 1.1rem;
      font-weight: 800;
      cursor: pointer;
      transition: all .3s;
      box-shadow: 0 10px 20px -5px rgba(255, 90, 95, 0.4);
    }
    .btn-submit:hover:not(:disabled) { transform: scale(1.02); box-shadow: 0 15px 30px -5px rgba(255, 90, 95, 0.5); }
    .btn-submit:disabled { background: #cbd5e1; box-shadow: none; cursor: not-allowed; }
  `]
})
export class SpotFormComponent {
  form: FormGroup;
  loading = false;
  errorMsg = '';

  constructor(
    private fb: FormBuilder, 
    private http: HttpClient, 
    private router: Router,
    private authService: AuthService
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      location: ['', Validators.required],
      pricePerNight: [0, [Validators.required, Validators.min(20)]],
      maxCapacity: [1, [Validators.required, Validators.min(1)]],
      typeSpot: ['HOUSE', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMsg = '';

    this.http.post(`${environment.apiUrl}/api/v1/spot`, this.form.value).subscribe({
      next: () => {
        // Refrescar el perfil para actualizar el rol a OWNER si es necesario
        this.authService.getProfile().subscribe({
          next: () => this.router.navigate(['/spots']),
          error: () => this.router.navigate(['/spots']) // Navegar igual si falla el refresh
        });
      },
      error: (err) => {
        this.errorMsg = 'Error al publicar el alojamiento. Verifica los datos.';
        this.loading = false;
      }
    });
  }
}
