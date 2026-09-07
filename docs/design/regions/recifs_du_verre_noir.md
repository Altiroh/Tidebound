# Récifs du Verre Noir — spécification de région

Statut : **implémenté**, premier biome du palier Extension/V1.5 de
`docs/design/Tidebound_Roadmap_Biomes.md` (`TB-WORLD-007`).

## Identité

Récifs tranchants, coraux sombres, eau turquoise mais dangereuse, relief sous-marin dense. Risque de
collision, faune plus rare, plongée rentable, cartographie précise — biome « technique » où la
lecture du terrain compte.

## Différence avec les Falaises Abyssales

Déjà notée dans `docs/design/regions/falaises_abyssales.md` : les Falaises sont un relief **émergé**
(côte à pic, on la longe en surface), les Récifs sont un relief **sous-marin** (on y plonge). Ne pas
les confondre.

## Réalisation technique

`tidebound:glass_reef`, clone de `minecraft:warm_ocean` (extrait du jar client réel) — choix
déterminant : `warm_ocean` génère déjà nativement des récifs coralliens dans ses `features`, hérités
tels quels par le clone. « Coraux sombres » n'est réalisé qu'en teinte d'eau (turquoise sombre,
`water_color: 1344140`) : le corail lui-même reste coloré comme en vanilla — en changer la couleur
demanderait de nouveaux blocs, hors scope vanilla-friendly de cette tranche.

Carvé par érosion dans la cellule `warm_ocean` existante (`humidity [0.55, 1.0]`,
`continentalness [-0.35, 0.05]`) : `erosion [0.7, 1.0]` → récifs, `erosion [-1.0, 0.7]` reste
`warm_ocean` normal — même schéma exact que `violet_shallows`/`abyss_ocean`.

Ajouté au tag `#tidebound:dangerous` : le « risque de collision » se traduit par les dégâts de coque
progressifs déjà en place (`HullIntegrityEvents`), aucun code Java nouveau.

## Ce qui n'est pas dans cette tranche

- pas de corail teinté sombre (nécessiterait de nouveaux blocs) ;
- pas de mécanique de « cartographie précise » distincte (aucune notion de précision de carte
  n'existe dans le projet) ;
- pas de faune rare spécifique à ce biome (les espèces de poisson restent celles de
  `CatchProfiles`/`TB-CORE-010`, aucun biais par biome — même limite déjà documentée pour le Marais).
