# Diagramas UML

## Arquitectura general

```
┌─────────────────────────────────────────────────────────────┐
│                        FRONTEND                             │
│                    React + Vite (SPA)                       │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP + JWT
          ┌────────────────┼──────────────────────┐
          │                │                      │
    ┌─────▼────┐   ┌───────▼──────┐      ┌───────▼──────┐
    │ ms-auth  │   │ ms-usuarios  │      │ ms-recursos  │
    │  :8080   │   │    :8081     │      │   :8082      │
    └─────┬────┘   └───────┬──────┘      └──────┬───────┘
          │                │                    │
    ┌─────▼────┐   ┌───────▼──────┐      ┌──────▼───────┐
    │  DB auth │   │  DB usuarios │      │ DB recursos  │
    └──────────┘   └──────────────┘      └──────────────┘

    ┌───────────────┐  ┌────────────┐  ┌─────────────┐  ┌──────────────────┐
    │ms-valoraciones│  │ms-descargas│  │ ms-reportes │  │ms-notificaciones │
    │    :8083      │  │   :8084    │  │   :8085     │  │     :8086        │
    └───────┬───────┘  └─────┬──────┘  └──────┬──────┘  └────────┬─────────┘
            │                │                │                   │
    ┌───────▼───────┐  ┌─────▼──────┐  ┌──────▼──────┐  ┌────────▼─────────┐
    │DB valoraciones│  │DB descargas│  │ DB reportes │  │DB notificaciones │
    └───────────────┘  └────────────┘  └─────────────┘  └──────────────────┘
```

---

## Estructura interna de cada microservicio (Clean Architecture)

```
┌─────────────────────────────────────────────┐
│              INFRAESTRUCTURE                │
│  ┌──────────────┐    ┌─────────────────┐   │
│  │ entry_points │    │ driver_adapters │   │
│  │ Controller   │    │ JpaRepository   │   │
│  │ ExceptionH.  │    │ GatewayImpl     │   │
│  └──────┬───────┘    └────────┬────────┘   │
│         │                     │            │
│  ┌──────▼─────────────────────▼────────┐   │
│  │           APPLICATION               │   │
│  │  SecurityConfig  UseCaseConfig      │   │
│  │  SwaggerConfig   DTOs               │   │
│  └──────────────────┬──────────────────┘   │
│                     │                      │
│  ┌──────────────────▼──────────────────┐   │
│  │              DOMAIN                 │   │
│  │   model/  usecase/  gateway/        │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

---

## Flujo de autenticación

```
Usuario         Frontend        ms-auth          ms-usuarios
  │                │               │                  │
  │──POST /registro──►             │                  │
  │                │──POST /api/auth/registro─────►   │
  │                │               │──guarda credencial│
  │                │               │◄─200 OK           │
  │                │──POST /api/usuarios──────────────►│
  │                │                         guarda perfil
  │                │◄──────────────────────────200 OK──│
  │                │               │                  │
  │──POST /login───►               │                  │
  │                │──POST /api/auth/login──►          │
  │                │               │──valida password  │
  │                │◄──────JWT token────────           │
  │◄───JWT token───│               │                  │
```

---

## Flujo de descarga de recurso

```
Usuario    Frontend    ms-recursos  ms-descargas  ms-reportes  ms-notificaciones
  │           │             │            │              │              │
  │─descargar─►             │            │              │              │
  │           │─GET /{id}/descargar─►    │              │              │
  │           │◄────archivo─────         │              │              │
  │◄─archivo──│             │            │              │              │
  │           │─POST /api/descargas─────►│              │              │
  │           │             │            │─guarda reg.  │              │
  │           │─POST /api/reportes/actividad────────────►              │
  │           │             │            │              │─guarda evento │
  │           │─POST /api/notificaciones/descarga───────────────────►  │
  │           │             │            │              │   envía WA   │
```

---

## Flujo de publicación de recurso

```
Usuario    Frontend     ms-recursos   ms-reportes   ms-notificaciones
  │           │              │              │               │
  │─publicar──►              │              │               │
  │           │─POST /api/recursos (multipart)►             │
  │           │              │─guarda en BD │               │
  │           │◄──recurso creado──           │               │
  │           │─POST /api/reportes/actividad►│               │
  │           │─POST /api/notificaciones/nuevo-recurso──────►│
  │           │              │              │    envía WA   │
```
