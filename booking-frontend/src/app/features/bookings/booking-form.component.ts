import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SpotService } from '../../core/services/spot.service';
import { BookingService } from '../../core/services/booking.service';
import { Spot } from '../../core/models/spot.model';

@Component({
  selector: 'app-booking-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="page-wrapper">
      <button class="btn-back" (click)="goBack()">← Volver</button>

      <div class="booking-layout">
        <!-- Resumen del spot -->
        <div class="spot-summary" *ngIf="spot">
          <div class="spot-icon-big">{{ getIcon(spot.typeSpot) }}</div>
          <h2>{{ spot.name }}</h2>
          <p class="location">📍 {{ spot.location }}</p>
          <div class="spot-details">
            <div class="detail"><span>Tipo</span><strong>{{ getTypeLabel(spot.typeSpot) }}</strong></div>
            <div class="detail"><span>Capacidad máx.</span><strong>{{ spot.maxCapacity }} personas</strong></div>
            <div class="detail"><span>Precio por noche</span><strong>\${{ spot.pricePerNight | number:'1.0-0' }}</strong></div>
          </div>
        </div>

        <!-- Formulario de reserva -->
        <div class="booking-form-card">
          <h2>Datos de la Reserva</h2>

          <form [formGroup]="form" (ngSubmit)="onSubmit()">
            <div class="field-row">
              <div class="field">
                <label for="checkIn">Check-in</label>
                <input id="checkIn" type="date" formControlName="checkInDate" [min]="tomorrow">
              </div>
              <div class="field">
                <label for="checkOut">Check-out</label>
                <input id="checkOut" type="date" formControlName="checkOutDate" [min]="tomorrow">
              </div>
            </div>

            <div class="field">
              <label for="guests">Número de huéspedes</label>
              <input id="guests" type="number" formControlName="numberOfGuests" min="1" [max]="spot?.maxCapacity ?? 100">
            </div>

            <div class="field">
              <label for="requests">Peticiones especiales (opcional)</label>
              <textarea id="requests" formControlName="specialRequests" rows="3" placeholder="Cuna, llegada tarde..."></textarea>
            </div>

            <!-- Resumen de precio -->
            <div class="price-summary" *ngIf="totalNights > 0 && spot">
              <div class="price-row"><span>{{ totalNights }} noches × \${{ spot.pricePerNight | number:'1.0-0' }}</span><span>\${{ totalPrice | number:'1.0-0' }}</span></div>
              <div class="price-row total"><span>Total</span><span>\${{ totalPrice | number:'1.0-0' }}</span></div>
            </div>

            <div class="error-msg" *ngIf="errorMsg">⚠️ {{ errorMsg }}</div>
            <div class="success-msg" *ngIf="successMsg">✅ {{ successMsg }}</div>

            <button type="submit" [disabled]="form.invalid || loading" class="btn-submit">
              <span *ngIf="!loading">Confirmar Reserva · \${{ totalPrice | number:'1.0-0' }}</span>
              <span *ngIf="loading">Procesando...</span>
            </button>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-wrapper { padding: 2rem; max-width: 1000px; margin: 0 auto; }
    .btn-back {
      background: none; border: 1px solid rgba(255,255,255,0.15);
      color: rgba(255,255,255,0.6); padding: 0.5rem 1rem;
      border-radius: 8px; cursor: pointer; margin-bottom: 2rem;
      transition: all .2s; font-family: inherit; font-size: 0.9rem;
    }
    .btn-back:hover { color: #fff; border-color: rgba(255,255,255,0.3); }
    .booking-layout { display: grid; grid-template-columns: 1fr 1.5fr; gap: 2rem; }
    @media (max-width: 768px) { .booking-layout { grid-template-columns: 1fr; } }
    .spot-summary {
      background: rgba(108,71,255,0.08);
      border: 1px solid rgba(108,71,255,0.2);
      border-radius: 16px; padding: 2rem; text-align: center;
    }
    .spot-icon-big { font-size: 4rem; margin-bottom: 1rem; }
    .spot-summary h2 { color: #fff; margin: 0 0 0.5rem; font-size: 1.3rem; }
    .location { color: rgba(255,255,255,0.5); font-size: 0.9rem; margin: 0 0 1.5rem; }
    .spot-details { display: flex; flex-direction: column; gap: 0.75rem; }
    .detail { display: flex; justify-content: space-between; }
    .detail span { color: rgba(255,255,255,0.45); font-size: 0.85rem; }
    .detail strong { color: #fff; font-size: 0.9rem; }
    .booking-form-card {
      background: rgba(255,255,255,0.04);
      border: 1px solid rgba(255,255,255,0.08);
      border-radius: 16px; padding: 2rem;
    }
    .booking-form-card h2 { color: #fff; margin: 0 0 1.5rem; font-size: 1.3rem; }
    form { display: flex; flex-direction: column; gap: 1.25rem; }
    .field-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .field { display: flex; flex-direction: column; gap: 0.5rem; }
    label { color: rgba(255,255,255,0.6); font-size: 0.85rem; }
    input, textarea, select {
      background: rgba(255,255,255,0.06);
      border: 1px solid rgba(255,255,255,0.1);
      border-radius: 10px; padding: 0.75rem 1rem;
      color: #fff; font-size: 0.95rem; outline: none;
      transition: border .2s; font-family: inherit;
      color-scheme: dark;
    }
    input:focus, textarea:focus { border-color: rgba(108,71,255,0.6); }
    textarea { resize: vertical; }
    .price-summary {
      background: rgba(108,71,255,0.1);
      border: 1px solid rgba(108,71,255,0.2);
      border-radius: 10px; padding: 1rem;
    }
    .price-row { display: flex; justify-content: space-between; color: rgba(255,255,255,0.6); font-size: 0.9rem; margin-bottom: 0.5rem; }
    .price-row.total { color: #fff; font-weight: 700; font-size: 1rem; margin-bottom: 0; padding-top: 0.5rem; border-top: 1px solid rgba(255,255,255,0.1); }
    .error-msg { background: rgba(255,70,70,0.1); border: 1px solid rgba(255,70,70,0.2); color: #ff8080; border-radius: 8px; padding: 0.75rem 1rem; font-size: 0.85rem; }
    .success-msg { background: rgba(34,197,94,0.1); border: 1px solid rgba(34,197,94,0.2); color: #4ade80; border-radius: 8px; padding: 0.75rem 1rem; font-size: 0.85rem; }
    .btn-submit {
      background: linear-gradient(135deg, #6c47ff, #a855f7);
      border: none; color: #fff;
      padding: 0.9rem; border-radius: 10px;
      font-size: 1rem; font-weight: 600;
      cursor: pointer; transition: opacity .2s; font-family: inherit;
    }
    .btn-submit:hover:not(:disabled) { opacity: 0.88; }
    .btn-submit:disabled { opacity: 0.4; cursor: not-allowed; }
  `]
})
export class BookingFormComponent implements OnInit {
  form: FormGroup;
  spot: Spot | null = null;
  loading = false;
  errorMsg = '';
  successMsg = '';
  tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private spotService: SpotService,
    private bookingService: BookingService
  ) {
    this.form = this.fb.group({
      checkInDate: ['', Validators.required],
      checkOutDate: ['', Validators.required],
      numberOfGuests: [1, [Validators.required, Validators.min(1)]],
      specialRequests: ['']
    });
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('spotId'));
    this.spotService.getById(id).subscribe({
      next: spot => this.spot = spot,
      error: () => this.router.navigate(['/spots'])
    });
  }

  get totalNights(): number {
    const checkIn = this.form.value.checkInDate;
    const checkOut = this.form.value.checkOutDate;
    if (!checkIn || !checkOut) return 0;
    const diff = new Date(checkOut).getTime() - new Date(checkIn).getTime();
    return Math.max(0, Math.floor(diff / 86400000));
  }

  get totalPrice(): number {
    return this.totalNights * (this.spot?.pricePerNight ?? 0);
  }

  onSubmit(): void {
    if (this.form.invalid || !this.spot) return;
    this.loading = true;
    this.errorMsg = '';
    this.successMsg = '';

    const guestId = localStorage.getItem('userId') ?? '';
    this.bookingService.create({
      guestId,
      spotId: this.spot.id,
      ...this.form.value
    }).subscribe({
      next: () => {
        this.successMsg = '¡Reserva creada exitosamente! Redirigiendo...';
        setTimeout(() => this.router.navigate(['/my-bookings']), 1800);
      },
      error: err => {
        this.errorMsg = err.error?.message ?? 'Error al crear la reserva.';
        this.loading = false;
      }
    });
  }

  getIcon(type: string): string {
    const icons: Record<string, string> = { HOUSE: '🏡', APARTMENT: '🏢', ROOM: '🛏️' };
    return icons[type] ?? '🏠';
  }

  getTypeLabel(type: string): string {
    const labels: Record<string, string> = { HOUSE: 'Casa', APARTMENT: 'Apartamento', ROOM: 'Habitación' };
    return labels[type] ?? type;
  }

  goBack(): void { this.router.navigate(['/spots']); }
}
