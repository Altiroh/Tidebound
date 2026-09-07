# Mer Brumeuse — spécification de région

Statut : **implémenté (couche visuelle uniquement)**, deuxième biome du palier Extension/V1.5 de
`docs/design/Tidebound_Roadmap_Biomes.md` (`TB-WORLD-007`).

## Identité

Nappe de brouillard perpétuel, cloches lointaines, repères effacés, lueurs diffuses. Navigation à
faible visibilité, importance des phares et du compas, rencontres rares, événements d'ambiance —
tension douce, pas de jumpscare, perte de repères.

## Décision : pas de vraie réduction de visibilité dans cette tranche

« Navigation à faible visibilité » impliquerait de réduire la distance de rendu ou d'épaissir
réellement le brouillard client dans ce biome. Aucun mécanisme de ce type n'existe dans le projet ;
vanilla ne l'expose pas au niveau d'un simple fichier de biome (`fog_color` teinte la couleur du
brouillard atmosphérique à l'horizon, il ne réduit pas la portée de vue). L'implémenter réellement
demanderait soit un effet de statut client, soit un hook de rendu dédié — plus risqué à deviner sans
retour visuel possible dans cette session, laissé à un ticket futur plutôt qu'improvisé.

Cette tranche pose uniquement la couche que le format de biome permet honnêtement : une teinte
d'eau et de brouillard pâle et délavée, cohérente avec l'ambiance recherchée, sans prétendre à un
vrai effet de visibilité réduite.

## Réalisation technique

`tidebound:foggy_sea`, clone de `minecraft:cold_ocean` — `water_color`, `water_fog_color` et
`fog_color` tous les trois retintés vers un gris-blanc pâle et délavé (`9213595`/`11186100`/
`13159890`), la première fois que `fog_color` (couleur du brouillard atmosphérique, pas seulement
l'eau) est modifié dans ce projet plutôt que laissé à sa valeur vanilla.

Carvé par érosion dans la cellule `cold_ocean` existante (`humidity [-0.45, -0.15]`,
`continentalness [-0.35, 0.05]`) : `erosion [0.7, 1.0]` → mer brumeuse, le reste inchangé.

Pas de tag `#tidebound:dangerous` : la tonalité est la tension et la perte de repères, pas un danger
physique pour la coque — volontairement distinct des Falaises/Récifs/Fosses.

## Ce qui n'est pas dans cette tranche

- pas de vraie réduction de visibilité (voir décision ci-dessus) ;
- pas de « cloches lointaines » (aucun son d'ambiance dédié ajouté) ;
- pas de « rencontres rares » ni d'événements spécifiques à ce biome.
