import http from 'k6/http';
import { check, sleep } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://localhost:9090';

export const options = {
  vus: 50,
  duration: '2m',
  thresholds: {
    http_req_duration: ['avg<2000', 'p(95)<2000'],
    http_req_failed: ['rate<0.01'],
  },
};

export function setup() {
  if (!__ENV.RUT || !__ENV.PASSWORD) {
    throw new Error('Define RUT y PASSWORD para ejecutar la prueba con una cuenta UTP de prueba.');
  }
  const response = http.post(
    `${baseUrl}/auth/login`,
    JSON.stringify({ rut: __ENV.RUT, password: __ENV.PASSWORD }),
    { headers: { 'Content-Type': 'application/json' } },
  );
  check(response, { 'login de prueba responde 200': (res) => res.status === 200 });
  if (response.status !== 200) {
    throw new Error(`No se pudo autenticar la cuenta de prueba: HTTP ${response.status}`);
  }
  return { token: response.json('token') };
}

export default function (data) {
  const params = { headers: { Authorization: `Bearer ${data.token}` } };
  for (const path of ['/academico/api/estudiantes', '/academico/api/horarios', '/academico/api/notas']) {
    const response = http.get(`${baseUrl}${path}`, params);
    check(response, { [`${path} responde 2xx`]: (res) => res.status >= 200 && res.status < 300 });
  }
  sleep(1);
}
