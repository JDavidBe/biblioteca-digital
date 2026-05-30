Biblioteca Digital — Microservicios

Sistema de gestión de recursos educativos construido con arquitectura de microservicios.

## Tecnologías

- **Java 21** + **Spring Boot 3.4.5**
- **Gradle** (build por microservicio)
- **PostgreSQL 16** (base de datos independiente por microservicio)
- **JWT** (autenticación stateless)
- **JaCoCo** (cobertura de pruebas ≥ 90%)
- **Docker** + **Docker Compose**
- **Swagger/OpenAPI** (documentación automática)

---

## Microservicios

| Servicio            | Puerto | Descripción                                    |
|---------------------|--------|------------------------------------------------|
| ms-auth             | 8080   | Autenticación y generación de JWT              |
| ms-usuarios         | 8081   | Gestión de usuarios                            |
| ms-recursos         | 8082   | Publicación y descarga de recursos             |
| ms-valoraciones     | 8083   | Calificaciones y reseñas                       |
| ms-descargas        | 8084   | Historial y estadísticas de descargas          |
| ms-reportes         | 8085   | Registro de actividad y reportes               |
| ms-notificaciones   | 8086   | Envío de notificaciones y alertas por WhatsApp |

---

## Levantar con Docker Compose

```bash
# Clonar
git clone https://github.com//biblioteca-digital-microservicios.git
cd biblioteca-digital-microservicios/biblioteca-digital

# Configurar variables de entorno
cp .env.example .env
# Editar MAIL_USERNAME, MAIL_PASSWORD y TWILIO_* en .env

# Construir y levantar
docker compose up --build -d

# Ver estado
docker compose ps

# Ver logs de un servicio
docker compose logs -f ms-auth
```

---

## Variables de entorno

Cada microservicio lee estas variables (con valores por defecto para desarrollo local):

| Variable                     | Descripción                         | Default                                             |
|------------------------------|-------------------------------------|-----------------------------------------------------|
| `SPRING_DATASOURCE_URL`      | URL JDBC de la base de datos        | `jdbc:postgresql://localhost:5432/<db>`             |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la BD                    | `postgres`                                          |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la BD                 | `2305`                                              |
| `JWT_SECRET`                 | Clave de firma JWT (≥32 chars)      | `BibliotecaDigital2025SecretKeyMustBe32CharsLong!!` |
| `JWT_EXPIRATION_MS`          | Expiración del token en ms          | `28800000` (8 horas)                                |
| `MAIL_USERNAME`              | Correo para envío (solo notif.)     | —                                                   |
| `MAIL_PASSWORD`              | App password de Gmail (solo notif.) | —                                                   |
| `TWILIO_ACCOUNT_SID`         | SID de cuenta Twilio                | —                                                   |
| `TWILIO_AUTH_TOKEN`          | Token de autenticación Twilio       | —                                                   |

---

## Ejecutar pruebas

```bash
# Un microservicio
cd ms-auth
./gradlew test jacocoTestReport

# Ver reporte de cobertura
open build/reports/jacoco/test/html/index.html

# Verificar que supera el 90%
./gradlew check
```

**Total de pruebas:** 484 tests distribuidos en 7 microservicios.

| Microservicio     | Tests |
|-------------------|-------|
| ms-auth           | 82    |
| ms-usuarios       | 71    |
| ms-recursos       | 55    |
| ms-valoraciones   | 58    |
| ms-descargas      | 78    |
| ms-reportes       | 61    |
| ms-notificaciones | 79    |

---

## Swagger UI

Una vez levantados los servicios:

- ms-auth → http://localhost:8080/swagger-ui.html
- ms-usuarios → http://localhost:8081/swagger-ui.html
- ms-recursos → http://localhost:8082/swagger-ui.html
- ms-valoraciones → http://localhost:8083/swagger-ui.html
- ms-descargas → http://localhost:8084/swagger-ui.html
- ms-reportes → http://localhost:8085/swagger-ui.html
- ms-notificaciones → http://localhost:8086/swagger-ui.html

### Autenticación en Swagger

1. Llamar `POST /api/auth/registro` para crear cuenta
2. Llamar `POST /api/auth/login` para obtener el token JWT
3. En cualquier otro microservicio: clic en **Authorize** → pegar el token

---

## Arquitectura

Cada microservicio sigue **Clean Architecture**:
infraestructure/
entry_points/     → Controllers + GlobalExceptionHandler
driver_adapters/  → JPA entities, repositories, mappers
security/         → JwtAuthFilter
application/
config/           → SecurityConfig, SwaggerConfig, UseCaseConfig
dto/              → Request/Response DTOs
domain/
model/            → Entidades de dominio + interfaces Gateway
usecase/          → Lógica de negocio pura (sin frameworks)

---

## Despliegue en Railway (free, sin tarjeta)

Ver guía completa en [`docs/DEPLOY.md`](docs/DEPLOY.md).

---

## Documentación

- [`docs/MER.md`](docs/MER.md) — Modelo Entidad-Relación
- [`docs/UML.md`](docs/UML.md) — Diagramas de arquitectura y secuencia
- [`docs/DEPLOY.md`](docs/DEPLOY.md) — Guía de despliegue
