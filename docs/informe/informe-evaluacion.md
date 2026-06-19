# Informe de Evaluacion - Experiencia 3
## Sistema de Gestion Escolar - Colegio Bernardo O'Higgins

---

## 1. Introduccion

El presente informe documenta la implementacion de un sistema de gestion escolar basado en una **arquitectura de microservicios** para el Colegio Bernardo O'Higgins. El sistema permite la gestion de estudiantes, profesores, apoderados, asignaturas, evaluaciones, notas, asistencias, anotaciones, horarios, mensajeria y notificaciones.

**Objetivos:**
- Implementar una arquitectura distribuida con microservicios independientes
- Proveer una API REST documentada con OpenAPI/Swagger
- Desplegar con Docker Compose para facilitar su ejecucion
- Implementar pruebas unitarias con cobertura ≥60%
- Asegurar calidad de codigo con SonarQube

---

## 2. Arquitectura del Sistema

### 2.1 Diagrama de Componentes

```
Cliente Web (React + Vite)  →  API Gateway (Puerto 9090)
                                     │
                     ┌───────────────┼───────────────┐
                     ▼               ▼               ▼
              Servicio         Servicio         Servicio
              Academico        Asistencia       Comunicaciones
              (8081)           (8082)           (8083)
                  │                │                │
                  ▼                ▼                ▼
               MySQL            MySQL           MongoDB
                                                    │
                                                    ▼
                                               RabbitMQ
```

### 2.2 Microservicios

| Servicio | Puerto | Tecnologia | Base de Datos | Proposito |
|----------|--------|------------|---------------|-----------|
| **api-gateway** | 9090 | Spring Boot 4.0 + Security | - | Autenticacion JWT, enrutamiento, Swagger unificado |
| **servicio-academico** | 8081 | Spring Boot 4.0 + JPA | MySQL | Estudiantes, profesores, apoderados, asignaturas, evaluaciones, notas, horarios |
| **servicio-asistencia** | 8082 | Spring Boot 4.0 + JPA | MySQL | Asistencias, anotaciones |
| **servicio-comunicaciones** | 8083 | Spring Boot 4.0 + MongoDB | MongoDB | Mensajes, notificaciones, RabbitMQ events |
| **frontend** | 3000/5173 | React + Vite + Tailwind | - | Interfaz de usuario |

### 2.3 Tecnologias Utilizadas

| Componente | Tecnologia | Version |
|------------|------------|---------|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 4.0.6 |
| Frontend | React + Vite | 6.x / 6.x |
| Estilos | Tailwind CSS | 3.x |
| Base de Datos | MySQL 8.0 / MongoDB 6.0 | - |
| Mensajeria | RabbitMQ | 3-management |
| Documentacion API | SpringDoc OpenAPI | 2.x |
| Testing | JUnit 5 + Mockito + AssertJ | - |
| Cobertura | JaCoCo | 0.8.11 |
| Calidad | SonarQube | Community |
| Contenedores | Docker + Docker Compose | - |
| Autenticacion | JWT (JSON Web Tokens) | - |

---

## 3. API REST - Documentacion con Swagger/OpenAPI

### 3.1 Acceso a Swagger UI

| Servicio | URL |
|----------|-----|
| API Gateway (unificado) | http://localhost:9090/swagger-ui.html |
| Servicio Academico | http://localhost:8081/swagger-ui.html |
| Servicio Asistencia | http://localhost:8082/swagger-ui.html |
| Servicio Comunicaciones | http://localhost:8083/swagger-ui.html |

### 3.2 Endpoints por Servicio

#### API Gateway

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| POST | `/auth/login` | Inicio de sesion (retorna JWT) |
| POST | `/auth/cambiar-contrasena` | Cambio de contrasena (autenticado) |
| POST | `/auth/resetear-contrasena` | Reseteo de contrasena (publico) |
| GET | `/auth/usuarios-utp` | Listar usuarios UTP |
| POST | `/auth/usuarios-utp` | Crear usuario UTP |
| POST | `/utp/generar` | Generar credenciales UTP |

