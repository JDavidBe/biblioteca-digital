# Modelo Entidad-Relación

Cada microservicio tiene su propia base de datos PostgreSQL independiente. No existen foreign keys entre bases de datos — las relaciones lógicas se mantienen por `usuarioId` y `recursoId` como referencias numéricas.

---

## ms-auth — Base de datos: `biblioteca_auth`

### credenciales
| Campo         | Tipo    | Restricciones          |
|---------------|---------|------------------------|
| id            | BIGINT  | PK, auto-increment     |
| correo        | VARCHAR | NOT NULL, UNIQUE       |
| password_hash | VARCHAR | NOT NULL               |
| rol           | VARCHAR | NOT NULL               |
| activo        | BOOLEAN | NOT NULL, default true |

---

## ms-usuarios — Base de datos: `biblioteca_usuarios`

### usuarios
| Campo       | Tipo      | Restricciones          |
|-------------|-----------|------------------------|
| id          | BIGINT    | PK, auto-increment     |
| nombre      | VARCHAR   | NOT NULL               |
| correo      | VARCHAR   | NOT NULL, UNIQUE       |
| institucion | VARCHAR   |                        |
| grado       | VARCHAR   |                        |
| rol         | VARCHAR   | NOT NULL               |
| activo      | BOOLEAN   | NOT NULL, default true |
| creado_en   | TIMESTAMP |                        |

---

## ms-recursos — Base de datos: `biblioteca_recursos`

### recursos
| Campo             | Tipo      | Restricciones           |
|-------------------|-----------|-------------------------|
| id                | BIGINT    | PK, auto-increment      |
| titulo            | VARCHAR   | NOT NULL                |
| descripcion       | TEXT      |                         |
| area              | VARCHAR   | NOT NULL                |
| grado             | VARCHAR   |                         |
| tipo              | VARCHAR   | NOT NULL, default PDF   |
| nombre_archivo    | VARCHAR   |                         |
| ruta_archivo      | VARCHAR   |                         |
| tamano_bytes      | BIGINT    |                         |
| subido_por_id     | BIGINT    |                         |
| subido_por_correo | VARCHAR   |                         |
| creado_en         | TIMESTAMP |                         |
| disponible        | BOOLEAN   | NOT NULL, default true  |
| contenido         | BYTEA     |                         |

---

## ms-valoraciones — Base de datos: `biblioteca_valoraciones`

### valoraciones
| Campo          | Tipo      | Restricciones       |
|----------------|-----------|---------------------|
| id             | BIGINT    | PK, auto-increment  |
| recurso_id     | BIGINT    | NOT NULL            |
| usuario_id     | BIGINT    |                     |
| usuario_correo | VARCHAR   |                     |
| puntuacion     | INTEGER   | NOT NULL            |
| comentario     | TEXT      |                     |
| creado_en      | TIMESTAMP |                     |

> UNIQUE constraint sobre `(recurso_id, usuario_id)` — un usuario solo puede valorar un recurso una vez.

---

## ms-descargas — Base de datos: `biblioteca_descargas`

### descargas
| Campo          | Tipo      | Restricciones       |
|----------------|-----------|---------------------|
| id             | BIGINT    | PK, auto-increment  |
| recurso_id     | BIGINT    | NOT NULL            |
| titulo_recurso | VARCHAR   |                     |
| usuario_id     | BIGINT    |                     |
| usuario_correo | VARCHAR   |                     |
| descargado_en  | TIMESTAMP |                     |
| ip_origen      | VARCHAR   |                     |

---

## ms-reportes — Base de datos: `biblioteca_reportes`

### actividades
| Campo          | Tipo      | Restricciones       |
|----------------|-----------|---------------------|
| id             | BIGINT    | PK, auto-increment  |
| tipo_evento    | VARCHAR   | NOT NULL            |
| entidad_id     | BIGINT    |                     |
| entidad_tipo   | VARCHAR   |                     |
| usuario_id     | BIGINT    |                     |
| usuario_correo | VARCHAR   |                     |
| detalle        | TEXT      |                     |
| ocurrido_en    | TIMESTAMP |                     |

---

## ms-notificaciones — Base de datos: `biblioteca_notificaciones`

### notificaciones
| Campo                 | Tipo      | Restricciones               |
|-----------------------|-----------|-----------------------------|
| id                    | BIGINT    | PK, auto-increment          |
| destinatario_correo   | VARCHAR   |                             |
| destinatario_telefono | VARCHAR   | max 20 chars                |
| destinatario_id       | BIGINT    |                             |
| canal                 | VARCHAR   | NOT NULL, default EMAIL     |
| tipo                  | VARCHAR   | NOT NULL                    |
| asunto                | VARCHAR   |                             |
| mensaje               | TEXT      | NOT NULL                    |
| enviada               | BOOLEAN   | NOT NULL, default false     |
| enviada_sms           | BOOLEAN   | NOT NULL, default false     |
| creada_en             | TIMESTAMP |                             |
| enviada_en            | TIMESTAMP |                             |

---

## Relaciones lógicas entre microservicios

```
credenciales.correo  ────  usuarios.correo
usuarios.id          ────  recursos.subido_por_id
recursos.id          ────  valoraciones.recurso_id
recursos.id          ────  descargas.recurso_id
usuarios.id          ────  valoraciones.usuario_id
usuarios.id          ────  descargas.usuario_id
usuarios.id          ────  actividades.usuario_id
usuarios.id          ────  notificaciones.destinatario_id
```
