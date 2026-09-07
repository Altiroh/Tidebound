# Mer Morte — spécification de région

Statut : **implémenté**, premier biome du palier Endgame de `docs/design/Tidebound_Roadmap_Biomes.md`
(`TB-WORLD-008`).

## Identité

Eau plate, absence d'oiseaux, presque aucun son, bancs de poissons rarissimes, horizon vide. Zone
pauvre en ressources courantes, recherche de poissons spéciaux, sentiment d'expédition lointaine —
biome psychologique autant que ludique, le calme devient inquiétant.

## Réalisation technique

`tidebound:dead_calm`, clone de `minecraft:lukewarm_ocean` — deux changements, pas un seul comme les
biomes précédents :

1. **Teinte** : eau et brouillard d'eau vers un gris-vert délavé et plat (`water_color: 7897720`,
   `water_fog_color: 4608075`), cohérent avec « horizon vide ».
2. **Suppression des apparitions de faune aquatique** : `spawners.water_ambient`,
   `spawners.water_creature` et `spawners.underground_water_creature` vidés (morues, poissons
   tropicaux, poissons-globes, calmars, dauphins, calmars luisants — tous retirés). Première fois
   dans ce projet que la section `spawners` d'un biome est modifiée plutôt que seulement `effects`.
   Aucun poisson vivant visible dans l'eau, cohérent avec « bancs de poissons rarissimes » et
   « presque aucun son ».

**La pêche à la canne reste entièrement fonctionnelle** : elle utilise le mécanisme de pêche vanilla
(table de butin sur l'hameçon), indépendant de la présence d'entités de poissons nageant à
proximité. Un joueur peut toujours pêcher dans ce biome ; il ne verra simplement jamais de poisson
nager avant de mordre — l'effet recherché.

Pas de tag `#tidebound:dangerous` : la tonalité est le malaise psychologique, pas un danger physique
pour la coque — même logique que la Mer Brumeuse.

Carvé par érosion dans la cellule `lukewarm_ocean` existante (`humidity [0.2, 0.55]`,
`continentalness [-0.35, 0.05]`) : `erosion [0.85, 1.0]` → Mer Morte, le reste inchangé.

## Ce qui n'est pas dans cette tranche

- pas de poisson « spécial » distinct (toujours les quatre profils vanilla de `CatchProfiles`,
  aucun bonus de rareté propre à ce biome — même limite déjà documentée pour le Marais) ;
- pas de réduction des sons ambiants au-delà de ce que la suppression des créatures aquatiques
  implique déjà indirectement (moins de sons de poisson/dauphin) ;
- pas de mécanique de « sentiment d'expédition lointaine » (aucune notion de distance/durée de trajet
  suivie dans le projet).

## Vérification

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK` ;
- démarrage réel d'un serveur dédié sur un monde neuf : `Done`, aucune erreur.

## Non vérifié

Comme tous les biomes de cette session : teinte réelle, rareté de la poche, et perception effective
du calme (absence de poissons visibles) restent à confirmer par l'utilisateur.
