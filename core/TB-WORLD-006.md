# TB-WORLD-006 — Archipel des Brisants (dernier biome MVP)

## Contexte

Cinquième et dernier biome MVP de `docs/design/Tidebound_Roadmap_Biomes.md`. Spécification détaillée
dans `docs/design/regions/archipel_des_brisants.md`.

Contrairement aux trois biomes précédents (`TB-WORLD-003`, `004`, `005`), celui-ci ne nécessitait
presque aucun nouveau code : vérification faite contre le code réel, « petits phares », « ports
secondaires » et « commerce léger » correspondent déjà à des systèmes en place
(`PortArchetype.LIGHTHOUSE_OUTPOST`, `RegionalPortEvents`, archétypes de commerce), et
« enchaînement d'îlots » est déjà une propriété du preset `archipelago`. Le seul élément
manquant était les **récifs**.

## Implémentation

`HarborPlacementService.buildReef` (nouveau) : 6 à 9 rochers dispersés dans les hauts-fonds de part
et d'autre du quai, uniquement pour l'archétype `LIGHTHOUSE_OUTPOST`. Chaque rocher est posé sur le
vrai fond marin local (`Heightmap.Types.OCEAN_FLOOR`), pas à une hauteur fixe, pour éviter tout
rocher flottant si la profondeur varie d'un point à l'autre — même précaution que
`WreckPlacementService.findSeabed` dans `TB-WORLD-005`. Mélange de pierre/pierre moussue/cobblestone/
andésite pour un aspect naturel plutôt qu'un bloc unique répété. Déterministe par site
(`RandomSource.create(plan.siteId())`).

## Bilan : les cinq biomes MVP sont traités

Avec cette tranche, `docs/design/Tidebound_Roadmap_Biomes.md` a ses cinq biomes MVP couverts, à des
degrés d'ambition assumés et documentés dans chaque note de région :

| Biome | Ticket | Ampleur réelle |
|---|---|---|
| Côte paisible | `TB-WORLD-001`/`002` | déjà couverte par l'archipel de départ |
| Marais des Lanternes | `TB-WORLD-003` | nouveau biome + marée simplifiée + bonus de pêche + contrat |
| Falaises Abyssales | `TB-WORLD-004` | nouveau biome + tag dangereux (aucun code Java neuf) |
| Plateau des Épaves | `TB-WORLD-005` | nouveau système de structure + première table de butin |
| Archipel des Brisants | `TB-WORLD-006` | une seule fonction de décoration, tout le reste existait |

Prochaines étapes de la roadmap (hors MVP) : Récifs du Verre Noir, Mer Brumeuse, Fosses Abyssales
(extension/V1.5), puis Mer Morte et Mer Extérieure (endgame) — voir
`docs/design/Tidebound_Roadmap_Biomes.md`.

## Vérification

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK` ;
- démarrage réel d'un serveur dédié sur un monde neuf : `Done`, aucune erreur.

## Non vérifié

`buildReef` n'a jamais été exercé par un vrai joueur dans cette session (nécessite un port
`LIGHTHOUSE_OUTPOST` réellement matérialisé). Même limite que toutes les additions procédurales de
cette session : compilation et démarrage serveur propres, rendu réel non vérifié.
