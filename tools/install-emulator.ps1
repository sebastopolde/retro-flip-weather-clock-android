$ErrorActionPreference = 'Stop'
$ProjectRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot 'env.ps1')
& (Join-Path $PSScriptRoot 'build.ps1')
$adb = Join-Path $env:ANDROID_HOME 'platform-tools\adb.exe'
& $adb -s emulator-5580 install -r (Join-Path $ProjectRoot 'app\build\outputs\apk\debug\app-debug.apk')
& $adb -s emulator-5580 shell am start -W -n 'fr.papycha.htcweatherclock/.MainActivity'