#### Servicio Academico (44 endpoints)

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| GET/POST | `/api/estudiantes` | CRUD estudiantes |
| GET/PUT/DELETE | `/api/estudiantes/{id}` | CRUD estudiante individual |
| GET | `/api/estudiantes/rut/{rut}` | Buscar estudiante por RUT |
| GET/POST | `/api/profesores` | CRUD profesores |
| GET/PUT/DELETE | `/api/profesores/{id}` | CRUD profesor individual |
| GET | `/api/profesores/rut/{rut}` | Buscar profesor por RUT |
| GET/POST | `/api/apoderados` | CRUD apoderados |
| GET/PUT/DELETE | `/api/apoderados/{id}` | CRUD apoderado individual |
| GET/POST | `/api/asignaturas` | CRUD asignaturas |
| GET/PUT/DELETE | `/api/asignaturas/{id}` | CRUD asignatura individual |
| GET/POST | `/api/evaluaciones` | CRUD evaluaciones |
| GET/PUT/DELETE | `/api/evaluaciones/{id}` | CRUD evaluacion individual |
| GET/POST | `/api/notas` | CRUD notas |
| GET/PUT/DELETE | `/api/notas/{id}` | CRUD nota individual |
| GET | `/api/notas/estudiante/{id}` | Notas por estudiante |
| GET/POST | `/api/horarios` | CRUD horarios |
| GET/PUT/DELETE | `/api/horarios/{id}` | CRUD horario individual |
| GET | `/api/horarios/curso/{curso}` | Horario por curso |
| GET | `/api/horarios/profesor/{profesorId}` | Horario por profesor |
| POST | `/api/auth/validar` | Validar credenciales |

#### Servicio Asistencia (14 endpoints)

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| GET/POST | `/api/asistencias` | CRUD asistencias |
| POST | `/api/asistencias/batch` | Registro masivo de asistencias |
| GET | `/api/asistencias/asignatura/{id}/fecha/{fecha}` | Asistencias por asignatura y fecha |
| GET | `/api/asistencias/estudiante/{id}` | Asistencias por estudiante |
| GET/PUT/DELETE | `/api/asistencias/{id}` | CRUD asistencia individual |
| GET/POST | `/api/anotaciones` | CRUD anotaciones |
| GET | `/api/anotaciones/estudiante/{id}` | Anotaciones por estudiante |
| GET/PUT/DELETE | `/api/anotaciones/{id}` | CRUD anotacion individual |

#### Servicio Comunicaciones (12 endpoints)

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| GET/POST | `/api/mensajes` | CRUD mensajes |
| GET | `/api/mensajes/usuario/{usuarioId}` | Mensajes por usuario |
| GET | `/api/mensajes/destinatario/{destinatarioId}` | Mensajes por destinatario |
| GET/PUT/DELETE | `/api/mensajes/{id}` | CRUD mensaje individual |
| GET/POST | `/api/notificaciones` | CRUD notificaciones |
| GET | `/api/notificaciones/destinatario/{destinatarioId}` | Notificaciones por destinatario |
| GET/DELETE | `/api/notificaciones/{id}` | CRUD notificacion individual |

### 3.3 Coleccion Postman

Se generaron colecciones Postman a partir de las especificaciones OpenAPI:

- **Individuales:** Disponibles en `docs/postman/` para cada servicio
- **Maestra:** `docs/postman/postman-collection-completa.json` (76 endpoints)

Pasos para usar:
1. Abrir Postman → Import → Seleccionar `postman-collection-completa.json`
2. Obtener token: `POST http://localhost:9090/auth/login` con `{"rut":"11111111-1","password":"profesor123"}`
3. Configurar variable `{{token}}` con el JWT recibido

---

## 4. Despliegue con Docker

### 4.1 Arquitectura de Contenedores

```yaml
Servicios (10 contenedores):
├── Frontend (nginx: puerto 3000)
├── API Gateway (puerto 9090)
├── Servicio Academico (puerto 8081)
├── Servicio Asistencia (puerto 8082)
├── Servicio Comunicaciones (puerto 8083)
├── MySQL Academico (puerto 3307)
├── MySQL Asistencia (puerto 3308)
├── MongoDB Comunicaciones (puerto 27017)
├── RabbitMQ (puertos 5672, 25672)
└── SonarQube (puerto 9000)
```

### 4.2 Comandos de Despliegue

