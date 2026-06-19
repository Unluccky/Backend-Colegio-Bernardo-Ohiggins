# Backend - Colegio Bernardo O'Higgins

Sistema de gestión escolar desarrollado con una **arquitectura de microservicios** sobre **Spring Boot**, que centraliza la administración de estudiantes, profesores, apoderados, asignaturas, evaluaciones, notas, asistencia, anotaciones, horarios, mensajería y notificaciones del colegio.

El sistema expone toda su funcionalidad a través de un **API Gateway** con autenticación **JWT**, y se comunica de forma síncrona (REST/Feign) y asíncrona (**RabbitMQ**) entre servicios.

## Repositorios del proyecto

| Repositorio | Descripción |
|---|---|
| [Frontend-Colegio-Bernardo-Ohiggins](https://github.com/Unluccky/Frontend-Colegio-Bernardo-Ohiggins) (React + Vite + TailwindCSS) | API Gateway + microservicios (Spring Boot) |
| [Documentacion-Colegio-Bernardo-Ohiggins](https://github.com/Unluccky/Documentacion-Colegio-Bernardo-Ohiggins.git) | Informe, diagramas y documentación del proyecto |

---

## Arquitectura del sistema

```
Cliente Web (React + Vite)
        │
        ▼
  API Gateway (Puerto 9090) ── Autenticación JWT, enrutamiento, Swagger unificado
        │
        ├──────────────┬──────────────────┐
        ▼              ▼                  ▼
  Servicio          Servicio          Servicio
  Académico         Asistencia        Comunicaciones
  (8081)            (8082)            (8083)
     │                  │                  │
     ▼                  ▼                  ▼
   MySQL              MySQL            MongoDB
(db_academico)    (db_asistencia)  (db_comunicaciones)
                       │                  ▲
                       └──── RabbitMQ ────┘
                       (evento AnotacionEvent)
```

| Servicio | Puerto | Tecnología | Base de datos | Responsabilidad |
|---|---|---|---|---|
| **api-gateway** | `9090` | Spring Boot 4 + Spring Cloud Gateway + Security | — | Login/JWT, enrutamiento a microservicios, Circuit Breaker, Swagger agregado |
| **servicio-academico** | `8081` | Spring Boot 4 + JPA | MySQL (`db_academico`) | Estudiantes, profesores, apoderados, asignaturas, evaluaciones, notas, horarios |
| **servicio-asistencia** | `8082` | Spring Boot 4 + JPA + OpenFeign | MySQL (`db_asistencia`) | Asistencias y anotaciones de conducta |
| **servicio-comunicaciones** | `8083` | Spring Boot 4 + Spring Data MongoDB | MongoDB (`db_comunicaciones`) | Mensajería interna y notificaciones |
| **frontend** | `3000` (Docker) / `5173` (dev) | React + Vite + TailwindCSS | — | Interfaz de usuario ([repositorio del frontend](https://github.com/Unluccky/Frontend-Colegio-Bernardo-Ohiggins)) |

`servicio-asistencia` consulta a `servicio-academico` vía **Feign Client** (con *fallback* y Circuit Breaker de Resilience4j) para validar datos de estudiantes, y publica eventos `AnotacionEvent` por **RabbitMQ** cada vez que se crea una anotación; `servicio-comunicaciones` los consume para generar notificaciones automáticas.

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje de los microservicios |
| Spring Boot | 4.0.6 | Framework base de todos los servicios |
| Spring Cloud Gateway (WebMVC) | 2025.1.1 | Enrutamiento del API Gateway |
| Spring Security + JWT (jjwt) | 0.12.6 | Autenticación y autorización por roles |
| Spring Data JPA | — | Persistencia en `servicio-academico` y `servicio-asistencia` |
| Spring Data MongoDB | — | Persistencia en `servicio-comunicaciones` |
| Spring Cloud OpenFeign | — | Comunicación síncrona `asistencia → academico` |
| Resilience4j | — | Circuit Breaker / TimeLimiter |
| Spring AMQP (RabbitMQ) | — | Mensajería asíncrona entre `asistencia` y `comunicaciones` |
| MySQL | 8.0 | Bases de datos de Académico y Asistencia |
| MongoDB | 6.0 | Base de datos de Comunicaciones |
| RabbitMQ | 3 (management) | Broker de eventos |
| SpringDoc OpenAPI | 2.6.0 | Documentación Swagger por servicio y agregada en el Gateway |
| Lombok | — | Reducción de código boilerplate |
| JUnit 5 + Mockito + AssertJ + MockMvc | — | Pruebas unitarias y de integración |
| JaCoCo | 0.8.11 | Cobertura de pruebas |
| SonarQube | Community | Análisis de calidad de código |
| Docker + Docker Compose | — | Orquestación de los 10 contenedores del sistema |
| Maven | — | Gestión de dependencias y build |

---

## Estructura del proyecto

```
colegio-bernardo-ohiggins/
├── api-gateway/                    # Gateway + autenticación JWT
│   └── src/main/java/com/colegio/api_gateway/
│       ├── config/                 # OpenApiConfig, RestTemplateConfig
│       └── security/               # AuthController, AuthService, JwtUtil,
│                                    # JwtAuthenticationFilter, SecurityConfig, UtpController
│
├── servicio-academico/             # Estudiantes, profesores, apoderados, notas, etc.
│   └── src/main/java/com/colegio/servicio_academico/
│       ├── controller/             # Estudiante, Profesor, Apoderado, Asignatura,
│       │                           # Evaluacion, Nota, Horario, Auth
│       ├── model/                  # Entidades JPA
│       ├── repository/             # Spring Data JPA
│       ├── service / service/impl/ # Lógica de negocio
│       └── config/                 # DataSeeder, GlobalExceptionHandler, OpenApiConfig
│
├── servicio-asistencia/            # Asistencias y anotaciones de conducta
│   └── src/main/java/com/colegio/servicio_asistencia/
│       ├── controller/             # AsistenciaController, AnotacionController
│       ├── feign/                  # AcademicoFeignClient + Fallback
│       ├── messaging/              # AnotacionEvent, Publisher, RabbitMQConfig
│       ├── model/ │ repository/ │ service/
│       └── config/                 # DataSeeder, GlobalExceptionHandler, OpenApiConfig
│
├── servicio-comunicaciones/        # Mensajería y notificaciones
│   └── src/main/java/com/colegio/servicio_comunicaciones/
│       ├── controller/             # MensajeController, NotificacionController
│       ├── messaging/              # AnotacionEvent, AnotacionEventConsumer, RabbitMQConfig
│       ├── model/ │ repository/ │ service/
│       └── config/                 # DataSeeder, GlobalExceptionHandler, OpenApiConfig
│
├── docs/
│   ├── informe/                    # Informe de evaluación del proyecto
│   └── postman/                    # Colecciones Postman + specs OpenAPI por servicio
│
├── scripts/
│   ├── sonar-analysis.sh           # Ejecuta el análisis SonarQube de los 4 servicios
│   └── convert-openapi-to-postman.py
│
├── docker-compose.yml              # Orquesta los 10 contenedores del sistema
└── package.json
```

---

## Requisitos previos

- **Docker** y **Docker Compose** (forma recomendada de ejecutar el proyecto completo)
- **Java 21+** y **Maven** (solo si se quiere ejecutar algún servicio fuera de Docker)
- **Node.js 18+** (solo si se quiere ejecutar el [frontend](https://github.com/Unluccky/Frontend-Colegio-Bernardo-Ohiggins) en modo desarrollo)

---

## Instalación y ejecución con Docker (recomendado)

### 1. Clonar el repositorio

```bash
git clone <URL-DE-ESTE-REPOSITORIO>
cd colegio-bernardo-ohiggins
```

### 2. Levantar todos los servicios

```bash
docker-compose up -d
```

Esto construye y levanta **10 contenedores**: los 4 microservicios Java, el frontend, las bases de datos (MySQL ×2, MongoDB), RabbitMQ y SonarQube. La primera vez puede tardar unos minutos mientras se compilan las imágenes y las bases de datos pasan su *healthcheck*.

### 3. Verificar el estado de los contenedores

```bash
docker-compose ps
docker-compose logs -f [nombre-del-servicio]
```

### 4. Detener el entorno

```bash
docker-compose down       # Detiene los contenedores
docker-compose down -v    # Detiene y elimina también los volúmenes (borra los datos)
```

### URLs disponibles tras el despliegue

| Recurso | URL |
|---|---|
| Frontend | http://localhost:3000 |
| API Gateway | http://localhost:9090 |
| Swagger UI (agregado, todos los servicios) | http://localhost:9090/swagger-ui.html |
| Servicio Académico (directo) | http://localhost:8081 |
| Servicio Asistencia (directo) | http://localhost:8082 |
| Servicio Comunicaciones (directo) | http://localhost:8083 |
| RabbitMQ Management | http://localhost:25672 (`admin` / `admin`) |
| SonarQube | http://localhost:9000 |

---

## Ejecución local (desarrollo, sin Docker)

Cada microservicio es un proyecto Maven independiente. Para correrlo localmente se necesita tener disponibles sus dependencias (MySQL, MongoDB y/o RabbitMQ según el servicio) en `localhost`.

```bash
cd servicio-academico
./mvnw spring-boot:run        # Puerto 8081

cd ../servicio-asistencia
./mvnw spring-boot:run        # Puerto 8082

cd ../servicio-comunicaciones
./mvnw spring-boot:run        # Puerto 8083

cd ../api-gateway
./mvnw spring-boot:run        # Puerto 9090
```

> En este modo, `servicio-academico` y `servicio-asistencia` se conectan por defecto a `jdbc:mysql://localhost:3306/...`, y `servicio-comunicaciones` a `mongodb://localhost:27017/db_comunicaciones`. Estas URLs se sobrescriben con variables de entorno cuando se ejecuta vía Docker Compose (ver tabla más abajo).

### Ejecutar las pruebas unitarias

```bash
cd servicio-academico && ./mvnw clean verify
cd servicio-asistencia && ./mvnw clean verify
cd servicio-comunicaciones && ./mvnw clean verify
cd api-gateway && ./mvnw clean verify
```

`mvn clean verify` ejecuta los tests con JUnit 5 + Mockito + MockMvc y genera el reporte de cobertura **JaCoCo** en `target/site/jacoco`.

### Análisis de calidad con SonarQube

```bash
# 1. Levantar SonarQube: docker-compose up -d sonarqube
# 2. Generar un token en http://localhost:9000/account/security
export SONAR_TOKEN=tu_token_aqui
bash scripts/sonar-analysis.sh
```

---

## Variables de entorno principales

| Variable | Servicio | Descripción | Valor por defecto (local) |
|---|---|---|---|
| `SPRING_DATASOURCE_URL` | academico / asistencia | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/db_<servicio>` |
| `SPRING_DATASOURCE_USERNAME` / `_PASSWORD` | academico / asistencia | Credenciales MySQL | `root` / *(vacío)* |
| `SPRING_DATA_MONGODB_URI` | comunicaciones | URI de conexión a MongoDB | `mongodb://localhost:27017/db_comunicaciones` |
| `ACADEMICO_SERVICE_URL` | asistencia, api-gateway | URL del servicio académico para Feign / validación de credenciales | `http://localhost:8081` |
| `SPRING_RABBITMQ_HOST` / `_PORT` | asistencia, comunicaciones | Conexión al broker RabbitMQ | `localhost` / `5672` |
| `SPRING_RABBITMQ_USERNAME` / `_PASSWORD` | asistencia, comunicaciones | Credenciales RabbitMQ | `admin` / `admin` |
| `jwt.secret` | api-gateway | Clave de firma de los tokens JWT | definida en `application.properties` |

---

## Autenticación y seguridad

El **API Gateway** es el único punto de entrada autenticado del sistema:

- `POST /auth/login` recibe `{ "rut": "...", "password": "..." }`, valida las credenciales contra `servicio-academico` (o contra la lista interna de usuarios UTP) y devuelve un **JWT** junto con el `rut` y el `role` del usuario.
- El `JwtAuthenticationFilter` intercepta cada petición, valida el token y carga el rol en el contexto de seguridad de Spring.
- Las contraseñas se almacenan con **BCrypt**.
- CORS está habilitado para `http://localhost:*` y `http://127.0.0.1:*`.

### Roles y permisos (definidos en `SecurityConfig`)

| Recurso | Lectura (GET) | Escritura (POST/PUT/DELETE) |
|---|---|---|
| `/admin/usuarios-utp/**` | Cualquier usuario autenticado | Solo `UTP` |
| `/academico/api/estudiantes`, `profesores`, `apoderados`, `asignaturas`, `horarios` | `UTP`, `PROFESOR`, `ALUMNO`, `APODERADO` | Solo `UTP` |
| `/academico/api/notas`, `evaluaciones` | `UTP`, `PROFESOR`, `ALUMNO`, `APODERADO` | `UTP`, `PROFESOR` |
| `/asistencia/**` (asistencias y anotaciones) | `UTP`, `PROFESOR`, `ALUMNO`, `APODERADO` | `UTP`, `PROFESOR` |
| `/comunicaciones/**` (mensajes y notificaciones) | Cualquier usuario autenticado | Cualquier usuario autenticado |
| `/auth/login`, Swagger/OpenAPI | Público | — |

### Credenciales de prueba (datos de ejemplo / `DataSeeder`)

> Solo para ambiente de desarrollo. **No usar en producción.**

| Rol | RUT | Contraseña |
|---|---|---|
| UTP | `77777777-7` | `utp123` |
| Profesor (ejemplo) | `11111111-1` | `profesor123` |
| Alumno (ejemplo) | `22222222-2` | `alumno123` |
| Apoderado | — | `apoderado123` |

---

## Endpoints principales

Todos los endpoints se consumen a través del **API Gateway** (`http://localhost:9090`), que aplica el prefijo correspondiente y reenvía la petición al microservicio (`StripPrefix=1`).

### Autenticación (`api-gateway`)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/auth/login` | Inicia sesión y devuelve el JWT |
| POST | `/auth/cambiar-contrasena` | Cambia la contraseña del usuario autenticado |
| POST | `/auth/resetear-contrasena` | Resetea la contraseña de un usuario (uso UTP / "olvidé mi contraseña") |
| GET | `/admin/usuarios-utp` | Lista los usuarios UTP |
| POST | `/admin/usuarios-utp` | Crea un usuario UTP |
| DELETE | `/admin/usuarios-utp/{rut}` | Elimina un usuario UTP |

### Servicio Académico (`/academico/api/...`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET / POST | `/estudiantes` | Listar / crear estudiantes |
| GET / PUT / DELETE | `/estudiantes/{id}` | Obtener, actualizar o eliminar un estudiante |
| GET | `/estudiantes/rut/{rut}` | Buscar estudiante por RUT |
| GET / POST | `/profesores` | Listar / crear profesores |
| GET / PUT / DELETE | `/profesores/{id}` | Obtener, actualizar o eliminar un profesor |
| GET / POST | `/apoderados` | Listar / crear apoderados |
| GET / PUT / DELETE | `/apoderados/{id}` | Obtener, actualizar o eliminar un apoderado |
| GET | `/apoderados/estudiante/{estudianteId}` | Apoderados de un estudiante |
| GET / POST | `/asignaturas` | Listar / crear asignaturas |
| GET / PUT / DELETE | `/asignaturas/{id}` | Obtener, actualizar o eliminar una asignatura |
| GET / POST | `/evaluaciones` | Listar / crear evaluaciones |
| GET / PUT / DELETE | `/evaluaciones/{id}` | Obtener, actualizar o eliminar una evaluación |
| GET / POST | `/notas` | Listar / registrar notas |
| GET / DELETE | `/notas/{id}` | Obtener o eliminar una nota |
| GET | `/notas/estudiante/{estudianteId}` | Notas de un estudiante |
| GET / POST | `/horarios` | Listar / crear bloques de horario |
| GET / PUT / DELETE | `/horarios/{id}` | Obtener, actualizar o eliminar un horario |
| GET | `/horarios/curso/{curso}` | Horario completo de un curso |
| GET | `/horarios/profesor/{profesorId}` | Horario de un profesor |
| GET | `/horarios/curso/{curso}/dia/{dia}` | Horario de un curso en un día específico |
| POST | `/auth/validar` | Validación de credenciales (uso interno del Gateway) |

### Servicio Asistencia (`/asistencia/api/...`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET / POST | `/asistencias` | Listar / registrar asistencias |
| POST | `/asistencias/batch` | Registro masivo de asistencias (por curso/clase) |
| GET / PUT / DELETE | `/asistencias/{id}` | Obtener, actualizar o eliminar una asistencia |
| GET | `/asistencias/estudiante/{estudianteId}` | Asistencias de un estudiante |
| GET | `/asistencias/por-clase` | Asistencias filtradas por clase/fecha |
| GET / POST | `/anotaciones` | Listar / crear anotaciones de conducta |
| GET / PUT / DELETE | `/anotaciones/{id}` | Obtener, actualizar o eliminar una anotación |
| GET | `/anotaciones/estudiante/{estudianteId}` | Anotaciones de un estudiante |

### Servicio Comunicaciones (`/comunicaciones/api/...`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET / POST | `/mensajes` | Listar / enviar mensajes |
| GET / PUT / DELETE | `/mensajes/{id}` | Obtener, actualizar o eliminar un mensaje |
| GET | `/mensajes/usuario/{usuarioId}` | Mensajes enviados/recibidos por un usuario |
| GET | `/mensajes/destinatario/{destinatarioId}` | Mensajes recibidos por un destinatario |
| GET / POST | `/notificaciones` | Listar / crear notificaciones |
| GET / DELETE | `/notificaciones/{id}` | Obtener o eliminar una notificación |
| GET | `/notificaciones/destinatario/{destinatarioId}` | Notificaciones de un destinatario |

---

## Documentación interactiva (Swagger / Postman)

- **Swagger UI agregado:** http://localhost:9090/swagger-ui.html (combina la documentación del Gateway y de los 3 microservicios en una sola interfaz).
- **Swagger por servicio:** disponible en `/swagger-ui.html` del puerto correspondiente (`8081`, `8082`, `8083`).
- **Colección Postman:** importar `docs/postman/postman-collection-completa.json` (76 endpoints). Para autenticarse:
  1. Ejecutar `POST http://localhost:9090/auth/login` con un usuario válido.
  2. Copiar el `token` recibido en la variable `{{token}}` de la colección.

---

## Comunicación entre microservicios

- **Síncrona (REST + Feign):** `servicio-asistencia` consulta a `servicio-academico` mediante `AcademicoFeignClient` para validar estudiantes y apoderados. Si el servicio académico no responde, se activa el **Circuit Breaker** de Resilience4j y se ejecuta el `AcademicoFeignClientFallback`.
- **Asíncrona (RabbitMQ):** al crear una anotación, `servicio-asistencia` publica un evento `AnotacionEvent`; `servicio-comunicaciones` lo consume (`AnotacionEventConsumer`) y genera automáticamente una notificación para el destinatario correspondiente.
- **Manejo de errores:** los tres microservicios académico/asistencia/comunicaciones implementan un `GlobalExceptionHandler` que traduce las excepciones de negocio en respuestas HTTP coherentes (por ejemplo, `404 Not Found`).

---

## Testing y calidad de código

- **22 clases de prueba** con JUnit 5, Mockito y MockMvc, cubriendo controladores y servicios de los 4 módulos (api-gateway, académico, asistencia, comunicaciones).
- **JaCoCo** genera el reporte de cobertura en cada build (`mvn clean verify` → `target/site/jacoco/index.html`).
- **SonarQube** analiza bugs, code smells, vulnerabilidades y duplicación de código; se ejecuta vía `scripts/sonar-analysis.sh` contra la instancia local en `http://localhost:9000`.
- Cada microservicio define su propia clave de proyecto en `sonar-project.properties` (`colegio-api-gateway`, `colegio-servicio-academico`, `colegio-servicio-asistencia`, `colegio-servicio-comunicaciones`).

---

## Autores

**José Muñoz**
**Sebastián Santander**

Proyecto grupal — Evaluación Parcial N°3
Asignatura: Desarrollo Fullstack III — DuocUC
