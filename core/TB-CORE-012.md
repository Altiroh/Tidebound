# TB-CORE-012 — Crash au lancement : JourneyMap beta épinglé

## Symptôme

Rapport de crash réel envoyé par l'utilisateur depuis son PC de test (devpack `0.29.0-alpha`) :

```
java.lang.NullPointerException: Cannot invoke "journeymap.client.JourneymapClient.isInitialized()"
because the return value of "journeymap.client.JourneymapClient.getInstance()" is null
	at ...Minecraft.handler$zdk000$journeymap$onGameLoadFinishedMixin(Minecraft.java:4509)
```

Crash au tout premier écran de chargement (« Rendering overlay », 3,4 s après le lancement, avant
tout monde ou menu) — aucune trace de code Tidebound dans la pile d'appel. Le mixin fautif appartient
entièrement à JourneyMap lui-même.

## Cause

Le fichier épinglé dans `modpack/manifest.json` (projet CurseForge 32274, fichier 8116777) était
`journeymap-neoforge-1.21.1-6.0.0-beta.76.jar` — une préversion bêta, accompagnée d'un
`journeymap-api-neoforge-2.0.0-1.21.1-SNAPSHOT.jar` tiré automatiquement comme dépendance. Cette
combinaison bêta+snapshot plante avant même l'initialisation complète du client.

## Correctif

Remplacement par la dernière version **Release** stable de JourneyMap pour NeoForge 1.21.1 :
`journeymap-1.21.1-6.0.7+neoforge` (fichier CurseForge 8764294). Cette version documente son API
comme directement intégrée (« Implements JourneyMap API v2.0.0-1.21.1 » sans dépendance externe
séparée) : le jar `journeymap-api-...SNAPSHOT.jar` ne devrait plus être tiré du tout à l'import.

Fichiers modifiés :

- `modpack/manifest.json` : `fileID` de l'entrée JourneyMap (32274) passé de `8116777` à `8764294` ;
- `core/tools/validate_content.py` : même mise à jour dans le dictionnaire `expected` qui pinne les
  versions attendues du Devpack.

## Vérification

- `python3 tools/validate_content.py` : `OK`, toujours 25 mods épinglés ;
- recherche CurseForge confirmant que 8764294 est bien un fichier de canal **Release** (pas Beta/Alpha)
  pour Minecraft 1.21.1 / NeoForge.

## Non vérifié

Impossible de relancer le client réel dans cette session (l'environnement de développement Core ne
lance qu'un serveur dédié, sans JourneyMap). La correction lève la cause identifiée (bêta instable +
dépendance snapshot), mais seul un nouvel import du Devpack sur la machine de l'utilisateur confirmera
que le client démarre normalement.

## Correctif insuffisant — voir `TB-CORE-013`

Le nouvel import a planté à l'identique, au même endroit, avec `journeymap-neoforge-1.21.1-6.0.7.jar`
(canal Release, pas bêta) et sa vraie dépendance `journeymap-api-neoforge-2.0.0-1.21.1.jar` (pas un
snapshot). Deux versions différentes de JourneyMap plantent donc de la même façon : ce n'est pas un
problème de version précise mais une instabilité connue de JourneyMap lui-même sur cette combinaison
NeoForge 1.21.1 (des rapports similaires existent sur son dépôt GitHub, `TeamJM/journeymap` issues
#792 et #856). Continuer à changer de version aurait été deviner une troisième fois. Décision : sortir
JourneyMap du pack plutôt que de le maintenir — voir `TB-CORE-013`.
