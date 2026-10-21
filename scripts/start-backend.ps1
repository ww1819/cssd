$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
Set-Location $PSScriptRoot\..\backend
Write-Host 'Starting cssd-app on :8080 (need PostgreSQL cssd/cssd @ localhost:5432)'
mvn -DskipTests spring-boot:run -pl cssd-app -am
