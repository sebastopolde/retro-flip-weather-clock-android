# Retro Flip Weather Clock

Application Android moderne proposant une horloge rabattable rétro accompagnée de la météo actuelle et de prévisions sur quatre jours.

> Projet communautaire non officiel, sans affiliation avec HTC Corporation. HTC est une marque de ses propriétaires respectifs.

## Télécharger

La dernière version installable est disponible dans les [releases GitHub](https://github.com/sebastopolde/retro-flip-weather-clock-android/releases/latest).

Android demandera d’autoriser l’installation depuis le navigateur ou le gestionnaire de fichiers utilisé. Conservez Google Play Protect activé : l’APK release est signé, mais il est distribué hors Play Store.

## Fonctionnalités

- horloge rabattable au style rétro ;
- widget classique 4×3 ;
- widget hebdomadaire 4×4 avec quatre jours de prévision ;
- widget compact 4×3 avec météo actuelle et quatre jours ;
- adresse saisie manuellement ou obtenue par géolocalisation ;
- météo Open-Meteo avec cache hors ligne ;
- accès aux alarmes Android depuis la partie horloge ;
- page météo détaillée adaptée à l’adresse configurée ;
- compatibilité Android 6 à Android 16.

## Installation

1. Téléchargez le fichier APK depuis la dernière release.
2. Vérifiez, si souhaité, son empreinte SHA-256 publiée dans les notes de version.
3. Ouvrez l’APK sur le téléphone.
4. Autorisez temporairement l’installation depuis cette source si Android le demande.
5. Ouvrez l’application, choisissez une adresse puis ajoutez le widget désiré.

Les mises à jour doivent être signées avec la même clé que la version déjà installée. Ne mélangez pas une ancienne version debug avec la version release : désinstallez d’abord la version debug si Android refuse la mise à jour.

## Construire les sources

Sous Windows :

```powershell
.\tools\bootstrap.ps1
.\tools\test.ps1
```

L’environnement autonome est documenté dans [docs/ENVIRONMENT.md](docs/ENVIRONMENT.md) et les contrôles dans [docs/TESTING.md](docs/TESTING.md).

Pour générer une version release signée, créez `.signing/release.properties` et la clé indiquée par ce fichier, puis lancez :

```powershell
.\tools\build-release.ps1
```

La clé privée et les mots de passe ne doivent jamais être ajoutés au dépôt.

## Données et confidentialité

- aucune publicité ;
- aucun compte utilisateur ;
- aucune collecte analytique ;
- localisation demandée uniquement après action explicite de l’utilisateur ;
- météo fournie par [Open-Meteo](https://open-meteo.com/).

## Licence et illustrations

Le code original de ce dépôt est proposé sous licence MIT. Les illustrations historiques réutilisées pour préserver l’apparence du widget ne sont pas couvertes par cette licence et restent la propriété de leurs ayants droit. Consultez [NOTICE.md](NOTICE.md) avant toute redistribution ou modification publique.

## État du projet

Version actuelle : **0.1.0**. Le projet est fourni sans garantie et vise en priorité les appareils Android personnels.
