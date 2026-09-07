# TB-CORE-017 — JourneyMap remis en place (troisième tentative, informée)

## Contexte

L'utilisateur a explicitement demandé à retenter JourneyMap malgré les deux crashes identiques déjà
rencontrés (`TB-CORE-012` : `6.0.0-beta.76` ; `TB-CORE-013` : `6.0.7` stable), en acceptant le risque
d'un nouveau plantage. Une troisième tentative « à l'aveugle » (prendre la version la plus récente,
comme les deux précédentes) aurait été le même pari déjà perdu deux fois.

## Choix du fichier : téléchargements comme signal de stabilité réelle

Recherche CurseForge : les deux versions qui ont planté (`beta.76`, `6.0.7`) sont des builds peu
téléchargés. `journeymap-neoforge-1.21.1-6.0.0-beta.52.jar` (CurseForge fileID `6689206`), sorti le
23 juin 2025, affiche **4,7 millions de téléchargements** sur ce seul fichier — un ordre de grandeur
au-dessus, signe qu'une large base d'utilisateurs l'a réellement fait tourner sans que ce problème
précis y soit massivement rapporté. Ce n'est pas une garantie, mais un critère de sélection
différent et justifié plutôt qu'une répétition du même pari (« la plus récente disponible »).

## Changement

- `modpack/manifest.json` : l'entrée Xaero's Minimap (projet `263420`) remplacée par JourneyMap
  (projet `32274`, fichier `6689206`) ;
- `core/tools/validate_content.py` : même remplacement dans le dictionnaire `expected` ;
- toutes les mentions actives de Xaero's Minimap dans la documentation (`README.md`,
  `modpack/README.md`, `docs/modpack/TB-PACK-001.md`, `docs/DECISIONS.md`, `docs/TESTING.md`)
  repassées à JourneyMap. Les tickets historiques (`TB-CORE-012/013/014/016`) ne sont **pas**
  réécrits : ils décrivent fidèlement ce qui s'est passé au moment où ça s'est passé.

## Vérification

- `python3 tools/validate_content.py` : `OK`, toujours 27 mods épinglés.

## Non vérifié — risque assumé explicitement

Contrairement aux autres correctifs de cette session, celui-ci n'a **pas** de garantie de résolution :
c'est un troisième essai sur un problème dont la cause exacte reste inconnue. Si `beta.52` plante à
son tour, ce sera un vrai signal qu'il faut soit abandonner JourneyMap pour de bon, soit chercher la
cause dans l'interaction avec un autre mod du pack (Sodium/Iris/ImmediatelyFast apparaissent tous dans
la pile d'appel du crash d'origine) plutôt que dans le choix de version.
