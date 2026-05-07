import http from 'k6/http';
import { check, sleep } from 'k6';

// 1. CONFIGURACIÓN DE LA PRUEBA DE ESTRÉS DE CPU
export const options = {
  scenarios: {
    login_stress_scenario: {
      executor: 'shared-iterations',
      vus: 200,          // 200 Usuarios Virtuales (hilos) concurrentes
      iterations: 200,   // 200 logins en total
      maxDuration: '30s',// Tiempo límite de la prueba
    },
  },
};

const BASE_URL = 'http://localhost:8080/api/v1/auth';

export default function () {
  // Payload estándar: el mismo usuario para los 200 hilos
  const payload = JSON.stringify({
    email: 'test@k6.com',
    password: 'password123'
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  // 2. DISPARAMOS EL LOGIN
  const res = http.post(`${BASE_URL}/login`, payload, params);

  // 3. MEDIMOS EL IMPACTO
  check(res, {
    'Estado es 200 (Login Exitoso)': (r) => r.status === 200,
    'Tiempo de respuesta < 1s (Saludable)': (r) => r.timings.duration < 1000,
    'Tiempo de respuesta < 3s (Con algo de lag)': (r) => r.timings.duration < 3000,
    'Estado es 500/503 (Servidor colapsó)': (r) => r.status === 500 || r.status === 503,
  });

  // Breve pausa
  sleep(0.1);
}
