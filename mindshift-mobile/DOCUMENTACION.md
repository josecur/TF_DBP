# MindShift — App Móvil (Android nativo) — Documentación

Documentación de la **app móvil** de MindShift, migrada desde el frontend web (Angular) a
**Android nativo con Kotlin + Jetpack Compose**. Consume el backend Spring Boot
(`../mindshift-backend-kotlin`) que reemplazó al Django original.

---

## 1. Resumen

MindShift es una plataforma de bienestar psicológico: un **cuestionario** que detecta el nivel
de carga mental del usuario y lo conecta con **especialistas** para agendar **citas**. La app
móvil cubre el flujo completo de los dos roles (usuario y especialista).

| | Angular (web, original) | Android (móvil, esta migración) |
|---|---|---|
| Lenguaje/UI | TypeScript + HTML/Tailwind | Kotlin + Jetpack Compose |
| Estado | Servicios + signals | ViewModel + StateFlow |
| HTTP | HttpClient | Retrofit + OkHttp + Gson |
| Sesión | `localStorage` | DataStore (Preferences) |
| Ruteo | Angular Router | Navigation Compose |

---

## 2. Stack técnico

- **Kotlin** 2.2.10 · **Jetpack Compose** (BOM 2026.02.01) · **Material 3**
- **AGP** 9.2.1 · **minSdk** 24 (Android 7) · **targetSdk** 36
- **Arquitectura:** MVVM (Model–View–ViewModel)
- **Red:** Retrofit 2.11 + Gson + OkHttp (logging)
- **Sesión:** DataStore Preferences
- **Navegación:** Navigation Compose
- **Asíncrono:** Coroutines + StateFlow

---

## 3. Arquitectura (MVVM)

```
 UI (Composables)  →  ViewModel (StateFlow)  →  Repositorio/Red (ApiClient/Retrofit)  →  Backend
        ▲                      │
        └──── observa estado ──┘  (collectAsStateWithLifecycle)
```

- **Pantallas (`@Composable`)**: solo dibujan y reaccionan al estado; no llaman a la red directamente.
- **ViewModels**: exponen el estado con `StateFlow` y hacen las llamadas a la API en coroutines.
  Los que necesitan la sesión (login, reservas, historial) son `AndroidViewModel` para acceder a DataStore.
- **Estado de carga**: patrón genérico `Resource<T>` = `Loading | Success(data) | Error(mensaje)`.

---

## 4. Estructura del proyecto

```
app/src/main/java/com/mindshift/
├── MainActivity.kt                 # Punto de entrada; aplica el tema y arranca MindShiftApp()
├── data/
│   ├── ApiClient.kt                # Cliente Retrofit (BASE_URL, OkHttp, Gson)
│   ├── ApiService.kt               # Definición de todos los endpoints
│   ├── SessionManager.kt           # Sesión persistente con DataStore (+ data class Session)
│   └── model/
│       └── Models.kt               # DTOs (request/response) que reflejan el JSON del backend
└── ui/
    ├── MindShiftApp.kt             # Scaffold + barra inferior + NavHost (toda la navegación)
    ├── Resource.kt                 # Estado genérico Loading/Success/Error
    ├── HomeScreen.kt               # Pantalla de inicio (landing)
    ├── CuestionarioScreen.kt       # Cuestionario (selección → preguntas → resultado)
    ├── CuestionarioViewModel.kt    # Lógica del test + autoguardado de la evaluación
    ├── EspecialistasScreen.kt      # Lista de especialistas (+ Avatar, EstadoError reutilizables)
    ├── EspecialistasViewModel.kt   # Carga lista y detalle de especialistas
    ├── EspecialistaDetalleScreen.kt# Perfil del especialista + "Solicitar cita"
    ├── ReservaViewModel.kt         # Envía la solicitud de cita
    ├── MisCitasScreen.kt           # Reservas del usuario (+ EstadoChip, formatoFecha reutilizables)
    ├── MisCitasViewModel.kt
    ├── PanelEspecialistaScreen.kt  # Solicitudes que recibe el especialista + "Aceptar"
    ├── PanelEspecialistaViewModel.kt
    ├── HistorialScreen.kt          # Historial de evaluaciones del usuario
    ├── HistorialViewModel.kt
    ├── PerfilScreen.kt             # Perfil (datos de sesión + accesos por rol)
    ├── PerfilViewModel.kt          # Observa la sesión y permite cerrarla
    ├── AuthViewModel.kt            # Login (usuario/especialista) y registro
    └── theme/                      # Color.kt, Theme.kt, Type.kt (paleta azul/blanco)
```