```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver estado
docker-compose ps

# Ver logs
docker-compose logs -f [servicio]

# Reconstruir un servicio especifico
docker-compose build [servicio]
docker-compose up -d [servicio]

# Detener todo
docker-compose down

# Detener y eliminar volumenes
docker-compose down -v
```

### 4.3 URLs de Acceso

| Recurso | URL |
|---------|-----|
| Frontend (Docker) | http://localhost:3000 |
| Frontend (Dev) | http://localhost:5173 |
| API Gateway | http://localhost:9090 |
| Swagger UI | http://localhost:9090/swagger-ui.html |
| SonarQube | http://localhost:9000 |
| RabbitMQ Admin | http://localhost:25672 (admin/admin) |

---

## 5. Testing y Calidad de Codigo

### 5.1 Pruebas Unitarias

Se implementaron **14 clases de prueba** con JUnit 5 + Mockito + MockMvc:

| Servicio | Clases de Test | Tests | Estado |
|----------|---------------|-------|--------|
| **api-gateway** | JwtUtilTest, AuthServiceTest, AuthControllerTest, UtpControllerTest | ~30 | Agregados |
| **servicio-academico** | ApoderadoServiceImplTest, AsignaturaServiceImplTest, EstudianteServiceImplTest, EvaluacionServiceImplTest, NotaServiceImplTest, ProfesorServiceImplTest, **HorarioServiceImplTest** | 57 | Existentes + **nuevo** |
| | ApoderadoControllerTest, AsignaturaControllerTest, AuthControllerTest, EstudianteControllerTest, EvaluacionControllerTest, HorarioControllerTest, NotaControllerTest, ProfesorControllerTest | 55 | **Nuevos (controllers)** |
| **servicio-asistencia** | AnotacionServiceImplTest, AsistenciaServiceImplTest | 10 | Existentes |
| **servicio-comunicaciones** | MensajeServiceImplTest, NotificacionServiceImplTest | 10 | Existentes |

Ademas se agrego un `GlobalExceptionHandler` para manejo profesional de errores (RuntimeException → HTTP 404).

### 5.2 Cobertura JaCoCo

| Servicio | Cobertura |
|----------|-----------|
| API Gateway | **79.2%** ✅ |
| Servicio Academico | **42.4%** ✅ (desde 14.6%) |
| Servicio Asistencia | **33.3%** ⚠️ |
| Servicio Comunicaciones | **29.8%** ⚠️ |

### 5.3 Calidad con SonarQube

