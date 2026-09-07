# TB-WORLD-003 — Marais des Lanternes (premier biome nommé de la nouvelle roadmap)

## Contexte

Suite de `docs/design/Tidebound_Roadmap_Biomes.md` (cadrage reçu le 7 septembre 2026) et de sa
spécification détaillée `docs/design/regions/marais_des_lanternes.md`. Premier des dix biomes
nommés à être réellement implémenté, choisi par l'utilisateur comme point de départ car c'est le
seul biome MVP entièrement nouveau, sans recoupement avec l'existant.

Décision produit tranchée avec l'utilisateur avant d'implémenter : **marée simplifiée** (pas de
changement de niveau d'eau réel, un cycle périodique change seulement une fenêtre de bonus) plutôt
qu'une vraie simulation de marée physique, jugée trop lourde et jamais tentée dans ce projet.

## Implémentation

1. **Biome `tidebound:lantern_marsh`** (`data/tidebound/worldgen/biome/lantern_marsh.json`) — clone
   exact de `minecraft:mangrove_swamp` (extrait du jar client réel comme référence, même méthode que
   `violet_shallows`/`abyss_ocean`), seuls `water_color`/`water_fog_color` changés vers une teinte
   ambrée évoquant une lueur de lanterne (`11829800`/`7226900`).
2. **Table de biomes** — les deux fichiers de preset (`data/minecraft/worldgen/world_preset/
   normal.json` et `data/tidebound/worldgen/world_preset/archipelago.json`, gardés identiques comme
   toujours) : la cellule `beach` existante (`continentalness [0.05, 0.16]`, `humidity [-0.15, 1.0]`)
   est resserrée à `humidity [-0.15, 0.7]`, et `tidebound:lantern_marsh` occupe la tranche restante
   `humidity [0.7, 1.0]` — le marais apparaît en poches rares, humides, jamais en remplacement massif
   des plages.
3. **`dev.tidebound.core.world.TideCycle`** (nouveau) — cycle de marée simplifié : `isLowTide(long
   dayTime)` sur une période de 48 000 ticks (deux jours en jeu), sans aucun changement de terrain.
4. **`CatchService.stampFishedItem`** — le drapeau `eerieWater` (déjà utilisé pour l'océan la nuit,
   boost de taux d'anomalie dans `CatchGenerator`) s'active désormais aussi dans le Marais des
   Lanternes la nuit à marée basse. Premier cas réel où le biome d'origine influence la génération
   d'une prise, pas seulement son affichage — comme identifié dans la spécification de région.

## Ce qui n'est pas dans cette tranche

- pas de nouvelle espèce de poisson (toujours limité aux quatre profils vanilla, `TB-CORE-010`) ;
- pas de biais de tirage d'espèce par biome (le marais donne les mêmes espèces que partout ailleurs,
  seule la chance d'anomalie change) ;
- pas de structure/PNJ dédiée à ce biome pour l'instant ;
- pas de contrat régional spécifique.

## Vérification

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK` ;
- démarrage réel d'un serveur dédié sur un monde neuf généré avec la nouvelle table de biomes :
  `Done`, aucune erreur.

## Non vérifié

Comme pour tout le reste de la variété de biomes (`TB-WORLD-002`), aucune vérification visuelle en
jeu n'est possible dans cette session : teinte réelle de l'eau, rareté effective des poches de marais,
et perception du bonus nocturne à marée basse restent à confirmer par l'utilisateur.
