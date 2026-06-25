# MindShift — Resumen exhaustivo de la migración a Kotlin

Documento maestro del Trabajo Final: migración de la plataforma **MindShift** desde su stack
original (**Angular + Django**) a **Kotlin** (backend **Spring Boot** + app móvil **Android/Jetpack Compose**).

> Documentos relacionados:
> - [`COMO_EJECUTAR.md`](COMO_EJECUTAR.md) — guía paso a paso para levantar todo.
> - [`mindshift-mobile/DOCUMENTACION.md`](mindshift-mobile/DOCUMENTACION.md) — documentación técnica de la app móvil.
> - `prender-todo.bat` / `apagar-todo.bat` — scripts para encender/apagar el entorno.

---

## Índice

1. [Resumen ejecutivo](#1-resumen-ejecutivo)
2. [Contexto y objetivo](#2-contexto-y-objetivo)
3. [Stack tecnológico (origen → destino)](#3-stack-tecnológico-origen--destino)
4. [Arquitectura general](#4-arquitectura-general)
5. [Estructura del monorepo](#5-estructura-del-monorepo)
6. [Backend — Kotlin + Spring Boot](#6-backend--kotlin--spring-boot)
7. [App móvil — Kotlin + Jetpack Compose](#7-app-móvil--kotlin--jetpack-compose)
8. [Mapa de equivalencias](#8-mapa-de-equivalencias)
9. [Contrato de la API (endpoints)](#9-contrato-de-la-api-endpoints)
10. [Cronología de módulos (incrementos)](#10-cronología-de-módulos-incrementos)
11. [Datos de prueba y credenciales](#11-datos-de-prueba-y-credenciales)
12. [Cómo ejecutar](#12-cómo-ejecutar)
13. [Verificación y pruebas](#13-verificación-y-pruebas)
14. [Flujo de trabajo Git](#14-flujo-de-trabajo-git)
15. [Problemas encontrados y soluciones](#15-problemas-encontrados-y-soluciones)
16. [Estado final y trabajo futuro](#16-estado-final-y-trabajo-futuro)

---

## 1. Resumen ejecutivo

MindShift es una plataforma de bienestar psicológico: un **cuestionario** que detecta el nivel de
carga mental del usuario (BAJO / MODERADO / CRÍTICO) y lo conecta con **especialistas** para
agendar **citas**. Existían dos roles: **usuario** (alumno/paciente) y **especialista**.

Se migró **todo el sistema** a Kotlin:

- **Backend**: de Django REST Framework a **Spring Boot 4** (mismas rutas y mismo JSON).
- **Front**: de la web Angular a una **app Android nativa** con Jetpack Compose.

El resultado cubre el **flujo completo de ambos roles** y conserva el aspecto visual de la web
(paleta azul + blanco). Todo está versionado en Git, probado en un celular real y documentado.

---

## 2. Contexto y objetivo

| | Original | Objetivo (esta migración) |
|---|---|---|
| Frontend | Angular (web, `localhost:4200`) | Android nativo (Kotlin + Compose) |
| Backend | Django + Django REST Framework | Spring Boot 3/4 (Kotlin) |
| Base de datos | SQLite | H2 (archivo, cero instalación) |
| Contraseñas | Texto plano | **BCrypt** (mejora) |

**Motivación:** unificar el stack en Kotlin, llevar la experiencia a móvil nativo y mejorar la
seguridad de credenciales, manteniendo el backend **intercambiable** con el Django original
(mismas URLs y formato JSON, para no romper nada).

---

## 3. Stack tecnológico (origen → destino)

### Backend
- **Kotlin** 2.3.x · **Spring Boot 4.1** (Web MVC) · **Spring Data JPA** + Hibernate
- **H2** (archivo) · **Jackson 3** · **spring-security-crypto** (BCrypt)
- **Gradle** (wrapper) · **JDK 24**

### App móvil
- **Kotlin** 2.2.10 · **Jetpack Compose** (BOM 2026.02.01) · **Material 3**
- **AGP** 9.2.1 · **minSdk 24** (Android 7) · **targetSdk 36**
- **Retrofit 2.11** + **Gson** + **OkHttp** · **Coroutines** + **StateFlow**
- **DataStore** (sesión) · **Navigation Compose** · arquitectura **MVVM**

---

## 4. Arquitectura general

```
┌─────────────────────────────┐      HTTP / REST / JSON      ┌──────────────────────────────┐
│        App Android          │ ───────────────────────────▶ │     Backend Spring Boot       │
│  (Kotlin + Jetpack Compose) │                              │  Controllers → Services → JPA │
│  UI · ViewModel · Retrofit  │ ◀─────────────────────────── │                               │
└─────────────────────────────┘                              └───────────────┬───────────────┘
                                                                             │ JDBC
                                                                             ▼
                                                                   ┌──────────────────┐
                                                                   │  Base de datos H2 │
                                                                   │  (archivo local)  │
                                                                   └──────────────────┘
```

En desarrollo, el celular alcanza el backend por **USB** mediante `adb reverse tcp:8000 tcp:8000`
(por eso la app usa `http://localhost:8000/`). El backend escucha en el **puerto 8000** (igual que
el Django original, para que el frontend web tampoco necesite cambios).

---

## 5. Estructura del monorepo

Repositorio: `TF_DBP` (rama de trabajo **`migracion-kotlin`**). Vive en `C:\Dev\TF_DBP`.

```
TF_DBP/
├── mindshift-backend-kotlin/   # ★ Backend nuevo (Spring Boot)
├── mindshift-mobile/           # ★ App móvil nueva (Android/Compose)
├── mindshift-backend/          # Backend Django (legado, referencia)
├── mindshift-frontend/         # Frontend Angular (legado, referencia)
├── prender-todo.bat            # Enciende: túnel + backend + compila/instala app
├── apagar-todo.bat             # Apaga: backend + daemons Gradle + Android Studio
├── COMO_EJECUTAR.md            # Guía de ejecución
└── RESUMEN_MIGRACION.md        # Este documento
```

Los proyectos **legados** (Angular y Django) se conservan como referencia de la migración; los
nuevos (`*-kotlin` y `*-mobile`) son los que se desarrollaron.

---

## 6. Backend — Kotlin + Spring Boot

### 6.1 Estructura (`src/main/kotlin/com/mindshift/`)

```
├── MindshiftBackendKotlinApplication.kt   # main()
├── model/          # Entidades JPA: Escenario, Opciones, Usuario, Profesional, Reserva, Evaluacion
├── repository/     # Repositories.kt — 6 interfaces JpaRepository (query methods)
├── dto/            # DTOs request/response + Mappers.kt (entidad → DTO)
├── service/        # Lógica de negocio (1 por recurso)
├── controller/     # @RestController (1 por recurso) — mismas rutas que DRF
└── config/
    ├── AppConfig.kt           # CORS (localhost:4200) + bean BCryptPasswordEncoder
    ├── TrailingSlashFilter.kt # Acepta la barra final (Django sí, Spring no por defecto)
    └── DataLoader.kt          # Siembra datos al arrancar (CSV + especialistas demo)
```

### 6.2 Entidades (6)

| Entidad | Descripción |
|---|---|
| `Escenario` | Pregunta del cuestionario (categoría, enunciado, orden) con sus `Opciones` |
| `Opciones` | Respuesta posible (contenido, valor_puntos) ligada a un Escenario |
| `Usuario` | Alumno/paciente (datos + nivel_riesgo); clave **hasheada** |
| `Profesional` | Especialista (datos + especialidad, universidad, agenda, avatar) |
| `Reserva` | Cita usuario↔especialista (estado, fecha, motivo, contacto) |
| `Evaluacion` | **Nueva**: cada intento del cuestionario (puntaje, nivel, categoría, fecha) → historial |

### 6.3 Decisiones de diseño

- **Compatibilidad total con el Django original**: se respetan las rutas (`/api/...`) y el JSON
  (incluyendo nombres mixtos como `valor_puntos`, `idEscenario`, `nivel_riesgo`).
- **BCrypt** en vez de texto plano (la BD H2 arranca vacía de usuarios; se crean por registro).
- **`TrailingSlashFilter`**: reenvía `/api/x/` → `/api/x` para que el cliente funcione con la
  barra final que Django toleraba sola.
- **CORS** habilitado para `localhost:4200` (la web Angular sigue funcionando).
- **H2 en archivo** (`./data/mindshift.mv.db`): cero instalación, datos persistentes. Consola en
  `/h2-console`.
- **`DataLoader`** siembra al primer arranque: **60 escenarios + 240 opciones** (desde CSV) y
  **3 especialistas demo**. El cuestionario equivale a los scripts `importar_*.py` de Django.
- **Campos calculados de `Reserva`** (igual que los `SerializerMethodField` de DRF): el contacto
  y el nivel del alumno solo se revelan cuando la reserva está **Aceptada**.

---

## 7. App móvil — Kotlin + Jetpack Compose

### 7.1 Arquitectura MVVM

```
UI (@Composable) ──observa StateFlow──> ViewModel ──suspend──> Retrofit (ApiClient) ──> Backend
```

- Las pantallas solo dibujan y reaccionan al estado (`collectAsStateWithLifecycle`).
- Los ViewModels exponen el estado con `StateFlow` y llaman a la API en coroutines.
- Estado de carga genérico: `Resource<T>` = `Loading | Success(data) | Error`.
- Los ViewModels que usan la sesión son `AndroidViewModel` (acceden a DataStore).

### 7.2 Estructura (`app/src/main/java/com/mindshift/`)

```
├── MainActivity.kt              # Aplica el tema y arranca MindShiftApp()
├── data/
│   ├── ApiClient.kt             # Retrofit (BASE_URL, OkHttp, Gson)
│   ├── ApiService.kt            # Todos los endpoints
│   ├── SessionManager.kt        # Sesión con DataStore (+ data class Session)
│   └── model/Models.kt          # DTOs (reflejan el JSON del backend)
└── ui/
    ├── MindShiftApp.kt          # Scaffold + barra inferior + NavHost (navegación)
    ├── Resource.kt              # Loading/Success/Error
    ├── HomeScreen.kt            # Inicio (landing)
    ├── Cuestionario{Screen,ViewModel}.kt    # Test + autoguardado de evaluación
    ├── Especialistas{Screen,ViewModel}.kt   # Lista de especialistas
    ├── EspecialistaDetalleScreen.kt         # Detalle + solicitar cita
    ├── ReservaViewModel.kt                  # Envía la solicitud de cita
    ├── MisCitas{Screen,ViewModel}.kt        # Reservas del usuario
    ├── PanelEspecialista{Screen,ViewModel}.kt  # Solicitudes del especialista + Aceptar
    ├── Historial{Screen,ViewModel}.kt       # Historial de evaluaciones
    ├── Perfil{Screen,ViewModel}.kt          # Perfil + accesos por rol
    ├── AuthViewModel.kt                      # Login y registro
    └── theme/                                # Color.kt, Theme.kt, Type.kt (azul/blanco)
```

### 7.3 Pantallas y navegación

Barra inferior de 4 secciones + subpantallas internas:

```
🏠 Inicio        🧠 Test          🩺 Expertos              👤 Perfil
  HomeScreen     Cuestionario     Especialistas (lista)    Perfil
                                    └─ Detalle               ├─ (sin sesión) Login / Registro
                                       └─ Solicitar cita     ├─ (usuario) Mis citas · Mi historial
                                                             └─ (especialista) Solicitudes de cita
```

### 7.4 Funcionalidades por rol

| Función | Usuario | Especialista |
|---|---|---|
| Cuenta | Registro · Login · sesión persistente | Login |
| Cuestionario | Test interactivo → nivel; **autoguarda** la evaluación | — |
| Especialistas | Lista · detalle · **solicitar cita** | — |
| Reservas | **Mis citas** (estado + contacto si Aceptada) | **Panel**: ver y **aceptar** solicitudes |
| Historial | **Mi historial** (tests pasados) | — |

### 7.5 Decisiones de diseño

- **Tema fijo azul + blanco** (sin "dynamic color") para replicar la web; barras del sistema con
  íconos oscuros sobre el fondo blanco.
- **Íconos de la barra inferior = emoji** (🏠🧠🩺👤): sin dependencias extra.
- **Fechas con `SimpleDateFormat`** (no `java.time`) por compatibilidad con `minSdk 24`.
- **Sesión con DataStore**: reemplaza el `localStorage` del web; persiste entre reinicios.

---

## 8. Mapa de equivalencias

### Angular (web) → Compose (móvil)

| Angular | Compose |
|---|---|
| `home.component` | `HomeScreen` |
| `cuestionario.component` (+ pregunta-item, progreso) | `CuestionarioScreen` + `CuestionarioViewModel` |
| `buscador-expertos` / `panel` | `EspecialistasScreen` |
| `perfil-especialista` | `EspecialistaDetalleScreen` |
| `login.component` / `formulario-registro` | `LoginScreen` / `RegistroScreen` + `AuthViewModel` |
| `user-profile` | `PerfilScreen` + `MisCitasScreen` + `HistorialScreen` |
| `panel.especialista` | `PanelEspecialistaScreen` |
| `reserva-modal` | diálogo "Solicitar cita" en `EspecialistaDetalleScreen` |
| Servicios + `localStorage` | ViewModels + `SessionManager` (DataStore) |

### Django → Spring Boot

| Django | Spring Boot |
|---|---|
| `models.py` (Model) | Entidades `@Entity` (JPA) |
| `serializers.py` | DTOs + `Mappers.kt` |
| `views.py` (ViewSet) | `@RestController` + `Service` |
| `urls.py` (router) | `@RequestMapping` |
| `Model.objects` | `JpaRepository` |
| `importar_*.py` | `DataLoader` (CommandLineRunner) |

---

## 9. Contrato de la API (endpoints)

Base: `http://localhost:8000` · prefijo `/api`.

| Recurso | Endpoints |
|---|---|
| Escenarios | `GET/POST /escenarios/` · `GET/PUT/PATCH/DELETE /escenarios/{id}/` |
| Opciones | `GET/POST /opciones/` · `.../{id}/` |
| Usuarios | CRUD `/usuarios/` · `POST /usuarios/registro/` · `POST /usuarios/login/` |
| Profesionales | CRUD `/profesionales/` · `GET /profesionales/buscar?especialidad=` · `POST /profesionales/login/` |
| Reservas | CRUD `/reservas/` · `GET /reservas/?idUsuario=` · `/por-usuario` · `/por-profesional` · `PUT/PATCH /reservas/{id}/` |
| Evaluaciones | `GET /evaluaciones/?idUsuario=` · `POST /evaluaciones/` |

**Login** usa `{username, password}` (username = correo). **Registro** de usuario calcula el nivel
por puntaje. Una **Reserva Aceptada** revela el contacto; **Pendiente** lo oculta (`"Privado"`).

---

## 10. Cronología de módulos (incrementos)

Cada módulo se construyó, **probó en el celular real** y se commiteó por separado:

| # | Commit | Módulo |
|---|---|---|
| 1 | `3b1d0a1` | **Backend** Django → Spring Boot (5 modelos, API, H2, BCrypt) |
| 2 | `5cd1862` | App móvil + **cuestionario** (lista de preguntas) + guía de ejecución |
| 3 | `e32dd54` | **Home + navegación** (barra inferior, Navigation Compose) |
| 4 | `02fe0d1` | **Especialistas** (lista + detalle) + seed de 3 especialistas |
| 5 | `588492f` | Scripts `prender-todo` / `apagar-todo` |
| 6 | `d56afb4` | **Login / Registro** + sesión con DataStore |
| 7 | `70efe4f` | Reservas: **solicitar cita** desde el detalle |
| 8 | `71310d5` | Reservas: **Mis citas** (lista del usuario) |
| 9 | `4db5769` | **Panel del especialista** (ver y aceptar solicitudes) |
| 10 | `35260d4` | **Rediseño** a la paleta azul + blanco |
| 11 | `d97f942` | **Historial** de evaluaciones (backend + móvil) |
| 12 | `8235652` | Documentación de la app móvil |
| 13 | `6dfe17b` | Mejora del script (instala siempre la última versión) |

La rama `pene` (commit `052b143`) conserva el **código original** intacto.

---

## 11. Datos de prueba y credenciales

Sembrados por el `DataLoader` al primer arranque del backend:

- **Cuestionario**: 60 escenarios + 240 opciones (4 categorías × 15 preguntas:
  `ansiedad_social`, `tdah_burnout`, `depresion_apatia`, `perfeccionismo`).
- **Especialistas** (clave `especialista123`):
  - `lucia.fernandez@mindshift.pe` — Ansiedad y estrés — USIL
  - `carlos.mendoza@mindshift.pe` — TDAH y burnout — UPC
  - `andrea.rios@mindshift.pe` — Depresión y apatía — PUCP
- **Usuario**: se crea desde la pantalla de **Registro** de la app.

---

## 12. Cómo ejecutar

Resumen (detalle en [`COMO_EJECUTAR.md`](COMO_EJECUTAR.md)):

1. **Backend**: en `mindshift-backend-kotlin` → `gradlew bootRun` (queda en `:8000`).
2. **Celular**: activar Depuración USB; crear el túnel `adb reverse tcp:8000 tcp:8000`.
3. **App**: abrir `mindshift-mobile` en Android Studio y ▶ Run.

**Atajo:** doble clic en **`prender-todo.bat`** (verifica el celular, túnel, arranca el backend en
su ventana, **compila e instala la última versión** y abre la app). Para apagar: **`apagar-todo.bat`**.

> Requisitos: JDK 24 (o 21), Android Studio (incluye SDK + `adb`), y un celular Android (o emulador).

---

## 13. Verificación y pruebas

Cada módulo se verificó de extremo a extremo **en un celular físico (ZTE, Android 14)**:

- **Backend**: `gradlew build` (levanta el contexto Spring + H2) y *smoke tests* con `curl`
  (registro/login/CRUD/reservas con sus campos calculados).
- **App**: se instaló por `adb` y se navegó cada flujo; se confirmó contra el backend que las
  acciones persistían (p. ej., solicitar cita → reserva creada; aceptar → contacto revelado;
  terminar test → evaluación guardada en el historial).

---

## 14. Flujo de trabajo Git

- Repositorio: **`github.com/josecur/TF_DBP`**.
- Rama de trabajo: **`migracion-kotlin`** (creada desde `pene`, que queda intacta).
- **13 commits** que cuentan la historia ordenada de la migración (ver sección 10).
- Cada incremento es un commit independiente y verificado.
- El proyecto se movió a **`C:\Dev\TF_DBP`** (fuera de OneDrive) para evitar conflictos de git/build.

---

## 15. Problemas encontrados y soluciones

| Problema | Solución |
|---|---|
| Barra final (`/api/x/`) que Django acepta y Spring no | `TrailingSlashFilter` que reenvía la petición |
| `java.time.Instant` requiere API 26 (minSdk es 24) | Fechas con `SimpleDateFormat` |
| Login del especialista no devolvía `correo` | La sesión usa el correo tecleado en el login |
| `keyevent ESC` cerraba el diálogo de reserva en las pruebas | Enviar con motivo vacío (usa texto por defecto) |
| OneDrive rompía git y bloqueaba `build/` | Se **movió el repo fuera de OneDrive** (`C:\Dev`) |
| El backend no traía especialistas sembrados | `DataLoader` siembra 3 especialistas demo |
| Django guardaba un solo `nivel_riesgo` (sin historial) | Entidad nueva `Evaluacion` + endpoints |

---

## 16. Estado final y trabajo futuro

**Estado: migración completa.** La app móvil cubre todo el flujo usuario + especialista con el
diseño de la web, sobre un backend Spring Boot equivalente al Django original; todo versionado,
probado y documentado.

**Ideas opcionales a futuro** (no incluidas):
- Quitar `node_modules` del repositorio (está commiteado e infla el repo).
- Desplegar el backend (p. ej. PostgreSQL + un host) y apuntar la app por WiFi.
- Pedir una clave/validación al registrar especialistas (hoy se crean por el formulario).
- Notificaciones push cuando una cita es aceptada.
