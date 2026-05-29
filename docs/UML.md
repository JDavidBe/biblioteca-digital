# UML - Diagramas de Arquitectura y Secuencia
# Biblioteca Digital - Microservicios

---

## 1. Diagrama de Arquitectura de Componentes

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENTE (Frontend)                            │
│                     HTTP REST / JSON + JWT                           │
└───────────────┬──────────────────────────────────────────────────────┘
                │
                ▼
┌──────────────────────────────────────────────────────────────────────┐
│                    MICROSERVICIOS (Spring Boot 3 / Java 21)           │
│                                                                       │
│  ┌──────────────┐  ┌────────────────┐  ┌──────────────────────────┐  │
│  │  ms-auth     │  │  ms-usuarios   │  │      ms-recursos         │  │
│  │  :8080       │  │  :8081         │  │      :8082               │  │
│  │              │  │                │  │                          │  │
│  │  POST /login │  │  GET  /usuarios│  │  POST /recursos          │  │
│  │  POST /reg.  │  │  POST /usuarios│  │  GET  /recursos          │  │
│  │  GET  /valid.│  │  PUT  /{id}    │  │  GET  /{id}/descargar    │  │
│  └──────┬───────┘  └──────┬─────────┘  └────────────┬─────────────┘  │
│         │                 │                          │               │
│  ┌──────┴────────────────────────────────────────────┘               │
│  │                                                                   │
│  ▼                                                                   │
│  ┌──────────────┐  ┌────────────────┐  ┌──────────────────────────┐  │
│  │ms-valoraciones│  │  ms-descargas  │  │   ms-notificaciones      │  │
│  │  :8083        │  │  :8084         │  │   :8086                  │  │
│  │               │  │                │  │                          │  │
│  │  POST /cal.   │  │  POST /desc.   │  │  POST /bienvenida        │  │
│  │  GET  /recurso│  │  GET  /usuario │  │  POST /nuevo-recurso     │  │
│  │  GET  /resumen│  │  GET  /top     │  │  POST /descarga          │  │
│  └───────────────┘  └──────┬─────────┘  └────────────┬─────────────┘  │
│                             │                         │               │
│  ┌──────────────────────────┘                         │               │
│  ▼                                                    ▼               │
│  ┌──────────────┐                              ┌──────────────────┐   │
│  │  ms-reportes │                              │  SMTP / Email    │   │
│  │  :8085       │                              │  (JavaMail)      │   │
│  │              │                              └──────────────────┘   │
│  │  POST /activ.│                                                     │
│  │  GET  /resumen│                                                    │
│  └──────────────┘                                                     │
└──────────────────────────────────────────────────────────────────────┘
                │
                ▼
┌──────────────────────────────────────────────────────────────────────┐
│                BASES DE DATOS (PostgreSQL 16 — una por MS)            │
│                                                                       │
│  [biblioteca_auth]  [biblioteca_usuarios]  [biblioteca_recursos]      │
│  [biblioteca_valoraciones]  [biblioteca_descargas]                    │
│  [biblioteca_reportes]  [biblioteca_notificaciones]                   │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 2. Arquitectura Limpia (Clean Architecture) - Por microservicio

