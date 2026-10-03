$ErrorActionPreference = 'Stop'
$ProjectRoot = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot 'build.ps1')
. (Join-Path $PSScriptRoot 'env.ps1')
$cache = Join-Path $env:USERPROFILE '.gradle\caches\modules-2\files-2.1'
$junit = Get-ChildItem (Join-Path $cache 'junit\junit\4.13.2') -Recurse -Filter 'junit-4.13.2.jar' | Select-Object -First 1 -ExpandProperty FullName
$hamcrest = Get-ChildItem (Join-Path $cache 'org.hamcrest\hamcrest-core\1.3') -Recurse -Filter '*.jar' | Select-Object -First 1 -ExpandProperty FullName
$kotlin = Get-ChildItem (Join-Path $cache 'org.jetbrains.kotlin\kotlin-stdlib\2.1.20') -Recurse -Filter '*.jar' | Select-Object -First 1 -ExpandProperty FullName
$classpath = @(
    (Join-Path $ProjectRoot 'app\build\tmp\kotlin-classes\debugUnitTest')
    (Join-Path $ProjectRoot 'app\build\tmp\kotlin-classes\debug')
    $junit; $hamcrest; $kotlin
) -join [IO.Path]::PathSeparator
& (Join-Path $env:JAVA_HOME 'bin\java.exe') -cp $classpath org.junit.runner.JUnitCore `
    fr.papycha.htcweatherclock.widget.WidgetLayoutSelectorTest `
    fr.papycha.htcweatherclock.widget.WidgetKindSelectorTest `
    fr.papycha.htcweatherclock.clock.ClockFormatterTest `
    fr.papycha.htcweatherclock.weather.WeatherRepositoryTest `
    fr.papycha.htcweatherclock.weather.HtcWeatherPresentationTest `
    fr.papycha.htcweatherclock.weather.ForecastDayFormatterTest `
    fr.papycha.htcweatherclock.storage.WeatherStorageCodecTest `
    fr.papycha.htcweatherclock.storage.ForecastStorageCodecTest `
    fr.papycha.htcweatherclock.work.RefreshDecisionTest
if ($LASTEXITCODE -ne 0) { throw "Échec JUnit (code $LASTEXITCODE)." }