---

## 5. Pantallas y funcionalidades

Navegación con **barra inferior** de 4 secciones + subpantallas internas:

| Sección / Pantalla | Rol | Qué hace |
|---|---|---|
| 🏠 **Inicio** (Home) | Todos | Landing: hero, áreas que evalúa, acceso a especialistas |
| 🧠 **Test** (Cuestionario) | Todos | Elegir categoría → responder con barra de progreso → puntaje → nivel (BAJO/MODERADO/CRÍTICO). Si hay sesión de usuario, **guarda la evaluación** |
| 🩺 **Expertos** | Todos | Lista de especialistas → **detalle** → **Solicitar cita** (usuario con sesión) |
| 👤 **Perfil** | Todos | Sin sesión: Login/Registro. Con sesión: datos + accesos por rol + cerrar sesión |
| **Login / Registro** | — | Login con pestañas Usuario/Especialista; registro de usuario |
| **Mis citas** | Usuario | Sus reservas con estado; si Aceptada, ve el contacto del especialista |
| **Solicitudes de cita** (Panel) | Especialista | Ve sus solicitudes y las **Acepta** (revela el contacto y nivel del alumno) |
| **Mi historial** | Usuario | Sus evaluaciones pasadas (categoría, nivel, puntaje, fecha) |

**Niveles de riesgo (cuestionario):** puntaje ≥ 31 → CRÍTICO · ≥ 16 → MODERADO · resto → BAJO.

---

## 6. Conexión con el backend

`data/ApiClient.kt` define `BASE_URL`. En desarrollo con celular por USB se usa
**`adb reverse tcp:8000 tcp:8000`**, por eso `BASE_URL = "http://localhost:8000/"`
(alternativa por WiFi: la IP del PC). Endpoints consumidos (`ApiService.kt`):

| Función | Método y ruta |
|---|---|
| Cuestionario | `GET /api/escenarios/` |
| Especialistas | `GET /api/profesionales/` · `GET /api/profesionales/{id}/` |
| Login usuario / especialista | `POST /api/usuarios/login/` · `POST /api/profesionales/login/` |
| Registro usuario | `POST /api/usuarios/registro/` |
| Reservas | `POST /api/reservas/` · `GET /api/reservas/?idUsuario=` · `GET /api/reservas/por-profesional` · `PATCH /api/reservas/{id}/` |
| Historial | `POST /api/evaluaciones/` · `GET /api/evaluaciones/?idUsuario=` |

---

## 7. Cómo ejecutar

Ver la guía general en `../COMO_EJECUTAR.md`. Resumen:
1. Levantar el backend (`../mindshift-backend-kotlin` → `gradlew bootRun`).
2. Conectar el celular (Depuración USB) y crear el túnel: `adb reverse tcp:8000 tcp:8000`.
3. Abrir `mindshift-mobile` en Android Studio y ▶ Run.
   (O usar los scripts `../prender-todo.bat` / `../apagar-todo.bat`.)

**Cuentas de especialista de prueba** (sembradas por el backend, clave `especialista123`):
`lucia.fernandez@mindshift.pe`, `carlos.mendoza@mindshift.pe`, `andrea.rios@mindshift.pe`.
El usuario se crea desde la pantalla de **Registro**.

---

## 8. Decisiones técnicas

- **Íconos de la barra inferior = emoji** (🏠🧠🩺👤): cero dependencias extra y se ven bien.
- **Fechas con `SimpleDateFormat`** (no `java.time`) para ser compatible con `minSdk 24`.
- **Tema fijo azul + blanco** (sin "dynamic color"): replica el look de la web.
- **`Evaluacion` es nueva en el backend**: el Django original solo guardaba un único
  `nivel_riesgo` por usuario; se agregó una entidad/endpoints para tener historial por fecha.
- **Compatibilidad de API**: se respetan las rutas y el JSON del backend (que a su vez replica
  los del Django original), por lo que ambos backends son intercambiables.
