# Guía de Despliegue

## Opción 1 — Docker Compose (local)

### Requisitos
- Docker Desktop instalado
- 4 GB RAM disponibles

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/<usuario>/biblioteca-digital.git
cd biblioteca-digital

# 2. Crear archivo de variables
cp .env.example .env

# 3. Editar .env con tus credenciales
# MAIL_USERNAME=tucorreo@gmail.com
# MAIL_PASSWORD=tu-app-password-gmail
# TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxx
# TWILIO_AUTH_TOKEN=xxxxxxxxxxxxxxxx

# 4. Levantar todos los servicios
docker compose up --build -d

# 5. Verificar que todos están corriendo
docker compose ps
```

Los servicios quedan disponibles en:

| Servicio          | URL local                        |
|-------------------|----------------------------------|
| Frontend          | http://localhost:5173            |
| ms-auth           | http://localhost:8080            |
| ms-usuarios       | http://localhost:8081            |
| ms-recursos       | http://localhost:8082            |
| ms-valoraciones   | http://localhost:8083            |
| ms-descargas      | http://localhost:8084            |
| ms-reportes       | http://localhost:8085            |
| ms-notificaciones | http://localhost:8086            |

---

## Opción 2 — Railway (gratuito, sin tarjeta)

Railway permite desplegar cada microservicio como un servicio independiente con su propia base de datos PostgreSQL.

### Requisitos
- Cuenta en [railway.app](https://railway.app)
- Repositorio en GitHub

### Pasos por microservicio

1. En Railway, clic en **New Project → Deploy from GitHub repo**
2. Seleccionar el repositorio y la carpeta del microservicio (ej: `ms-auth`)
3. Railway detecta automáticamente el `Dockerfile`
4. Agregar una base de datos: **New → Database → PostgreSQL**
5. En el servicio, ir a **Variables** y agregar:

```
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
JWT_SECRET=BibliotecaDigital2025SecretKeyMustBe32CharsLong!!
JWT_EXPIRATION_MS=28800000
```

6. Para ms-notificaciones agregar también:

```
MAIL_USERNAME=tucorreo@gmail.com
MAIL_PASSWORD=tu-app-password-gmail
TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxx
TWILIO_AUTH_TOKEN=xxxxxxxxxxxxxxxx
```

7. Repetir para cada uno de los 7 microservicios

### Notas
- Railway asigna una URL pública a cada servicio (ej: `ms-auth-production.up.railway.app`)
- Actualizar las URLs en el frontend (`VITE_API_*`) con las URLs públicas de Railway
- El plan gratuito incluye 500 horas/mes por servicio

---

## Opción 3 — Render (gratuito)

1. Crear cuenta en [render.com](https://render.com)
2. **New → Web Service → Connect GitHub**
3. Seleccionar repo y carpeta del microservicio
4. Configurar:
   - **Runtime**: Docker
   - **Root Directory**: `ms-auth` (o el que corresponda)
5. Agregar las variables de entorno equivalentes
6. **New → PostgreSQL** para la base de datos

### Nota
- Los servicios gratuitos de Render se duermen tras 15 minutos de inactividad
- El primer request tras el sueño tarda ~30 segundos

---

## Notificaciones WhatsApp (Twilio Sandbox)

Para recibir notificaciones por WhatsApp en desarrollo, cada número debe conectarse al sandbox de Twilio enviando desde WhatsApp:

```
join husband-nothing
```

al número **+1 415 523 8886**

> La conexión al sandbox dura 72 horas sin actividad. Para producción se requiere una cuenta Twilio de pago con número dedicado.
