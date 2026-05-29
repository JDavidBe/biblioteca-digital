# MER - Modelo Entidad-Relación
# Biblioteca Digital - Microservicios

Cada microservicio tiene su propia base de datos (arquitectura de datos descentralizada).
Las relaciones entre entidades de distintos microservicios son lógicas (por ID), no por FK físicas.

---

## MS-AUTH — biblioteca_auth

```
credenciales
─────────────────────────────────────────────
id              BIGSERIAL    PK
correo          VARCHAR(255) NOT NULL UNIQUE
password_hash   VARCHAR(255) NOT NULL
rol             VARCHAR(50)  NOT NULL  DEFAULT 'ESTUDIANTE'
activo          BOOLEAN      NOT NULL  DEFAULT true
```

---

## MS-USUARIOS — biblioteca_usuarios

```
usuarios
─────────────────────────────────────────────
id              BIGSERIAL    PK
nombre          VARCHAR(255) NOT NULL
correo          VARCHAR(255) NOT NULL UNIQUE
institucion     VARCHAR(255)
grado           VARCHAR(50)
rol             VARCHAR(50)  NOT NULL  DEFAULT 'ESTUDIANTE'
activo          BOOLEAN      NOT NULL  DEFAULT true
creado_en       TIMESTAMP    NOT NULL
```

**Relación lógica:** `usuarios.correo` ↔ `credenciales.correo` (mismo correo = misma cuenta)

---

## MS-RECURSOS — biblioteca_recursos

```
recursos
─────────────────────────────────────────────
id                BIGSERIAL    PK
titulo            VARCHAR(255) NOT NULL
descripcion       TEXT
area              VARCHAR(100) NOT NULL
grado             VARCHAR(50)
tipo              VARCHAR(50)  NOT NULL  DEFAULT 'PDF'
nombre_archivo    VARCHAR(255)
ruta_archivo      VARCHAR(500)
tamano_bytes      BIGINT
subido_por_id     BIGINT       (FK lógica → usuarios.id)
subido_por_correo VARCHAR(255)
creado_en         TIMESTAMP    NOT NULL
disponible        BOOLEAN      NOT NULL  DEFAULT true
```

---

## MS-VALORACIONES — biblioteca_valoraciones

```
valoraciones
─────────────────────────────────────────────
id              BIGSERIAL    PK
recurso_id      BIGINT       NOT NULL  (FK lógica → recursos.id)
usuario_id      BIGINT                 (FK lógica → usuarios.id)
usuario_correo  VARCHAR(255)
puntuacion      INTEGER      NOT NULL  CHECK(puntuacion BETWEEN 1 AND 5)
comentario      TEXT
creado_en       TIMESTAMP    NOT NULL

UNIQUE(recurso_id, usuario_id)
```

---

## MS-DESCARGAS — biblioteca_descargas

```
descargas
─────────────────────────────────────────────
id                BIGSERIAL    PK
recurso_id        BIGINT       NOT NULL  (FK lógica → recursos.id)
titulo_recurso    VARCHAR(255)
usuario_id        BIGINT                 (FK lógica → usuarios.id)
usuario_correo    VARCHAR(255)
descargado_en     TIMESTAMP    NOT NULL
```

---

## MS-REPORTES — biblioteca_reportes

```
registros_actividad
─────────────────────────────────────────────
id              BIGSERIAL    PK
tipo_evento     VARCHAR(100) NOT NULL
entidad_id      BIGINT
entidad_tipo    VARCHAR(100)
usuario_id      BIGINT
usuario_correo  VARCHAR(255)
detalle         TEXT
ocurrido_en     TIMESTAMP    NOT NULL

INDEX(tipo_evento)
INDEX(usuario_id)
INDEX(ocurrido_en)
```

---

## MS-NOTIFICACIONES — biblioteca_notificaciones

```
notificaciones
─────────────────────────────────────────────
id                    BIGSERIAL    PK
destinatario_correo   VARCHAR(255) NOT NULL
destinatario_id       BIGINT                 (FK lógica → usuarios.id)
tipo                  VARCHAR(50)  NOT NULL  DEFAULT 'GENERAL'
asunto                VARCHAR(255) NOT NULL
mensaje               TEXT         NOT NULL
enviada               BOOLEAN      NOT NULL  DEFAULT false
creada_en             TIMESTAMP    NOT NULL
enviada_en            TIMESTAMP

INDEX(destinatario_id)
INDEX(enviada)
```

---

## Diagrama de relaciones lógicas entre microservicios

```
                ┌─────────────┐
                │  MS-AUTH    │
                │ credenciales│
                └──────┬──────┘
                       │ correo
                       ▼
                ┌─────────────┐
                │ MS-USUARIOS │
                │  usuarios   │
                └──┬──────────┘
              id   │   id
         ┌─────────┼──────────────────────┐
         ▼         ▼                      ▼
  ┌────────────┐  ┌──────────────┐  ┌───────────────────┐
  │MS-RECURSOS │  │MS-DESCARGAS  │  │MS-NOTIFICACIONES  │
  │  recursos  │  │  descargas   │  │  notificaciones   │
  └─────┬──────┘  └──────────────┘  └───────────────────┘
   id   │
   ┌────┴────────────┐
   ▼                 ▼
┌──────────────┐  ┌──────────────┐
│MS-VALORACIONES│  │MS-REPORTES  │
│ valoraciones  │  │  actividad  │
└───────────────┘  └─────────────┘
```
