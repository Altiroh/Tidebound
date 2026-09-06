# TB-CORE-014 — Fournisseur de langage Kotlin manquant

## Symptôme

Après le correctif `TB-CORE-013` (JourneyMap → Xaero's Minimap), l'utilisateur a pu relancer le
client sans planter, mais l'écran des mods de CurseForge a bloqué le lancement avec :

```
Mod File mods\libIPN-neoforge-1.21.1-6.6.2.jar needs language provider kotlinforge:5.7.0 or above to load
We have found -
Mod File mods\InventoryProfilesNext-neoforge-1.21-2.1.11.jar needs language provider kotlinforge:5.7.0 or above to load
We have found -
```

## Cause

`libIPN` et `Inventory Profiles Next` (ajoutés dans `TB-CORE-007`) sont écrits en Kotlin et déclarent
tous les deux une dépendance sur le fournisseur de langage FML `kotlinforge` (le mod **Kotlin for
Forge**), qui n'a jamais été ajouté au manifeste — un oubli au moment de l'ajout initial, jamais
détecté car l'environnement de développement Core ne charge pas de vrais mods tiers (seul un import
CurseForge réel exécute la résolution des fournisseurs de langage FML).

## Correctif

Ajout de **Kotlin for Forge** au manifeste : CurseForge projet `351264`, fichier `6706911`
(version `5.9.0`, canal Release, compatible Minecraft 1.21.1 / NeoForge et Forge) — satisfait
l'exigence `kotlinforge:5.7.0 ou plus`.

Fichiers modifiés :

- `modpack/manifest.json` : nouvelle entrée `351264`/`6706911` ;
- `core/tools/validate_content.py` : même ajout dans le dictionnaire `expected` (26 mods épinglés
  désormais, au lieu de 25) ;
- `modpack/README.md`, `docs/modpack/TB-PACK-001.md` : mention de Kotlin for Forge comme dépendance
  d'Inventory Profiles Next/libIPN.

## Vérification

- `python3 tools/validate_content.py` : `OK (... Devpack: 26 pinned mods)` ;
- recherche CurseForge confirmant que le fichier `6706911` est en canal Release et couvre
  Minecraft 1.21.1 avec NeoForge.

## Non vérifié

Comme pour `TB-CORE-012`/`TB-CORE-013`, aucun client réel n'est lancé dans cette session. Seul un
nouvel import du Devpack confirmera que l'écran d'erreur de chargement des mods disparaît et que le
client atteint le menu principal.
