# Tester le widget

## Test immédiat déjà prêt

L’émulateur `HTCClock_API36` contient l’application et le widget 4×2. Le widget affiche l’heure en continu grâce à `TextClock`, interroge Open‑Meteo au toucher et conserve la dernière météo valide hors ligne.

1. Lancer `tools\start-emulator.ps1`.
2. Lancer `tools\install-emulator.ps1` si l’application doit être reconstruite.
3. Dans l’application, choisir une ville puis **Actualiser la météo**.
4. Appuyer sur **Ajouter le widget à l’accueil**, confirmer dans le lanceur.

## Vérifications automatisées

- `tools\test.ps1` compile l’APK et exécute les 13 tests unitaires.
- APK de développement : `app\build\outputs\apk\debug\app-debug.apk`.
- Paquet Android : `fr.papycha.htcweatherclock`.
- Compatibilité déclarée : Android 6/API 23 à Android 16/API 36.

Les commandes ADB des scripts ciblent explicitement `emulator-5580`; elles ne modifient pas le téléphone Samsung connecté.
