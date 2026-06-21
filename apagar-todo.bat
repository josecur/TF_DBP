@echo off
title MindShift - Apagar todo
set "ROOT=%~dp0"

echo ===========================================
echo    MindShift - Apagando el entorno
echo ===========================================
echo.

echo [1/4] Deteniendo el backend (puerto 8000)...
powershell -NoProfile -Command "$c=Get-NetTCPConnection -LocalPort 8000 -State Listen -ErrorAction SilentlyContinue; if($c){$c|Select-Object -ExpandProperty OwningProcess -Unique|ForEach-Object{Stop-Process -Id $_ -Force -ErrorAction SilentlyContinue}; Write-Output '     Backend detenido.'}else{Write-Output '     El backend ya estaba apagado.'}"
echo.

echo [2/4] Cerrando la ventana del backend...
taskkill /FI "WINDOWTITLE eq MindShift Backend*" /T /F >nul 2>&1
echo     Hecho.
echo.

echo [3/4] Deteniendo los daemons de Gradle (libera RAM)...
call "%ROOT%mindshift-mobile\gradlew.bat" -p "%ROOT%mindshift-mobile" --stop >nul 2>&1
call "%ROOT%mindshift-backend-kotlin\gradlew.bat" -p "%ROOT%mindshift-backend-kotlin" --stop >nul 2>&1
echo     Daemons detenidos.
echo.

echo [4/4] Cerrando Android Studio...
taskkill /IM studio64.exe /F >nul 2>&1 && echo     Android Studio cerrado. || echo     Android Studio no estaba abierto.
echo.

echo (El tunel adb se libera solo al desconectar el celular.)
echo ===========================================
echo   Todo apagado.
echo ===========================================
pause
