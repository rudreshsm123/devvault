@echo off
rem Starts DevVault in the background (no window). MySQL runs as the Windows service MySQL267.
cd /d "%~dp0"
if not exist target\devvault-0.0.1-SNAPSHOT.jar call mvnw.cmd -q package -DskipTests
powershell -NoProfile -WindowStyle Hidden -Command "Start-Process -WindowStyle Hidden -FilePath 'C:\Users\user\.gradle\jdks\eclipse_adoptium-21-amd64-windows.2\bin\java.exe' -ArgumentList '-jar','target\devvault-0.0.1-SNAPSHOT.jar' -RedirectStandardOutput devvault.log -RedirectStandardError devvault.err.log"