```
┌─────────────────────────────────────────────────────────────┐
│                MICROSERVICIO (Clean Architecture)            │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              INFRAESTRUCTURA                         │   │
│  │                                                     │   │
│  │  entry_points/          driver_adapters/            │   │
│  │  ┌──────────────┐      ┌──────────────────────────┐ │   │
│  │  │  Controller  │      │  JpaRepository (impl)    │ │   │
│  │  │  Exception   │      │  GatewayImpl             │ │   │
│  │  │  Handler     │      │  Mapper (Data↔Domain)    │ │   │
│  │  └──────┬───────┘      └───────────────┬──────────┘ │   │
│  └─────────┼─────────────────────────────┼─────────────┘   │
│            │                             │                  │
│  ┌─────────▼─────────────────────────────▼─────────────┐   │
│  │              APLICACIÓN                              │   │
│  │  config/                dto/                        │   │
│  │  ┌──────────────┐      ┌──────────────────────────┐ │   │
│  │  │ UseCaseConfig│      │  RequestDTO              │ │   │
│  │  │ SecurityConfig│     │  ResponseDTO             │ │   │
│  │  │ SwaggerConfig│      │  ApiResponseDTO          │ │   │
│  │  └──────────────┘      └──────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              DOMINIO (núcleo)                        │   │
│  │                                                     │   │
│  │  domain/model/          domain/usecase/             │   │
│  │  ┌──────────────┐      ┌──────────────────────────┐ │   │
│  │  │  Entidad     │      │  UseCase (lógica de      │ │   │
│  │  │  (POJO)      │      │  negocio pura)           │ │   │
│  │  │  Gateway     │      │  Sin dependencia de      │ │   │
│  │  │  (interfaz)  │      │  frameworks              │ │   │
│  │  └──────────────┘      └──────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Diagrama de Secuencia — Registro y Login

```
Cliente     ms-auth          ms-usuarios     ms-notificaciones
  │              │                │                  │
  │─── POST /api/auth/registro ──►│                  │
  │              │                │                  │
  │         valida datos          │                  │
  │         encripta pass         │                  │
  │         guarda credencial     │                  │
  │              │                │                  │
  │◄── 201 { token: "..." } ──────│                  │
  │              │                │                  │
  │─── POST /api/usuarios ────────────────────────►  │
  │              │                │ guarda usuario    │
  │◄─────────────────────────── 201 { usuario } ─────│
  │                                                   │
  │─── POST /api/notificaciones/bienvenida ──────────►│
  │                                                   │ envía email
  │◄─────────────────────────── 201 { notif } ────────│
  │
  │─── POST /api/auth/login ────►│
  │              │                │
  │         busca credencial      │
  │         verifica password     │
  │         genera JWT            │
  │              │                │
  │◄── 200 { token: "eyJ..." } ───│
```

---

## 4. Diagrama de Secuencia — Publicación y Descarga de Recurso

```
Docente     ms-recursos      ms-descargas    ms-notificaciones
  │              │                │                  │
  │─ POST /api/recursos (multipart) ──────────────►  │
  │              │                                   │
  │         valida datos                             │
  │         guarda archivo                           │
  │         guarda en BD                             │
  │              │                                   │
  │◄── 201 { recurso } ──────────────────────────────│
  │                                                  │
  │─ POST /api/notificaciones/nuevo-recurso ────────►│
  │                                                  │ notifica usuarios
  │◄─────────────────────────── 201 ─────────────────│

Estudiante  ms-recursos      ms-descargas    ms-notificaciones
  │              │                │                  │
  │─── GET /api/recursos/{id}/descargar ─────────►   │
  │              │                                   │
  │         lee archivo                              │
  │◄── 200 bytes (archivo) ──────────────────────────│
  │                                                  │
  │─── POST /api/descargas ────────────────────────► │
  │              │                │ registra descarga │
  │◄─────────────────────── 201 { descarga } ─────── │
  │                                                  │
  │─── POST /api/notificaciones/descarga ───────────►│
  │                                                  │ confirma por email
  │◄─────────────────────────── 201 ─────────────────│
```

---

## 5. Diagrama de Secuencia — Validación JWT

```
Cliente     ms-usuarios (u otro ms)    ms-auth
  │                  │                    │
  │── GET /api/usuarios/{id} ────────────►│
  │        Authorization: Bearer <token>   │
  │                  │                    │
  │        extrae token del header        │
  │                  │                    │
  │───────── GET /api/auth/validar ───────►│
  │                  │  Authorization: Bearer <token>
  │                  │                    │
  │                  │         valida firma JWT
  │                  │         extrae correo/rol
  │                  │                    │
  │◄──────── 200 { valido: true, correo } ─│
  │                  │                    │
  │         ejecuta lógica de negocio     │
  │◄── 200 { datos } ────────────────────►│
```
