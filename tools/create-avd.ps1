$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'env.ps1')

$Name = 'HTCClock_API36'
$AvdManager = Join-Path $env:ANDROID_HOME 'cmdline-tools\latest\bin\avdmanager.bat'
$existing = & $AvdManager list avd
if ($existing -notmatch "Name: $Name") {
    New-Item -ItemType Directory -Path $env:ANDROID_AVD_HOME -Force | Out-Null
    'no' | & $AvdManager create avd `
        --name $Name `
        --package 'system-images;android-36;google_apis;x86_64' `
        --device 'pixel_5' `
        --force
}
& $AvdManager list avd

