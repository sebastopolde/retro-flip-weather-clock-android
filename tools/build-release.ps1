$ErrorActionPreference = 'Stop'
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$PropertiesPath = Join-Path $ProjectRoot '.signing\release.properties'

if (-not (Test-Path -LiteralPath $PropertiesPath)) {
    throw 'Configuration de signature absente : .signing\release.properties'
}

. (Join-Path $PSScriptRoot 'env.ps1')
$Gradle = Join-Path $ProjectRoot '.tooling\gradle-8.11.1\bin\gradle.bat'

Push-Location $ProjectRoot
try {
    & $Gradle clean assembleRelease --no-daemon --max-workers=1
    if ($LASTEXITCODE -ne 0) { throw "Échec Gradle release (code $LASTEXITCODE)." }

    $Apk = Join-Path $ProjectRoot 'app\build\outputs\apk\release\app-release.apk'
    $BuildTools = Get-ChildItem (Join-Path $env:ANDROID_SDK_ROOT 'build-tools') -Directory |
        Sort-Object Name -Descending | Select-Object -First 1
    $ApkSigner = Join-Path $BuildTools.FullName 'apksigner.bat'
    & $ApkSigner verify --verbose --print-certs $Apk
    if ($LASTEXITCODE -ne 0) { throw "Signature APK invalide (code $LASTEXITCODE)." }

    $Dist = Join-Path $ProjectRoot 'dist'
    New-Item -ItemType Directory -Force -Path $Dist | Out-Null
    $Output = Join-Path $Dist 'Retro-Flip-Weather-Clock-0.1.0.apk'
    Copy-Item -Force -LiteralPath $Apk -Destination $Output
    Get-FileHash -LiteralPath $Output -Algorithm SHA256
} finally {
    Pop-Location
}
