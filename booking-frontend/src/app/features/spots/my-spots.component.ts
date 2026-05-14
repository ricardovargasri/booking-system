import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SpotService } from '../../core/services/spot.service';
import { Spot } from '../../core/models/spot.model';

@Component({
  selector: 'app-my-spots',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-wrapper">
      <div class="header">
        <div>
          <h1>Mis Alojamientos</h1>
          <p>Gestiona y actualiza tus propiedades publicadas.</p>
        </div>
        <button class="btn-new" routerLink="/spots/new">+ Publicar Nuevo</button>
      </div>

      <div class="table-container" *ngIf="!loading">
        <table class="modern-table">
          <thead>
            <tr>
              <th>Alojamiento</th>
              <th>Ubicación</th>
              <th>Precio</th>
              <th>Capacidad</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let spot of spots">
              <td>
                <div class="spot-cell">
                  <span class="icon">🏠</span>
                  <div>
                    <div class="name">{{ spot.name }}</div>
                    <div class="type">{{ spot.typeSpot }}</div>
                  </div>
                </div>
              </td>
              <td>{{ spot.location }}</td>
              <td class="price">{{ spot.pricePerNight | currency }}</td>
              <td>{{ spot.maxCapacity }} pers.</td>
              <td>
                <span class="badge" [class.available]="spot.isAvailable">
                  {{ spot.isAvailable ? 'Disponible' : 'Ocupado' }}
                </span>
              </td>
              <td>
                <div class="actions">
                  <button class="btn-edit" [routerLink]="['/spots/edit', spot.id]">Editar</button>
                  <button class="btn-delete" (click)="onDelete(spot.id)">Eliminar</button>
                </div>
              </td>
            </tr>
            <tr *ngIf="spots.length === 0">
              <td colspan="6">
                <div class="empty-state">
                  <div class="empty-icon-wrapper">
                    <span class="empty-emoji">🏡</span>
                    <div class="pulse-ring"></div>
                  </div>
                  <h3>Tu portafolio está vacío</h3>
                  <p>Parece que aún no has publicado ninguna propiedad. ¡Empieza hoy y conviértete en un anfitrión de éxito!</p>
                  <button class="btn-new-empty" routerLink="/spots/new">Publicar mi primer alojamiento</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="loading" *ngIf="loading">
        <div class="spinner"></div>
        <p>Cargando tus propiedades...</p>
      </div>
    </div>
  `,
  styles: [`
    .page-wrapper { max-width: 1100px; margin: 0 auto; padding: 3rem 2rem; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2.5rem; }
    h1 { font-size: 2.25rem; font-weight: 800; color: var(--text); letter-spacing: -1px; margin-bottom: 0.25rem; }
    p { color: var(--text-muted); font-size: 1.05rem; }

    .btn-new {
      background: var(--accent-secondary);
      color: #fff; border: none; padding: 0.8rem 1.5rem;
      border-radius: 50px; font-weight: 700; cursor: pointer;
      transition: all .2s; box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);
    }
    .btn-new:hover { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(79, 70, 229, 0.3); }

    .table-container {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 24px;
      overflow: hidden;
      box-shadow: var(--shadow);
    }

    .modern-table { width: 100%; border-collapse: collapse; text-align: left; }
    .modern-table th {
      padding: 1.25rem 1.5rem;
      background: #f8fafc;
      font-size: 0.85rem;
      font-weight: 700;
      color: var(--text-muted);
      text-transform: uppercase;
      letter-spacing: 0.5px;
      border-bottom: 1px solid var(--border);
    }
    .modern-table td { padding: 1.25rem 1.5rem; border-bottom: 1px solid var(--border); font-size: 0.95rem; }

    .spot-cell { display: flex; align-items: center; gap: 1rem; }
    .spot-cell .icon { font-size: 1.5rem; }
    .spot-cell .name { font-weight: 700; color: var(--text); }
    .spot-cell .type { font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; }

    .price { font-weight: 700; color: var(--text); }
    
    .badge {
      padding: 0.4rem 0.8rem; border-radius: 50px; font-size: 0.75rem; font-weight: 700;
    }
    .badge.available { background: #dcfce7; color: #15803d; }
    .badge:not(.available) { background: #fee2e2; color: #b91c1c; }

    .actions { display: flex; gap: 0.5rem; }
    button {
      padding: 0.5rem 1rem; border-radius: 12px; font-size: 0.85rem; font-weight: 600;
      cursor: pointer; transition: all .2s; border: 1px solid var(--border);
    }
    .btn-edit { background: #fff; color: var(--text); }
    .btn-edit:hover { background: #f8fafc; border-color: var(--accent-secondary); color: var(--accent-secondary); }
    .btn-delete { background: #fff; color: #ef4444; }
    .btn-delete:hover { background: #fee2e2; border-color: #ef4444; }

    .loading { text-align: center; padding: 4rem; }
    .spinner {
      width: 40px; height: 40px; margin: 0 auto 1rem;
      border: 3px solid #f1f5f9; border-top-color: var(--accent);
      border-radius: 50%; animation: spin 0.8s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
    .empty-state {
      padding: 5rem 2rem;
      text-align: center;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1.5rem;
    }
    .empty-icon-wrapper {
      position: relative;
      font-size: 4rem;
      margin-bottom: 1rem;
    }
    .empty-emoji { position: relative; z-index: 2; }
    .pulse-ring {
      position: absolute;
      top: 50%; left: 50%;
      transform: translate(-50%, -50%);
      width: 100px; height: 100px;
      background: var(--accent-glow);
      border-radius: 50%;
      z-index: 1;
      animation: pulse 2s infinite;
    }
    @keyframes pulse {
      0% { transform: translate(-50%, -50%) scale(0.8); opacity: 0.5; }
      100% { transform: translate(-50%, -50%) scale(1.5); opacity: 0; }
    }
    .empty-state h3 { font-size: 1.5rem; font-weight: 800; color: var(--text); }
    .empty-state p { max-width: 400px; margin: 0 auto; line-height: 1.6; }
    .btn-new-empty {
      background: var(--accent);
      color: white; border: none; padding: 1rem 2rem;
      border-radius: 50px; font-weight: 700; cursor: pointer;
      transition: all 0.3s;
    }
    .btn-new-empty:hover { transform: scale(1.05); box-shadow: 0 10px 20px -5px var(--accent-glow); }
  `]
})
export class MySpotsComponent implements OnInit {
  spots: Spot[] = [];
  loading = true;

  constructor(private spotService: SpotService) {}

  ngOnInit(): void {
    this.loadSpots();
  }

  loadSpots(): void {
    this.loading = true;
    this.spotService.getMySpots().subscribe({
      next: (res) => {
        this.spots = res.content;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  onDelete(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar este alojamiento? Esta acción no se puede deshacer.')) {
      this.spotService.delete(id).subscribe({
        next: () => {
          this.spots = this.spots.filter(s => s.id !== id);
          alert('Alojamiento eliminado correctamente.');
        },
        error: () => alert('Error al eliminar el alojamiento.')
      });
    }
  }
}
