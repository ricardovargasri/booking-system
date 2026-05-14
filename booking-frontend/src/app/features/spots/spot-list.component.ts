import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SpotService } from '../../core/services/spot.service';
import { Spot } from '../../core/models/spot.model';

@Component({
  selector: 'app-spot-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-wrapper">
      <div class="page-header">
        <h1>Alojamientos Disponibles</h1>
        <p>Encuentra el lugar perfecto para tu próxima aventura</p>
      </div>

      <div class="loading" *ngIf="loading">
        <div class="spinner"></div>
        <p>Cargando alojamientos...</p>
      </div>

      <div class="error-banner" *ngIf="error && !loading">
        <span>⚠️ {{ error }}</span>
      </div>

      <div class="empty-state" *ngIf="!loading && spots.length === 0">
        <div class="empty-icon">🏜️</div>
        <h3>No hay alojamientos aún</h3>
        <p>Parece que no hay sitios disponibles en este momento. ¡Vuelve más tarde!</p>
      </div>

      <div class="spots-grid" *ngIf="!loading && !error && spots.length > 0">
        <div class="spot-card" *ngFor="let spot of spots" (click)="selectSpot(spot)">
          <div class="spot-image">
            <span class="spot-icon">{{ getIcon(spot.typeSpot) }}</span>
            <span class="spot-type-badge">{{ getTypeLabel(spot.typeSpot) }}</span>
            <span class="availability-badge" [class.available]="spot.isAvailable" [class.unavailable]="!spot.isAvailable">
              {{ spot.isAvailable ? 'Disponible' : 'No disponible' }}
            </span>
          </div>
          <div class="spot-info">
            <h3>{{ spot.name }}</h3>
            <div class="spot-location">📍 {{ spot.location }}</div>
            <div class="spot-meta">
              <span class="capacity">👥 Hasta {{ spot.maxCapacity }} personas</span>
            </div>
            <div class="spot-footer">
              <div class="price">
                <span class="price-amount">\${{ spot.pricePerNight | number:'1.0-0' }}</span>
                <span class="price-unit"> / noche</span>
              </div>
              <button class="btn-book" [disabled]="!spot.isAvailable" (click)="$event.stopPropagation(); selectSpot(spot)">
                {{ spot.isAvailable ? 'Reservar' : 'No disp.' }}
              </button>
            </div>
          </div>
        </div>

        <div class="empty-state" *ngIf="spots.length === 0">
          <div class="empty-icon">🏜️</div>
          <h3>No hay alojamientos</h3>
          <p>Aún no se han registrado alojamientos en la plataforma.</p>
        </div>
      </div>

      <div class="pagination" *ngIf="totalPages > 1">
        <button (click)="prevPage()" [disabled]="currentPage === 0">← Anterior</button>
        <span>Página {{ currentPage + 1 }} de {{ totalPages }}</span>
        <button (click)="nextPage()" [disabled]="currentPage >= totalPages - 1">Siguiente →</button>
      </div>
    </div>
  `,
  styles: [`
    .page-wrapper { padding: 2rem; max-width: 1200px; margin: 0 auto; }
    .page-header { margin-bottom: 2.5rem; }
    .page-header h1 { color: var(--text); font-size: 2.25rem; font-weight: 800; margin: 0 0 0.5rem; letter-spacing: -1px; }
    .page-header p { color: var(--text-muted); font-size: 1.1rem; }
    .loading { display: flex; flex-direction: column; align-items: center; gap: 1rem; padding: 4rem; color: var(--text-muted); }
    .spinner {
      width: 40px; height: 40px;
      border: 3px solid #f1f5f9;
      border-top-color: var(--accent);
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
    .error-banner {
      background: #fee2e2; border: 1px solid #fecaca;
      color: #b91c1c; border-radius: 12px; padding: 1rem 1.5rem; margin-bottom: 2rem;
    }
    .spots-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 2rem;
    }
    .spot-card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 24px;
      overflow: hidden;
      cursor: pointer;
      transition: all .3s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: var(--shadow);
    }
    .spot-card:hover {
      transform: translateY(-8px);
      box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
      border-color: var(--accent);
    }
    .spot-image {
      height: 200px;
      background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
      display: flex; align-items: center; justify-content: center;
      position: relative;
      font-size: 4rem;
    }
    .spot-type-badge {
      position: absolute; top: 1rem; left: 1rem;
      background: rgba(255, 255, 255, 0.9); color: var(--text);
      padding: 0.4rem 0.8rem; border-radius: 50px; font-size: 0.75rem; font-weight: 700;
      box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
    }
    .availability-badge {
      position: absolute; top: 1rem; right: 1rem;
      padding: 0.4rem 0.8rem; border-radius: 50px; font-size: 0.75rem; font-weight: 700;
      box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
    }
    .available { background: #dcfce7; color: #15803d; }
    .unavailable { background: #fee2e2; color: #b91c1c; }
    .spot-info { padding: 1.5rem; }
    h3 { color: var(--text); margin: 0 0 0.5rem; font-size: 1.15rem; font-weight: 700; }
    .spot-location { color: var(--text-muted); font-size: 0.9rem; margin-bottom: 0.75rem; font-weight: 500; }
    .spot-meta { font-size: 0.85rem; color: var(--text-muted); margin-bottom: 1.25rem; display: flex; align-items: center; gap: 0.5rem; }
    .spot-footer { display: flex; align-items: center; justify-content: space-between; padding-top: 1.25rem; border-top: 1px dashed var(--border); }
    .price-amount { color: var(--text); font-size: 1.3rem; font-weight: 800; }
    .price-unit { color: var(--text-muted); font-size: 0.9rem; }
    .btn-book {
      background: var(--accent);
      border: none; color: #fff;
      padding: 0.6rem 1.25rem; border-radius: 50px;
      cursor: pointer; font-size: 0.9rem; font-weight: 700;
      transition: all .2s;
      box-shadow: 0 4px 14px 0 rgba(255, 90, 95, 0.3);
    }
    .btn-book:hover:not(:disabled) { transform: scale(1.05); box-shadow: 0 6px 20px rgba(255, 90, 95, 0.4); }
    .btn-book:disabled { background: #cbd5e1; box-shadow: none; cursor: not-allowed; }
    .empty-state { 
      grid-column: 1/-1; 
      text-align: center; 
      padding: 5rem 2rem; 
      background: #f8fafc;
      border: 2px dashed var(--border);
      border-radius: 24px;
      margin-top: 1rem;
    }
    .empty-icon { font-size: 4rem; margin-bottom: 1.5rem; filter: grayscale(0.5); }
    .empty-state h3 { color: var(--text); font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem; }
    .empty-state p { color: var(--text-muted); font-size: 1rem; }
    .pagination {
      display: flex; align-items: center; justify-content: center; gap: 1.5rem;
      margin-top: 2.5rem; color: rgba(255,255,255,0.6);
    }
    .pagination button {
      background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);
      color: #fff; padding: 0.5rem 1.25rem; border-radius: 8px; cursor: pointer;
      transition: background .2s; font-family: inherit;
    }
    .pagination button:hover:not(:disabled) { background: rgba(255,255,255,0.12); }
    .pagination button:disabled { opacity: 0.3; cursor: not-allowed; }
  `]
})
export class SpotListComponent implements OnInit {
  spots: Spot[] = [];
  loading = true;
  error = '';
  currentPage = 0;
  totalPages = 0;

  constructor(private spotService: SpotService, private router: Router) {}

  ngOnInit(): void { this.loadSpots(); }

  loadSpots(): void {
    this.loading = true;
    this.error = '';
    this.spotService.getAll(this.currentPage).subscribe({
      next: page => {
        this.spots = page.content;
        this.totalPages = page.totalPages;
        this.loading = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar los alojamientos. ¿Está el backend corriendo?';
        this.loading = false;
      }
    });
  }

  selectSpot(spot: Spot): void {
    if (spot.isAvailable) this.router.navigate(['/book', spot.id]);
  }

  getIcon(type: string): string {
    const icons: Record<string, string> = { HOUSE: '🏡', APARTMENT: '🏢', ROOM: '🛏️' };
    return icons[type] ?? '🏠';
  }

  getTypeLabel(type: string): string {
    const labels: Record<string, string> = { HOUSE: 'Casa', APARTMENT: 'Apartamento', ROOM: 'Habitación' };
    return labels[type] ?? type;
  }

  prevPage(): void { if (this.currentPage > 0) { this.currentPage--; this.loadSpots(); } }
  nextPage(): void { if (this.currentPage < this.totalPages - 1) { this.currentPage++; this.loadSpots(); } }
}
