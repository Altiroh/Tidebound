# TB-WORLD-008 — Palier Endgame : Mer Morte implémentée, Mer Extérieure différée

## Contexte

Dernier palier de `docs/design/Tidebound_Roadmap_Biomes.md`. Deux biomes prévus : Mer Morte et Mer
Extérieure. Traités différemment l'un de l'autre, délibérément.

## Mer Morte — implémentée

Voir `docs/design/regions/mer_morte.md`. `tidebound:dead_calm`, clone de `minecraft:lukewarm_ocean` :
teinte gris-vert délavée **et**, pour la première fois dans ce projet, suppression complète des
apparitions de faune aquatique (`spawners.water_ambient`/`water_creature`/
`underground_water_creature` vidés) — une eau visuellement vide de poissons, cohérent avec « bancs de
poissons rarissimes » et « presque aucun son ». La pêche à la canne reste pleinement fonctionnelle
(mécanisme indépendant de la présence d'entités de poissons nageant à proximité).

Carvé par érosion dans la cellule `lukewarm_ocean` existante, pas de tag `#tidebound:dangerous`
(malaise psychologique, pas un danger physique — même logique que la Mer Brumeuse).

## Mer Extérieure — différée délibérément

Voir `docs/design/regions/mer_exterieure.md` pour le raisonnement complet. Contrairement à tous les
biomes précédents, aucun des trois piliers de sa fantasy (poissons légendaires, structures majeures,
météo instable) n'a de système existant à réutiliser ou à formaliser — un simple clone d'océan teinté
serait une coquille vide plutôt qu'une implémentation honnête. **Pas cochée dans `ROADMAP.md`.**

## Vérification (Mer Morte)

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK` ;
- démarrage réel d'un serveur dédié sur un monde neuf : `Done`, aucune erreur.

## Non vérifié

Comme tous les biomes de cette session : teinte réelle et absence visible de poissons dans
`dead_calm` restent à confirmer par l'utilisateur.
