# MindShift — Guía de ejecución (stack Kotlin)

Esta guía explica cómo levantar el proyecto migrado a **Kotlin**:
- **Backend:** Spring Boot + H2 (`mindshift-backend-kotlin/`)
- **App móvil ("front"):** Android nativo con Jetpack Compose (`mindshift-mobile/`)

> El proyecto original (Angular + Django) sigue en el repo como referencia de la migración
> (`mindshift-frontend/` y `mindshift-backend/`). Ver la sección *Legado* al final.

---

## Requisitos

| Herramienta | Para qué |
|---|---|
| **JDK 24** (o 21) | Compilar Kotlin |
| **Android Studio** | Abrir y correr la app móvil (incluye el SDK y `adb`) |
| **Celular Android** con Depuración USB | Probar la app (o un emulador) |

> El backend trae su propio **Gradle wrapper** (`gradlew`), no necesitas instalar Gradle.

---

## 1) Levantar el BACKEND (Spring Boot)

Desde la carpeta `mindshift-backend-kotlin/`:

```bash
# Windows (PowerShell)
.\gradlew.bat bootRun

# Git Bash / Linux / Mac
./gradlew bootRun
```

- Queda escuchando en **http://localhost:8000**
- En el **primer arranque** siembra solo **60 escenarios + 240 opciones** (lee los CSV de `src/main/resources/data/`).
- Consola de la base de datos: **http://localhost:8000/h2-console**
  - JDBC URL: `jdbc:h2:file:./data/mindshift` · Usuario: `sa` · Contraseña: *(vacía)*
- Para **detenerlo**: `Ctrl + C` en esa terminal.

---

## 2) Conectar el CELULAR

1. En el celular: **Ajustes → Acerca del teléfono →** toca 7 veces *"Número de compilación"* para activar **Opciones de desarrollador**.
2. Activa **Depuración por USB**.
3. Conéctalo con un **cable de DATOS** (no de solo carga).
4. Verifica que `adb` lo detecte:

```bash
adb devices
# Debe listar tu equipo con estado "device" (no "unauthorized")
```

> `adb` está en `…\AppData\Local\Android\Sdk\platform-tools\adb.exe`.
> Lo más cómodo es usar la **Terminal integrada de Android Studio** (ya tiene `adb` disponible),
> o agregar esa carpeta `platform-tools` al `PATH` de Windows.

---

## 3) Túnel USB (¡paso clave!)

Para que el celular pueda hablar con el backend de tu PC por el cable:

```bash
adb reverse tcp:8000 tcp:8000
```

Esto hace que `localhost:8000` **del celular** apunte al `localhost:8000` **de tu PC**.
Por eso la app usa `BASE_URL = "http://localhost:8000/"` (en `mindshift-mobile/app/src/main/java/com/mindshift/data/ApiClient.kt`).

> ⚠️ El túnel se **pierde** cada vez que desconectas el celular o se reinicia `adb`.
> Vuelve a ejecutar el comando tras reconectar.
>
> **Alternativa por WiFi (sin cable):** cambia `BASE_URL` por la IP de tu PC
> (ej. `http://192.168.0.104:8000/`) y conecta el celular a la misma red WiFi.

---

## 4) Levantar la APP MÓVIL

1. Abre la carpeta **`mindshift-mobile/`** en Android Studio.
2. Espera a que termine el **Gradle sync** (abajo dice *"Sync finished"*).
3. Con el **celular conectado** y el **backend corriendo**, dale **▶ Run**.
4. Para una exposición: la ventana **"Running Devices"** espeja la pantalla del celular para proyectarla.

---

## ✅ Checklist rápido al RECONECTAR el celular

1. `adb devices` → ¿aparece como **device**? Si no: cambia cable/puerto y acepta el popup *"Permitir depuración USB"*.
2. `adb reverse tcp:8000 tcp:8000` → reactivar el túnel.
3. ¿El **backend** está corriendo en `:8000`?
4. **▶ Run** en Android Studio.

---

## Problemas comunes

| Síntoma | Causa probable / solución |
|---|---|
| El celular no aparece en `adb devices` | Cable de **solo carga** → usa uno de datos; o falta aceptar el popup de depuración USB. |
| La app muestra *"No se pudo conectar al backend"* | El backend no está corriendo, **o** falta el `adb reverse` tras reconectar. |
| Build lento / RAM al máximo | **Pausa OneDrive** y cierra pestañas del navegador mientras desarrollas. |

> 💡 **Rendimiento:** desarrolla con el **celular físico** (más ligero que el emulador) y mantén
> pausada la sincronización de OneDrive mientras trabajas.

---

## Legado (opcional, solo como referencia)

El stack original sigue versionado:

- **Frontend Angular** (`mindshift-frontend/`): `npm install` y luego `npm start` → http://localhost:4200
- **Backend Django** (`mindshift-backend/`): `python manage.py runserver` → http://localhost:8000

> No corras el Django y el Spring Boot a la vez: ambos usan el puerto **8000**.
