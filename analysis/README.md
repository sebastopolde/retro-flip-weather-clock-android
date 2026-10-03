# Analyse de compatibilité

L’ancien widget dépendait de HTC Sense et d’un launcher HTC. Le projet moderne reproduit la composition visuelle sous forme d’une application indépendante : `AppWidgetProvider`, `RemoteViews`, `TextClock`, stockage privé et API météo Open‑Meteo.

La météo est facultative : sans réseau ni cache, l’heure reste utilisable et le panneau indique l’état hors ligne. Une réponse réseau incomplète ne remplace jamais la dernière valeur valide.
