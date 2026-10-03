$ErrorActionPreference = 'Stop'

$ProjectRoot = Split-Path -Parent $PSScriptRoot
$ToolingRoot = Join-Path $ProjectRoot '.tooling'
$SdkRoot = Join-Path $ToolingRoot 'android-sdk'
$CommandLineArchive = Join-Path $ToolingRoot 'commandlinetools-win-15859902_latest.zip'
$CommandLineUrl = 'https://dl.google.com/android/repository/commandlinetools-win-15859902_latest.zip'
$CommandLineSha256 = '90ae805d20434428bffcb699c290860f19bb5f66a67e6b330067e3de801fb04a'
$JdkArchiveName = 'OpenJDK17U-17.0.16.zip'
$JdkUrl = 'https://api.adoptium.net/v3/binary/version/jdk-17.0.16%2B8/windows/x64/jdk/hotspot/normal/eclipse'
$JdkSha256 = '8c7cfff78a55c56ebaf470ed6a89c6466b47d8274bdabdda997d7507c20325c5'
$JdkArchive = Join-Path $ToolingRoot $JdkArchiveName

New-Item -ItemType Directory -Path $ToolingRoot -Force | Out-Null

if (-not (Test-Path -LiteralPath (Join-Path $ToolingRoot 'jdk17stable'))) {
    Invoke-WebRequest -Uri $JdkUrl -OutFile $JdkArchive
    $actual = (Get-FileHash -LiteralPath $JdkArchive -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actual -ne $JdkSha256) { throw "Empreinte JDK invalide: $actual" }
    Expand-Archive -LiteralPath $JdkArchive -DestinationPath (Join-Path $ToolingRoot 'jdk17stable') -Force
}

if (-not (Test-Path -LiteralPath (Join-Path $SdkRoot 'cmdline-tools\latest\bin\sdkmanager.bat'))) {
    Invoke-WebRequest -Uri $CommandLineUrl -OutFile $CommandLineArchive
    $actual = (Get-FileHash -LiteralPath $CommandLineArchive -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actual -ne $CommandLineSha256) { throw "Empreinte outils Android invalide: $actual" }
    $extractRoot = Join-Path $ToolingRoot 'cmdline-extract'
    Expand-Archive -LiteralPath $CommandLineArchive -DestinationPath $extractRoot -Force
    $latest = Join-Path $SdkRoot 'cmdline-tools\latest'
    New-Item -ItemType Directory -Path (Split-Path $latest) -Force | Out-Null
    Move-Item -LiteralPath (Join-Path $extractRoot 'cmdline-tools') -Destination $latest
}

. (Join-Path $PSScriptRoot 'env.ps1')
$SdkManager = Join-Path $SdkRoot 'cmdline-tools\latest\bin\sdkmanager.bat'
1..200 | ForEach-Object { 'y' } | & $SdkManager --sdk_root=$SdkRoot --licenses | Out-Null
& $SdkManager --sdk_root=$SdkRoot `
    'platform-tools' `
    'emulator' `
    'platforms;android-36' `
    'build-tools;36.0.0' `
    'system-images;android-36;google_apis;x86_64'

$GradleZip = Join-Path $ToolingRoot 'gradle-8.11.1-bin.zip'
$GradleHome = Join-Path $ToolingRoot 'gradle-8.11.1'
$GradleSha256 = 'f397b287023acdba1e9f6fc5ea72d22dd63669d59ed4a289a29b1a76eee151c6'
if (-not (Test-Path -LiteralPath (Join-Path $GradleHome 'bin\gradle.bat'))) {
    Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.11.1-bin.zip' -OutFile $GradleZip
    $actual = (Get-FileHash -LiteralPath $GradleZip -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actual -ne $GradleSha256) { throw "Empreinte Gradle invalide: $actual" }
    Expand-Archive -LiteralPath $GradleZip -DestinationPath $ToolingRoot -Force
}
$GradleBat = Join-Path $GradleHome 'bin\gradle.bat'
$GradleScript = Get-Content -LiteralPath $GradleBat -Raw
$GradleScript = $GradleScript.Replace('set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"', 'set DEFAULT_JVM_OPTS="-Xmx512m" "-Xms256m" "-XX:MaxMetaspaceSize=384m" "-XX:+HeapDumpOnOutOfMemoryError"')
Set-Content -LiteralPath $GradleBat -Value $GradleScript -Encoding Ascii

& (Join-Path $PSScriptRoot 'create-avd.ps1')
