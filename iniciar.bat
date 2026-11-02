@echo off
chcp 65001 >nul
cd /d "%~dp0"
java -version >nul 2>&1
if errorlevel 1 (
 echo Instale o Java JDK 17 ou superior e abra este arquivo novamente.
 echo Consulte o README.md para ver as instrucoes.
 pause
 exit /b 1
)
if not exist "target\classes\br\edu\cinearquivo\Main.class" (
 echo Projeto ainda nao compilado. Execute: mvn clean package
 pause
 exit /b 1
)
echo Abra http://localhost:8080/filmes no navegador apos a inicializacao.
java -cp "target\classes;target\dependency\*" br.edu.cinearquivo.Main
pause
