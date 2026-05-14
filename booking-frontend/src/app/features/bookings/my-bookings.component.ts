import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../core/services/booking.service';
import { Booking } from '../../core/models/booking.model';

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-wrapper">
      <div class="page-header">
        <h1>Mis Reservas</h1>
        <p>Historial y estado de tus reservas</p>
      </div>

      <div class="loading" *ngIf="loading">
        <div class="spinner"></div>
        <p>Cargando reservas...</p>
      </div>

      <div class="bookings-list" *ngIf="!loading">
        <div class="empty-state" *ngIf="bookings.length === 0">
          <div class="empty-icon">📭</div>
          <h3>Sin reservas aún</h3>
          <p>Cuando hagas una reserva, aparecerá aquí.</p>
        </div>

        <div class="booking-card" *ngFor="let booking of bookings">
          <div class="booking-header">
            <div class="booking-id">#{{ booking.id }}</div>
            <div class="status-badge" [ngClass]="booking.status.toLowerCase()">
              {{ getStatusLabel(booking.status) }}
            </div>
          </div>

          <div class="booking-body">
            <div class="booking-dates">
              <div class="date-block">
                <span class="date-label">Check-in</span>
                <strong>{{ booking.checkInDate | date:'dd MMM yyyy' }}</strong>
              </div>
              <div class="date-divider">→</div>
              <div class="date-block">
                <span class="date-label">Check-out</span>
                <strong>{{ booking.checkOutDate | date:'dd MMM yyyy' }}</strong>
              </div>
            </div>

            <div class="booking-meta">
              <span>👥 {{ booking.numberOfGuests }} huéspedes</span>
              <span class="payment-badge" [ngClass]="booking.paymentStatus.toLowerCase()">
                {{ getPaymentLabel(booking.paymentStatus) }}
              </span>
            </div>

            <div class="booking-price">
              <span class="price">\${{ booking.totalPrice | number:'1.0-0' }}</span>
              <span class="price-label">total</span>
            </div>

            <div class="booking-requests" *ngIf="booking.specialRequests">
              <span class="req-label">Petición especial:</span>
              <span>{{ booking.specialRequests }}</span>
            </div>
          </div>

          <div class="booking-footer" *ngIf="booking.status !== 'CANCELLED'">
            <button class="btn-cancel" (click)="cancel(booking)" [disabled]="cancelling === booking.id">
              {{ cancelling === booking.id ? 'Cancelando...' : 'Cancelar reserva' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-wrapper { padding: 2rem; max-width: 800px; margin: 0 auto; }
    .page-header { margin-bottom: 2rem; }
    .page-header h1 { color: #fff; font-size: 2rem; font-weight: 700; margin: 0 0 0.5rem; }
    .page-header p { color: rgba(255,255,255,0.5); margin: 0; }
    .loading { display: flex; flex-direction: column; align-items: center; gap: 1rem; padding: 4rem; color: rgba(255,255,255,0.5); }
    .spinner { width: 40px; height: 40px; border: 3px solid rgba(108,71,255,0.2); border-top-color: #6c47ff; border-radius: 50%; animation: spin 0.8s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .bookings-list { display: flex; flex-direction: column; gap: 1.25rem; }
    .empty-state { 
      text-align: center; 
      padding: 5rem 2rem; 
      background: rgba(255,255,255,0.02);
      border: 2px dashed rgba(255,255,255,0.1);
      border-radius: 24px;
    }
    .empty-icon { font-size: 4rem; margin-bottom: 1.5rem; }
    .empty-state h3 { color: #fff; font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem; }
    .booking-card {
      background: rgba(255,255,255,0.04);
      border: 1px solid rgba(255,255,255,0.08);
      border-radius: 14px; overflow: hidden;
      transition: border-color .2s;
    }
    .booking-card:hover { border-color: rgba(255,255,255,0.15); }
    .booking-header {
      display: flex; justify-content: space-between; align-items: center;
      padding: 1rem 1.25rem;
      border-bottom: 1px solid rgba(255,255,255,0.06);
      background: rgba(255,255,255,0.02);
    }
    .booking-id { color: rgba(255,255,255,0.4); font-size: 0.85rem; font-weight: 600; }
    .status-badge {
      padding: 0.25rem 0.75rem; border-radius: 20px; font-size: 0.78rem; font-weight: 600;
    }
    .status-badge.pending { background: rgba(234,179,8,0.15); color: #fbbf24; }
    .status-badge.confirmed { background: rgba(34,197,94,0.15); color: #4ade80; }
    .status-badge.cancelled { background: rgba(239,68,68,0.15); color: #f87171; }
    .booking-body { padding: 1.25rem; display: flex; flex-direction: column; gap: 1rem; }
    .booking-dates { display: flex; align-items: center; gap: 1.5rem; }
    .date-block { display: flex; flex-direction: column; gap: 0.25rem; }
    .date-label { color: rgba(255,255,255,0.4); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .date-block strong { color: #fff; font-size: 0.95rem; }
    .date-divider { color: rgba(255,255,255,0.25); font-size: 1.2rem; }
    .booking-meta { display: flex; align-items: center; gap: 1rem; color: rgba(255,255,255,0.5); font-size: 0.85rem; }
    .payment-badge { padding: 0.2rem 0.6rem; border-radius: 6px; font-size: 0.75rem; font-weight: 600; }
    .payment-badge.pending_payment { background: rgba(234,179,8,0.12); color: #fbbf24; }
    .payment-badge.paid { background: rgba(34,197,94,0.12); color: #4ade80; }
    .payment-badge.refunded { background: rgba(99,102,241,0.12); color: #a5b4fc; }
    .booking-price { display: flex; align-items: baseline; gap: 0.4rem; }
    .price { color: #fff; font-size: 1.4rem; font-weight: 700; }
    .price-label { color: rgba(255,255,255,0.4); font-size: 0.85rem; }
    .booking-requests { color: rgba(255,255,255,0.45); font-size: 0.85rem; font-style: italic; }
    .req-label { color: rgba(255,255,255,0.3); margin-right: 0.4rem; }
    .booking-footer { padding: 0.75rem 1.25rem; border-top: 1px solid rgba(255,255,255,0.06); }
    .btn-cancel {
      background: rgba(239,68,68,0.1); border: 1px solid rgba(239,68,68,0.25);
      color: #f87171; padding: 0.45rem 1rem; border-radius: 8px;
      cursor: pointer; font-size: 0.85rem; transition: background .2s; font-family: inherit;
    }
    .btn-cancel:hover:not(:disabled) { background: rgba(239,68,68,0.2); }
    .btn-cancel:disabled { opacity: 0.5; cursor: not-allowed; }
  `]
})
export class MyBookingsComponent implements OnInit {
  bookings: Booking[] = [];
  loading = true;
  cancelling: number | null = null;

  constructor(private bookingService: BookingService) {}

  ngOnInit(): void {
    this.bookingService.getMyBookings().subscribe({
      next: page => { this.bookings = page.content; this.loading = false; },
      error: () => this.loading = false
    });
  }

  cancel(booking: Booking): void {
    this.cancelling = booking.id;
    this.bookingService.cancel(booking.id).subscribe({
      next: updated => {
        const idx = this.bookings.findIndex(b => b.id === booking.id);
        if (idx !== -1) this.bookings[idx] = updated;
        this.cancelling = null;
      },
      error: () => this.cancelling = null
    });
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = { PENDING: 'Pendiente', CONFIRMED: 'Confirmada', CANCELLED: 'Cancelada' };
    return labels[status] ?? status;
  }

  getPaymentLabel(status: string): string {
    const labels: Record<string, string> = { PENDING_PAYMENT: 'Pago pendiente', PAID: 'Pagada', REFUNDED: 'Reembolsada' };
    return labels[status] ?? status;
  }
}
