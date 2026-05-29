# Guía de Despliegue — Biblioteca Digital

## Opción 1: Local con Docker Compose ✅ (recomendado para desarrollo)

### Prerequisitos
- Docker Desktop instalado
- Puertos 8080–8086 y 5432–5438 disponibles

```bash
# 1. Clonar
git clone https://github.com/<usuario>/biblioteca-digital-microservicios.git
cd biblioteca-digital-microservicios/biblioteca-digital

# 2. Configurar email (opcional, solo para notificaciones)
cp .env.example .env
# editar .env con tu correo Gmail y app password

# 3. Levantar
docker compose up --build -d

# 4. Verificar
docker compose ps

# Swagger de cada ms disponible en:
# http://localhost:8080/swagger-ui.html  (ms-auth)
# http://localhost:8081/swagger-ui.html  (ms-usuarios)
# ... hasta 8086
```

---

## Opción 2: Railway.app ✅ (free tier sin tarjeta de crédito)

Railway permite desplegar contenedores Docker gratis sin tarjeta.

### Pasos

**1. Subir código a GitHub (mínimo 4 commits)**

```bash
cd biblioteca-digital-microservicios/biblioteca-digital
git init
git add .
git commit -m "feat: arquitectura base de microservicios con clean architecture"

git add ms-*/src/test ms-notificaciones/
git commit -m "feat: ms-notificaciones y pruebas unitarias 90% JaCoCo"

git add ms-*/Dockerfile docker-compose.yml
git commit -m "feat: dockerizacion multistage de los 7 microservicios"

git add docs/ README.md .github/ render.yaml
git commit -m "docs: MER, UML, README y pipeline CI/CD con GitHub Actions"

git remote add origin https://github.com/<tu-usuario>/biblioteca-digital.git
git push -u origin main
```

**2. Crear proyecto en Railway**
- Ir a [railway.app](https://railway.app) → Login con GitHub
- New Project → Deploy from GitHub repo → seleccionar el repositorio

**3. Agregar servicios**

Por cada microservicio:
- Add Service → GitHub Repo → seleccionar la carpeta raíz del ms (ej: `ms-auth`)
- Railway detecta el Dockerfile automáticamente
- En Settings → Variables, agregar:
  - `SPRING_DATASOURCE_URL` → Railway provee PostgreSQL, copiar la URL interna
  - `JWT_SECRET` → cadena segura de 32+ caracteres
  - `SPRING_DATASOURCE_USERNAME` → `postgres`
  - `SPRING_DATASOURCE_PASSWORD` → la generada por Railway

**4. Agregar PostgreSQL por microservicio**
- Add Service → Database → PostgreSQL
- Conectar la variable `SPRING_DATASOURCE_URL` desde la base al microservicio

---

## Opción 3: Render.com ⚠️ (requiere tarjeta de crédito para Web Services)

Render free tier para Web Services requiere tarjeta aunque no se cobre.
Si tienes tarjeta, usa el `render.yaml` incluido:

- Ir a [render.com](https://render.com) → New → Blueprint
- Seleccionar el repositorio — Render detecta `render.yaml` automáticamente
- Configurar variables secretas: `JWT_SECRET`, `MAIL_USERNAME`, `MAIL_PASSWORD`

---

## Ejecutar pruebas localmente

```bash
# Todos los tests de un microservicio
cd ms-auth
./gradlew test jacocoTestReport --no-daemon

# Reporte HTML
open build/reports/jacoco/test/html/index.html

# Verificar umbral 90% (falla el build si no lo alcanza)
./gradlew check --no-daemon
```
