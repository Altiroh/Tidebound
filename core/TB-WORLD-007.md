# TB-WORLD-007 — Palier Extension/V1.5 : trois biomes nommés

## Contexte

Suite de `docs/design/Tidebound_Roadmap_Biomes.md` après les cinq biomes MVP (`TB-WORLD-003` à
`006`). Traite les trois biomes du palier « Extension/V1.5 » en un seul lot : Récifs du Verre Noir,
Mer Brumeuse, Fosses Abyssales. Spécifications détaillées dans `docs/design/regions/
recifs_du_verre_noir.md`, `mer_brumeuse.md` et `fosses_abyssales.md`.

## Résumé

| Biome | Base vanilla | Carvage | Tag dangereux |
|---|---|---|---|
| `tidebound:glass_reef` | `warm_ocean` (récifs coralliens hérités) | érosion dans la cellule `warm_ocean` | oui |
| `tidebound:foggy_sea` | `cold_ocean` | érosion dans la cellule `cold_ocean` | non — tension, pas de danger physique |
| `tidebound:abyssal_trench` | `deep_ocean` (même base qu'`abyss_ocean`) | érosion **à l'intérieur** de la tranche `abyss_ocean` existante | oui |

Les deux fichiers de preset (`normal.json`/`archipelago.json`) restent identiques comme toujours,
26 → 29 entrées.

`foggy_sea` est la première fois que `effects.fog_color` (couleur du brouillard atmosphérique, pas
seulement l'eau) est modifié dans ce projet. Décision explicite : pas de vraie réduction de
visibilité dans cette tranche — le format de biome ne l'expose pas simplement, et deviner un hook de
rendu client sans pouvoir le vérifier visuellement était jugé trop risqué. Voir
`docs/design/regions/mer_brumeuse.md`.

## Vérification

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK` ;
- démarrage réel d'un serveur dédié sur un monde neuf généré avec la table de biomes à 29 entrées :
  `Done`, aucune erreur.

## Non vérifié

Comme tous les biomes ajoutés dans cette session : teinte réelle de l'eau/du brouillard, rareté
effective des trois poches (chacune plus étroite que les biomes MVP, imbriquées dans des tranches
d'érosion déjà réduites), et perception de la dangerosité restent à confirmer par l'utilisateur.
