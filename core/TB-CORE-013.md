# TB-CORE-013 — Remplacement de JourneyMap par Xaero's Minimap

## Contexte

Suite de `TB-CORE-012`. Deux versions distinctes de JourneyMap (`6.0.0-beta.76` puis la version
Release `6.0.7`, avec sa vraie dépendance `journeymap-api-neoforge-2.0.0-1.21.1` non-snapshot) ont
planté au client avec exactement la même exception, au même endroit (`onGameLoadFinishedMixin`,
avant tout monde/menu). Des rapports similaires existent côté JourneyMap lui-même
(`TeamJM/journeymap` issues #792, #856) : c'est une instabilité connue du mod sur cette ligne
NeoForge 1.21.1, pas un problème de fichier précis épinglé par erreur.

## Décision

Retirer JourneyMap du Devpack plutôt que de continuer à essayer des versions au hasard, et le
remplacer par **Xaero's Minimap** (`neoforge-1.21.1-26.1.0`, canal Release, CurseForge projet
`263420`, fichier `8231212`) pour la même fonction (« Carte et retours rapides entre lieux
découverts », `TB-PACK-001`). Xaero's Minimap ne déclare aucune dépendance externe séparée sur cette
version — pas de risque équivalent au couple journeymap/journeymap-api.

## Fichiers touchés

- `modpack/manifest.json` : l'entrée JourneyMap (`32274`) remplacée par Xaero's Minimap (`263420`) ;
- `core/tools/validate_content.py` : même remplacement dans le dictionnaire `expected` ;
- `README.md`, `modpack/README.md`, `docs/modpack/TB-PACK-001.md`, `docs/DECISIONS.md`,
  `docs/TESTING.md` : toutes les mentions actives de JourneyMap remplacées par Xaero's Minimap.

## Vérification

- `python3 tools/validate_content.py` : `OK`, toujours 25 mods épinglés ;
- recherche CurseForge confirmant que le fichier `8231212` est en canal **Release** pour
  Minecraft 1.21.1 / NeoForge, sans dépendance supplémentaire déclarée.

## Non vérifié

Comme pour `TB-CORE-012`, aucun client réel n'est lancé dans cette session (serveur dédié uniquement).
Seul un nouvel import du Devpack confirmera que le client démarre et que la minimap fonctionne. Si
Xaero's Minimap pose à son tour un problème, le traiter comme un vrai bug de ce mod plutôt que de
retenter un remplacement au jugé.
