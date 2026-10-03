$ErrorActionPreference = 'Stop'
$ProjectRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot 'env.ps1')
$Gradle = Join-Path $ProjectRoot '.tooling\gradle-8.11.1\bin\gradle.bat'
if (-not (Test-Path -LiteralPath $Gradle)) { throw 'Gradle absent. Lancez tools\bootstrap.ps1.' }
Push-Location $ProjectRoot
try {
    & $Gradle assembleDebug compileDebugUnitTestKotlin --no-daemon --max-workers=1
    if ($LASTEXITCODE -ne 0) { throw "Échec Gradle (code $LASTEXITCODE)." }
} finally { Pop-Location }
