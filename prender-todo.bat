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

echo [2/4] Tunel USB + backend (ventana aparte, NO la cierres)...
"%ADB%" reverse tcp:8000 tcp:8000 && echo     Tunel OK || echo     [AVISO] No se creo el tunel: revisa el cable y la depuracion USB.
start "MindShift Backend" /D "%ROOT%mindshift-backend-kotlin" cmd /k gradlew.bat bootRun
echo     El backend arranca en su propia ventana (tarda unos segundos).
echo.

echo [3/4] Compilando e instalando la ULTIMA version de la app...
echo     (compila desde el codigo actual; la primera vez puede tardar)
call "%ROOT%mindshift-mobile\gradlew.bat" -p "%ROOT%mindshift-mobile" installDebug
if errorlevel 1 (
    echo     [AVISO] No se pudo instalar. Revisa que el celular este conectado y autorizado.
) else (
    echo     App instalada (version mas reciente).
)
echo.

echo [4/4] Abriendo la app en el celular...
"%ADB%" shell am start -n com.mindshift/.MainActivity >nul 2>&1
echo.
echo ===========================================
echo   Listo. El backend corre en la ventana "MindShift Backend".
echo   Si la app dice "No se pudo conectar", espera a que el
echo   backend termine de arrancar y toca "Reintentar".
echo   Para apagar todo: apagar-todo.bat
echo ===========================================
pause
