# Documentacion - Colegio Bernardo O'Higgins

## Estructura

```
docs/
├── informe/               # Informe formal de la Experiencia 3
│   └── informe-evaluacion.md
└── postman/               # Colecciones Postman
    ├── api-gateway-openapi.json
    ├── api-gateway-postman.json
    ├── servicio-academico-openapi.json
    ├── servicio-academico-postman.json
    ├── servicio-asistencia-openapi.json
    ├── servicio-asistencia-postman.json
    ├── servicio-comunicaciones-openapi.json
    ├── servicio-comunicaciones-postman.json
    └── postman-collection-completa.json   # Coleccion maestra (76 endpoints)
```

## Recursos

| Recurso | URL |
|---------|-----|
| Swagger UI (Gateway) | http://localhost:9090/swagger-ui.html |
| SonarQube Dashboard | http://localhost:9000 |
| Frontend (Dev) | http://localhost:5173 |
| Frontend (Docker) | http://localhost:3000 |

## Colecciones Postman

Importar `docs/postman/postman-collection-completa.json` en Postman.
Usar la variable `{{token}}` con un JWT valido obtenido de `POST /auth/login`.
