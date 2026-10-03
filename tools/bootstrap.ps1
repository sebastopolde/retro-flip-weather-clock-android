$ErrorActionPreference = 'Stop'

$ProjectRoot = Split-Path -Parent $PSScriptRoot
$ToolingRoot = Join-Path $ProjectRoot '.tooling'
$SdkRoot = Join-Path $ToolingRoot 'android-sdk'
$CommandLineArchive = Join-Path $ToolingRoot 'commandlinetools-win-15859902_latest.zip'
$CommandLineUrl = 'https://dl.google.com/android/repository/commandlinetools-win-15859902_latest.zip'
$CommandLineSha256 = '90ae805d20434428bffcb699c290860f19bb5f66a67e6b330067e3de801fb04a'
$JdkArchiveName = 'OpenJDK17U-jdk_x64_windows_hotspot_17.0.20.1_1.zip'
$JdkBaseUrl = 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.20.1%2B1'
$JdkArchive = Join-Path $ToolingRoot $JdkArchiveName

New-Item -ItemType Directory -Path $ToolingRoot -Force | Out-Null

if (-not (Test-Path -LiteralPath (Join-Path $ToolingRoot 'jdk17'))) {
    Invoke-WebRequest -Uri "$JdkBaseUrl/$JdkArchiveName" -OutFile $JdkArchive
    Invoke-WebRequest -Uri "$JdkBaseUrl/$JdkArchiveName.sha256.txt" -OutFile "$JdkArchive.sha256.txt"
    $expected = ((Get-Content -LiteralPath "$JdkArchive.sha256.txt" -Raw).Trim() -split '\s+')[0]
    $actual = (Get-FileHash -LiteralPath $JdkArchive -Algorithm SHA256).Hash
    if ($actual -ne $expected) { throw "Empreinte JDK invalide: $actual" }
    Expand-Archive -LiteralPath $JdkArchive -DestinationPath (Join-Path $ToolingRoot 'jdk17') -Force
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

& (Join-Path $PSScriptRoot 'create-avd.ps1')

