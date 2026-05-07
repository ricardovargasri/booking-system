import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

// ─── CONFIGURACIÓN ────────────────────────────────────────────────────────────
const BASE_URL = 'http://localhost:8080/api/v1';

// Usuario ya existente en tu BD para autenticarse
const TEST_EMAIL    = 'test@k6.com';
const TEST_PASSWORD = '123456';

// ID del Spot a intentar reservar (debe existir en la BD y estar disponible)
const SPOT_ID = 1;

// Fechas en el futuro que NO choquen con otras reservas
const CHECK_IN  = '2035-08-01';
const CHECK_OUT = '2035-08-10';

// ─── ESCENARIO ────────────────────────────────────────────────────────────────
export const options = {
  scenarios: {
    overbooking_attack: {
      executor: 'shared-iterations',
      vus: 50,        // 50 usuarios simultáneos
      iterations: 50, // 1 intento de reserva por usuario
      maxDuration: '15s',
    },
  },
  thresholds: {
    // Solo 1 reserva debe crearse correctamente
    'checks{check:Reserva creada (201)}': ['count<=1'],
    // Todas las demás deben ser rechazadas con 400 o 409
    'checks{check:Overbooking rechazado (400/409)}': ['count>=49'],
  },
};

// ─── SETUP: Se ejecuta UNA sola vez antes del test ───────────────────────────
export function setup() {
  // 1. Hacer login para obtener un token válido
  const loginRes = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify({ email: TEST_EMAIL, password: TEST_PASSWORD }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  const loginOk = check(loginRes, {
    'Login exitoso (200)': (r) => r.status === 200,
  });

  if (!loginOk) {
    console.error(`❌ Login falló: ${loginRes.status} ${loginRes.body}`);
    return { token: null };
  }

  const token = loginRes.json('token');
  console.log(`✅ Token obtenido. Atacando spot ${SPOT_ID} con 50 VUs...`);
  return { token };
}

// ─── TEST PRINCIPAL: Se ejecuta 50 veces en paralelo ─────────────────────────
export default function (data) {
  if (!data.token) {
    console.error('No hay token, abortando VU.');
    return;
  }

  const payload = JSON.stringify({
    spotId: SPOT_ID,
    checkInDate: CHECK_IN,
    checkOutDate: CHECK_OUT,
    numberOfGuests: 1,
    specialRequests: `Ataque de overbooking - VU ${__VU}`,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${data.token}`,
    },
  };

  const res = http.post(`${BASE_URL}/bookings`, payload, params);

  console.log(`VU ${__VU} -> Status: ${res.status}`);

  check(res, {
    'Reserva creada (201)':          (r) => r.status === 201 || r.status === 200,
    'Overbooking rechazado (400/409)': (r) => r.status === 400 || r.status === 409,
    'Sin crash del servidor (no 500)': (r) => r.status !== 500,
  });

  sleep(0.1);
}

// ─── TEARDOWN: Resumen al final ───────────────────────────────────────────────
export function teardown(data) {
  console.log('✅ Test de overbooking completado.');
  console.log('   → Solo 1 reserva debería haberse creado.');
  console.log('   → Las otras 49 deben haberse rechazado con 400/409.');
}
