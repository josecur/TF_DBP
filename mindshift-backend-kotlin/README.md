# MindShift Backend — Kotlin + Spring Boot

Migración del backend original (Django REST Framework) a **Kotlin + Spring Boot 4**.
Mantiene **exactamente las mismas rutas y el mismo JSON** que el backend Django, por lo que
el frontend Angular funciona **sin cambiar una sola línea** (sigue apuntando a `localhost:8000`).

## Stack

| Pieza | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.x |
| Framework | Spring Boot 4 (Web MVC) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | H2 en archivo (cero instalación) |
| Contraseñas | BCrypt (antes estaban en texto plano) |
| Build | Gradle (wrapper incluido) |

## Requisitos

- **JDK 24** (el que ya tienes) o JDK 21 LTS.
  Si usas el 21, cambia `JavaLanguageVersion.of(24)` por `21` en `build.gradle.kts`.

## Cómo ejecutar

### Opción A — Terminal (con el wrapper de Gradle)
```bash
# Windows PowerShell
.\gradlew.bat bootRun
# Git Bash / Linux / Mac
./gradlew bootRun
```

### Opción B — IntelliJ IDEA
1. **File → Open** y selecciona esta carpeta (`mindshift-backend-kotlin`).
2. Espera a que sincronice Gradle.
3. Ejecuta `MindshiftBackendKotlinApplication.kt` (botón ▶).

La API queda en **http://localhost:8000/api/**

### Consola de la base de datos
- URL: http://localhost:8000/h2-console
- JDBC URL: `jdbc:h2:file:./data/mindshift`
- Usuario: `sa` · Contraseña: *(vacía)*

## Equivalencia de endpoints (Django → Kotlin)

| Recurso | Endpoints |
|---|---|
| Escenarios | `GET/POST /api/escenarios/` · `GET/PUT/PATCH/DELETE /api/escenarios/{id}/` |
| Opciones | `GET/POST /api/opciones/` · `.../{id}/` |
| Usuarios | CRUD `/api/usuarios/` + `POST /api/usuarios/registro/` + `POST /api/usuarios/login/` |
| Profesionales | CRUD `/api/profesionales/` + `GET /api/profesionales/buscar?especialidad=` + `POST /api/profesionales/login/` |
| Reservas | CRUD `/api/reservas/` + `GET /api/reservas/?idUsuario=` + `/por-usuario` + `/por-profesional` |

## Detalles de la migración

- **Barra final**: Django la acepta sola; Spring no. Un filtro (`TrailingSlashFilter`) reenvía
  `/api/x/` → `/api/x`, para que el frontend funcione igual.
- **BCrypt**: las contraseñas ahora se hashean. Los usuarios/profesionales creados con el
  backend Django (texto plano) NO podrán loguear; hay que recrearlos (la BD H2 arranca vacía).
- **`clave` nunca se devuelve** en las respuestas (era `write_only` en DRF).
- **Reserva**: los campos calculados (`alumno_correo`, `nivel_riesgo`, `medico_contacto`) solo
  se revelan cuando `estado == "Aceptado"`, igual que en Django.
- **`/api/publicaciones/`**: el frontend lo llama, pero ese modelo **no existía** en el Django
  actual (daba 404). No se migró. Si lo necesitas, se agrega como nuevo recurso.

## Carga inicial de datos

Al arrancar por primera vez (BD vacía), `DataLoader` lee los CSV de
`src/main/resources/data/` y siembra **60 escenarios + 240 opciones** del cuestionario
(equivale a los scripts `importar_datos.py` / `importar_opciones.py` de Django).
Si la BD ya tiene datos, la carga se omite para no duplicar.
