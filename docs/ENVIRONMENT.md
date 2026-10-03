# Environnement de développement

L’environnement est autonome dans `.tooling/` et n’est pas versionné.

## Versions vérifiées

- JDK de compilation: Eclipse Temurin 17.0.16+8
- Gradle: 8.11.1, local au projet
- Android command-line tools: 15859902
- Android Debug Bridge: 1.0.41 / 37.0.1-15733141
- Android Emulator: 37.2.12.0
- Android SDK Platform: API 36
- Android SDK Build Tools: 36.0.0
- Image système: Android 16, Google APIs, x86_64
- AVD: `HTCClock_API36`, profil Pixel 5, série ADB dédiée `emulator-5580`
- Accélération: WHPX disponible et utilisable

## Utilisation

```powershell
.\tools\bootstrap.ps1
.\tools\start-emulator.ps1
.\tools\install-emulator.ps1
```

Pour un démarrage automatisé sans fenêtre :

```powershell
.\tools\start-emulator.ps1 -Headless
```

Pour charger les outils dans le terminal courant :

```powershell
. .\tools\env.ps1
adb devices
```

Le SDK, le JDK, Gradle, l’émulateur et ses disques virtuels restent confinés dans le projet. Le premier montage demande environ 6 Go, davantage après création des instantanés de l’émulateur. La révision JDK 17.0.16 est volontaire : elle évite un défaut de canal local observé avec 17.0.20.1 sous cette version de Windows.
