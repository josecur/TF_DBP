@echo off
title MindShift - Prender todo
set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
set "ROOT=%~dp0"

echo ===========================================
echo    MindShift - Encendiendo el entorno
echo ===========================================
echo.

echo [1/4] Verificando el celular...
"%ADB%" start-server >nul 2>&1
"%ADB%" devices
echo.

echo [2/4] Creando el tunel USB (del celular al PC, puerto 8000)...
"%ADB%" reverse tcp:8000 tcp:8000 && echo     Tunel OK || echo     [AVISO] No se creo el tunel: revisa el cable y la depuracion USB.
echo.

echo [3/4] Iniciando el backend en una ventana aparte (NO la cierres)...
start "MindShift Backend" /D "%ROOT%mindshift-backend-kotlin" cmd /k gradlew.bat bootRun
echo     El backend tarda unos segundos en compilar y arrancar.
echo.

echo [4/4] Instalando y abriendo la app en el celular...
if exist "%ROOT%mindshift-mobile\app\build\outputs\apk\debug\app-debug.apk" (
    "%ADB%" install -r "%ROOT%mindshift-mobile\app\build\outputs\apk\debug\app-debug.apk" >nul 2>&1
    "%ADB%" shell am start -n com.mindshift/.MainActivity >nul 2>&1
    echo     App abierta. Si muestra "No se pudo conectar", espera a que el
    echo     backend termine de arrancar y toca "Reintentar".
) else (
    echo     [INFO] Aun no hay APK compilada. Abre Android Studio y dale Run una vez.
)
echo.
echo ===========================================
echo   Listo. Para programar, abre Android Studio.
echo   Para apagar todo, ejecuta: apagar-todo.bat
echo ===========================================
pause
