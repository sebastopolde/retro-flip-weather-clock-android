$ErrorActionPreference = 'Stop'

$ProjectRoot = Split-Path -Parent $PSScriptRoot
$ToolingRoot = Join-Path $ProjectRoot '.tooling'
$StableJdk = Join-Path $ToolingRoot 'jdk17stable'
$JdkSearchRoot = if (Test-Path -LiteralPath $StableJdk) { $StableJdk } else { Join-Path $ToolingRoot 'jdk17' }
$JdkRoot = Get-ChildItem -LiteralPath $JdkSearchRoot -Directory |
    Select-Object -First 1 -ExpandProperty FullName
$SdkRoot = Join-Path $ToolingRoot 'android-sdk'
$AvdRoot = Join-Path $ToolingRoot 'avd'

if (-not $JdkRoot -or -not (Test-Path -LiteralPath (Join-Path $JdkRoot 'bin\java.exe'))) {
    throw 'JDK 17 absent. Lancez tools\bootstrap.ps1.'
}
if (-not (Test-Path -LiteralPath (Join-Path $SdkRoot 'platform-tools\adb.exe'))) {
    throw 'SDK Android absent. Lancez tools\bootstrap.ps1.'
}

$env:JAVA_HOME = $JdkRoot
$env:ANDROID_HOME = $SdkRoot
$env:ANDROID_SDK_ROOT = $SdkRoot
$env:ANDROID_AVD_HOME = $AvdRoot
$env:Path = @(
    (Join-Path $JdkRoot 'bin')
    (Join-Path $SdkRoot 'platform-tools')
    (Join-Path $SdkRoot 'emulator')
    (Join-Path $SdkRoot 'cmdline-tools\latest\bin')
    $env:Path
) -join [IO.Path]::PathSeparator
