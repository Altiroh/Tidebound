# TB-CORE-015 — libIPN hors de la plage acceptée par Inventory Profiles Next

## Symptôme

Après la correction du crash JourneyMap (`TB-CORE-012`/`013`) et l'ajout du fournisseur de langage
manquant (`TB-CORE-014`), l'utilisateur a pu avancer plus loin dans le chargement des mods et a
rencontré un nouveau blocage réel :

```
Mod inventoryprofilesnext requires libipn 6.5.1 or above, and below 6.6
Currently, libipn is 6.6.2
```

## Cause

`libIPN` (projet CurseForge 679177) était épinglé sur le fichier `7327448` (version `6.6.2`), au-delà
de la plage `[6.5.1, 6.6.0[` acceptée par la version d'`Inventory Profiles Next` déjà épinglée
(projet `495267`, fichier `6811490`). Incompatibilité présente depuis l'ajout initial de ces deux
mods (`TB-CORE-007`), jamais détectée car le crash JourneyMap bloquait le chargement avant d'y
arriver.

## Correctif

Remplacement par `libIPN-neoforge-1.21.1-6.5.1` (fichier CurseForge `6748206`, canal Release,
7 juillet 2025) — la borne basse exacte de la plage acceptée, choix le plus sûr puisqu'il satisfait
directement la contrainte annoncée par le message d'erreur plutôt que de deviner une version plus
récente d'Inventory Profiles Next dont la plage acceptée n'était pas vérifiable sans télécharger le
jar.

Fichiers modifiés :

- `modpack/manifest.json` : `fileID` de l'entrée libIPN (679177) passé de `7327448` à `6748206` ;
- `core/tools/validate_content.py` : même mise à jour dans le dictionnaire `expected`.

## Vérification

- `python3 tools/validate_content.py` : `OK`, toujours 26 mods épinglés.

## Non vérifié

Comme pour `TB-CORE-012`/`013`/`014`, aucun client réel n'est lancé dans cette session. Seul un
nouvel import du Devpack confirmera que le chargement des mods se termine sans erreur cette fois.
