param(
    [switch]$Headless,
    [switch]$ColdBoot,
    [ValidateRange(5554, 5682)]
    [int]$Port = 5580
)

$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'env.ps1')

$Emulator = Join-Path $env:ANDROID_HOME 'emulator\emulator.exe'
$arguments = @('-avd', 'HTCClock_API36', '-port', $Port, '-no-boot-anim')
if ($Headless) { $arguments += @('-no-window', '-gpu', 'swiftshader_indirect') }
if ($ColdBoot) { $arguments += '-no-snapshot-load' }

if ($Headless) {
    $process = Start-Process -FilePath $Emulator -ArgumentList $arguments -WindowStyle Hidden -PassThru
} else {
    $process = Start-Process -FilePath $Emulator -ArgumentList $arguments -PassThru
}
Write-Host "Emulateur démarré (PID $($process.Id))."

$Adb = Join-Path $env:ANDROID_HOME 'platform-tools\adb.exe'
$Serial = "emulator-$Port"
& $Adb -s $Serial wait-for-device
$deadline = (Get-Date).AddMinutes(5)
do {
    Start-Sleep -Seconds 2
    $booted = (& $Adb -s $Serial shell getprop sys.boot_completed 2>$null).Trim()
} while ($booted -ne '1' -and (Get-Date) -lt $deadline)

if ($booted -ne '1') { throw "L’émulateur n’a pas fini de démarrer dans le délai prévu." }
& $Adb -s $Serial shell input keyevent 82
Write-Host 'Android 16 est prêt.'
