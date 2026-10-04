$ErrorActionPreference = 'Stop'
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$forbiddenRemoteViews = Get-ChildItem (Join-Path $ProjectRoot 'app\src\main\res\layout') -Filter 'widget_*.xml' |
    Select-String -Pattern '<View(?:\s|>)'
if ($forbiddenRemoteViews) { throw "Vue interdite dans un layout RemoteViews : $($forbiddenRemoteViews.Path):$($forbiddenRemoteViews.LineNumber)" }
$androidNamespace = 'http://schemas.android.com/apk/res/android'
[xml]$manifest = Get-Content -Raw -LiteralPath (Join-Path $ProjectRoot 'app\src\main\AndroidManifest.xml')
$manifestNamespace = New-Object System.Xml.XmlNamespaceManager($manifest.NameTable)
$manifestNamespace.AddNamespace('android', $androidNamespace)
$alarmPermission = $manifest.SelectSingleNode("/manifest/uses-permission[@android:name='com.android.alarm.permission.SET_ALARM']", $manifestNamespace)
if (-not $alarmPermission) { throw "La permission Android d'ajout d'alarme est absente du manifeste." }
$application = $manifest.SelectSingleNode('/manifest/application')
if ($application.GetAttribute('fullBackupContent', $androidNamespace) -ne '@xml/backup_rules' -or
    $application.GetAttribute('dataExtractionRules', $androidNamespace) -ne '@xml/data_extraction_rules') {
    throw 'Les adresses et coordonnées doivent être exclues des sauvegardes Android.'
}
Get-ChildItem (Join-Path $ProjectRoot 'app\src\main\res\layout') -Filter 'widget_*.xml' | ForEach-Object {
    [xml]$layout = Get-Content -Raw -LiteralPath $_.FullName
    $missingDimensions = @($layout.SelectNodes('//*') | Where-Object {
        -not $_.HasAttribute('layout_width', $androidNamespace) -or -not $_.HasAttribute('layout_height', $androidNamespace)
    })
    if ($missingDimensions) { throw "Dimensions directes manquantes dans le layout RemoteViews $($_.Name) : $($missingDimensions[0].Name)" }
}
[xml]$weeklyLayout = Get-Content -Raw -LiteralPath (Join-Path $ProjectRoot 'app\src\main\res\layout\widget_weekly.xml')
$namespaceManager = New-Object System.Xml.XmlNamespaceManager($weeklyLayout.NameTable)
$namespaceManager.AddNamespace('android', $androidNamespace)
$weeklyIds = @($weeklyLayout.SelectNodes('//*[@android:id]', $namespaceManager) | ForEach-Object {
    $_.GetAttribute('id', $androidNamespace)
})
if ('@+id/current_weather_panel' -notin $weeklyIds -or '@+id/forecast_panel' -notin $weeklyIds) {
    throw 'Le widget hebdomadaire doit utiliser deux panneaux météo séparés.'
}
$fullSurfaceId = $weeklyLayout.DocumentElement.FirstChild.GetAttribute('id', $androidNamespace)
if ($fullSurfaceId -eq '@+id/weather_panel') {
    throw "La cible de rafraîchissement météo ne doit pas recouvrir l'horloge."
}
$forecastColumns = @($weeklyIds | Where-Object { $_ -match '^@\+id/day_\d+$' })
if (Compare-Object $forecastColumns @('@+id/day_1', '@+id/day_2', '@+id/day_3', '@+id/day_4')) {
    throw 'La maquette hebdomadaire doit afficher exactement quatre jours.'
}
$weeklyClockNodes = @($weeklyLayout.SelectNodes("//*[@android:id='@+id/hour_panel' or @android:id='@+id/minute_panel']", $namespaceManager))
if ($weeklyClockNodes.Count -ne 2 -or @($weeklyClockNodes | Where-Object { $_.GetAttribute('style') -ne '@style/HtcWeeklyFlipDigits' }).Count -ne 0) {
    throw 'Le widget hebdomadaire doit utiliser un style de chiffres isolé des widgets classiques.'
}
[xml]$weeklyInfo = Get-Content -Raw -LiteralPath (Join-Path $ProjectRoot 'app\src\main\res\xml\weekly_weather_widget_info.xml')
if ($weeklyInfo.'appwidget-provider'.GetAttribute('targetCellHeight', $androidNamespace) -ne '4') {
    throw 'Le widget hebdomadaire doit cibler une hauteur de quatre cellules.'
}
[xml]$classicInfo = Get-Content -Raw -LiteralPath (Join-Path $ProjectRoot 'app\src\main\res\xml\flip_clock_widget_info.xml')
if ($classicInfo.'appwidget-provider'.GetAttribute('targetCellHeight', $androidNamespace) -ne '3' -or
    $classicInfo.'appwidget-provider'.GetAttribute('resizeMode', $androidNamespace) -notmatch 'vertical') {
    throw 'Le widget classique doit réellement conserver son format 4×3 redimensionnable.'
}
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
    fr.papycha.htcweatherclock.RequestGateTest `
    fr.papycha.htcweatherclock.PreviewPageSelectorTest `
    fr.papycha.htcweatherclock.widget.WidgetLayoutSelectorTest `
    fr.papycha.htcweatherclock.widget.WidgetKindSelectorTest `
    fr.papycha.htcweatherclock.widget.WidgetActionSpecTest `
    fr.papycha.htcweatherclock.widget.WidgetPinResultTest `
    fr.papycha.htcweatherclock.widget.WidgetSyncPlanTest `
    fr.papycha.htcweatherclock.clock.ClockFormatterTest `
    fr.papycha.htcweatherclock.location.LocationProviderSpecTest `
    fr.papycha.htcweatherclock.weather.WeatherRepositoryTest `
    fr.papycha.htcweatherclock.weather.GeocodingSpecTest `
    fr.papycha.htcweatherclock.weather.HtcWeatherPresentationTest `
    fr.papycha.htcweatherclock.weather.ForecastDayFormatterTest `
    fr.papycha.htcweatherclock.weather.WeeklyWeatherFormatterTest `
    fr.papycha.htcweatherclock.storage.WeatherStorageCodecTest `
    fr.papycha.htcweatherclock.storage.LocationStorageCodecTest `
    fr.papycha.htcweatherclock.storage.WidgetStateTransferTest `
    fr.papycha.htcweatherclock.storage.ForecastStorageCodecTest `
    fr.papycha.htcweatherclock.work.RefreshDecisionTest `
    fr.papycha.htcweatherclock.work.RefreshRevisionTest
if ($LASTEXITCODE -ne 0) { throw "Échec JUnit (code $LASTEXITCODE)." }