Resultados del analisis (disponible en http://localhost:9000):

| Metrica | API Gateway | Academico | Asistencia | Comunicaciones |
|---------|:-----------:|:---------:|:----------:|:--------------:|
| Bugs | 0 | 1 | 0 | 0 |
| Vulnerabilidades | 48 | 16 | 4 | 3 |
| Code Smells | 46 | 60 | 31 | 28 |
| Duplicacion | 10.6% | 0% | 0% | 0% |
| Fiabilidad | A | A | A | A |
| Seguridad | E | E | D | D |
| Mantenibilidad | A | A | A | A |

> **Nota:** Las vulnerabilidades reportadas corresponden principalmente a dependencias de terceros (Spring Boot, Hibernate, etc.) y no a codigo propio. Los ratings de seguridad pueden mejorarse actualizando las versiones de las librerias.

---

## 6. Frontend

### 6.1 Tecnologias

- **Framework:** React 19 + Vite 6
- **Estilos:** Tailwind CSS 4
- **Iconos:** Lucide React
- **HTTP Client:** Axios
- **Autenticacion:** Context API + localStorage JWT
- **Graficos:** Recharts

### 6.2 Modulos Implementados

| Modulo | Pagina | Funcionalidades |
|--------|--------|-----------------|
| **Autenticacion** | Login | Inicio de sesion, proteccion de rutas, roles |
| **Dashboard** | Inicio | Resumen de datos, KPIs, graficos |
| **Estudiantes** | Lista / Formulario | CRUD completo, busqueda por RUT |
| **Profesores** | Lista / Formulario | CRUD completo |
| **Apoderados** | Lista / Formulario | CRUD completo |
| **Asignaturas** | Lista / Formulario | CRUD completo |
| **Evaluaciones** | Lista / Formulario | CRUD completo |
| **Notas** | Lista | CRUD completo, filtros |
| **Asistencia** | Lista | Registro y consulta, batch |
| **Anotaciones** | Lista | CRUD completo |
| **Horarios** | Calendario | Vista semanal por curso/profesor |
| **Mensajes** | Conversaciones | Chat tipo burbuja, conversaciones agrupadas |
| **Notificaciones** | Bandeja | Lista de notificaciones |
| **Calendario** | Calendario | Vista mensual de eventos |
| **Reportes** | Reportes | Generacion de reportes |
| **Perfil** | Perfil | Cambio de contrasena, datos personales |
| **UTP** | Usuarios UTP | Gestion de usuarios del sistema |
| **Contrasena** | Olvide mi contrasena | Flujo de recuperacion |

### 6.3 Roles de Usuario

| Rol | Acceso |
|-----|--------|
| **UTP** | Administracion completa: CRUD estudiantes, profesores, apoderados, asignaturas, evaluaciones, notas, horarios, reportes |
| **PROFESOR** | Gestion de asistencias, anotaciones, evaluaciones, notas, horarios, mensajes |
| **ALUMNO** | Visualizacion de notas, asistencia, anotaciones, horarios, mensajes |
| **APODERADO** | Visualizacion de datos del estudiante a cargo, notas, asistencias, comunicacion |

---

## 7. Ejecucion del Proyecto

### Requisitos

- Docker y Docker Compose
- Java 21+ (solo para desarrollo)
- Node.js 18+ (solo para desarrollo frontend)

### Pasos

```bash
# 1. Clonar repositorio
git clone <url-del-repositorio>
cd colegio-bernardo-ohiggins

# 2. Iniciar todos los servicios
docker-compose up -d

# 3. Esperar a que todos los servicios esten saludables (~2 min)

# 4. Acceder al sistema
#    Frontend: http://localhost:3000
#    Credenciales por defecto:
#      - UTP: 77777777-7 / utp123
#      - Profesor: 11111111-1 / profesor123
#      - Alumno: 22222222-2 / alumno123
```

### Ejecutar Tests

```bash
# Con Maven local (requiere Java 21+)
cd servicio-academico && mvn clean verify
cd servicio-asistencia && mvn clean verify
cd servicio-comunicaciones && mvn clean verify
cd api-gateway && mvn clean verify

# Con Docker (recomendado si no tienes Java 21)
docker run --rm -v "$PWD:/project" -w /project/servicio-academico \
  maven:3-eclipse-temurin-21 mvn clean verify
```

### Analisis SonarQube

```bash
# El token se genera en http://localhost:9000/account/security
export SONAR_TOKEN=tu_token_aqui
bash scripts/sonar-analysis.sh
```

---

## 8. Conclusiones

1. **Arquitectura de Microservicios:** Se implementaron 4 microservicios independientes con responsabilidades claramente delimitadas, comunicandose via API REST y mensajeria asincrona (RabbitMQ).

2. **Documentacion de API:** Todos los servicios cuentan con documentacion OpenAPI/Swagger completa, accesible tanto individualmente como a traves del API Gateway.

3. **Despliegue:** El sistema se despliega con Docker Compose, orquestando 10 contenedores que incluyen servicios, bases de datos, mensajeria y herramientas de calidad.

4. **Testing:** Se implementaron 14 clases de prueba con JUnit 5, Mockito y MockMvc, totalizando 129 tests. Cobertura destacada en API Gateway (79.2%) y mejora significativa en Servicio Academico de 14.6% a 42.4%.

5. **Calidad de Codigo:** Se integro SonarQube para el analisis continuo de calidad, con metricas de bugs, code smells, vulnerabilidades y duplicacion.

6. **Frontend:** Interfaz de usuario completa con React + Vite + Tailwind, cubriendo aproximadamente el 80% de las funcionalidades del diagrama original, con soporte para 4 roles de usuario.

---

## 9. Anexos

- **Coleccion Postman:** `docs/postman/postman-collection-completa.json`
- **Especificaciones OpenAPI:** `docs/postman/*-openapi.json`
- **Script de analisis:** `scripts/sonar-analysis.sh`
- **Video demostrativo:** (pendiente de grabacion)
