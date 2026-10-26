$ErrorActionPreference = 'Stop'
# Prefer machine JDK 17 (registry / common paths); avoid Maven falling back to JDK 8.
$candidates = @(
  $env:CSSD_JAVA_HOME,
  'D:\Program Files\Java\jdk-17',
  'C:\Program Files\Java\jdk-17'
) | Where-Object { $_ }
$jdk = $candidates | Where-Object { Test-Path (Join-Path $_ 'bin\javac.exe') } | Select-Object -First 1
if (-not $jdk) {
  throw "JDK 17 not found. Set CSSD_JAVA_HOME or install to D:\Program Files\Java\jdk-17"
}
$env:JAVA_HOME = $jdk
$env:Path = "$env:JAVA_HOME\bin;" + ($env:Path -split ';' | Where-Object { $_ -and $_ -notmatch 'Java\\latest\\jdk-1\.8|javapath' }) -join ';'
Write-Host "JAVA_HOME=$env:JAVA_HOME"
& "$env:JAVA_HOME\bin\java.exe" -version
Set-Location $PSScriptRoot\..\backend
Write-Host 'Starting cssd-app on :8080 (need PostgreSQL cssd/cssd @ localhost:5432)'
# -am 会把 spring-boot:run 误加到 parent；先安装依赖，再只对 cssd-app 跑 run
mvn -DskipTests install "-pl" cssd-app "-am" "-Dspring-boot.repackage.skip=true"
mvn -DskipTests spring-boot:run "-pl" cssd-app
