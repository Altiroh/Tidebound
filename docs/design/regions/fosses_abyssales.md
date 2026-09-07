# Fosses Abyssales — spécification de région

Statut : **implémenté (couche biome uniquement)**, troisième biome du palier Extension/V1.5 de
`docs/design/Tidebound_Roadmap_Biomes.md` (`TB-WORLD-007`). Rejoint le lore existant, §16-17 de
`docs/design/Tidebound_Lore.md` (Les abysses, Reliques abyssales et vitalité).

## Identité

Grandes déchirures océaniques, obscurité verticale, faune bioluminescente, ruines englouties. Pêche
abyssale, plongée avancée, reliques, aberrations et espèces rares — cœur du mid/late game étrange,
révélation de la couche lovecraftienne.

## Différence avec `abyss_ocean` (déjà existant depuis `TB-WORLD-002`)

`abyss_ocean` était déjà la réponse initiale à « eaux profondes dangereuses », mais la roadmap veut
un palier encore plus rare et plus extrême pour les Fosses. Plutôt que dupliquer un concept déjà
posé, les Fosses sont carvées **à l'intérieur même** de la tranche `abyss_ocean` existante
(`erosion [0.9, 1.0]` dans les `[0.7, 1.0]` d'`abyss_ocean`) : une poche encore plus rare dans une
poche déjà rare, cohérent avec « cœur du mid/late game », pas une simple redite.

## Réalisation technique

`tidebound:abyssal_trench`, clone de `minecraft:deep_ocean` (même base qu'`abyss_ocean`), teinté
quasi noir neutre (`water_color: 526350`, `water_fog_color: 197382`) plutôt que le bleu sombre
d'`abyss_ocean` — une nuance délibérément distincte, pas la même couleur réutilisée.

Ajouté au tag `#tidebound:dangerous` : les dégâts de coque progressifs s'y appliquent, cohérent avec
un palier plus extrême qu'`abyss_ocean`.

## Ce qui n'est pas dans cette tranche

- pas de faune bioluminescente dédiée (aucune entité/mécanique de ce type n'existe) ;
- pas de ruines englouties (aucune structure placée dans ce biome pour l'instant — pourrait
  réutiliser la technique de `WreckPlacementService`/`TB-WORLD-005` dans un ticket futur, à base de
  pierre plutôt que de bois) ;
- pas de reliques ni d'aberrations liées à ce biome spécifiquement (le système de reliques du lore,
  §17, n'est pas encore implémenté du tout dans le code, indépendamment de ce biome) ;
- pas de mécanique de « plongée avancée » ou de « pêche abyssale » distincte de la pêche normale.

Cette tranche pose la couche biome/danger, cohérente avec le reste de la session ; le contenu de
gameplay propre aux Fosses (ruines, reliques, faune) reste un chantier futur bien plus large.
