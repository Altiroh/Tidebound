# Plateau des Épaves — spécification de région

Statut : **conception + première implémentation**, troisième biome nommé de
`docs/design/Tidebound_Roadmap_Biomes.md`. Répond enfin à la question laissée en suspens depuis
`TB-WORLD-002` : « gros navires échoués rares (nécessite soit une structure `.nbt`, soit un
générateur procédural dédié — aucun des deux commencé) ».

## Identité

Ancienne route commerciale jonchée de coques, bouées cassées, mâts émergents, cargaisons perdues.
Exploration d'épaves, récupération de pièces mécaniques, journaux de bord, cartes au trésor — biome
de milieu de partie riche en boucle loot / réparation / amélioration.

## Décision : procédural, toujours pas de `.nbt`

Cohérent avec la décision prise en `TB-WORLD-002` (« Garde le système actuel et on verra si on doit
prendre une autre direction »). Les épaves de cette première tranche sont un **squelette de coque
procédural borné**, construit bloc par bloc comme `HarborPlacementService.buildHut` — pas la
structure `.nbt` authentique d'un « gros navire échoué rare » qui restait l'ambition initiale.

Ce que ça implique concrètement : la coque est modeste (9 blocs de long, murs partiels et irréguliers
tirés d'une graine déterministe par site), pas le grand navire spectaculaire décrit dans la fantasy
du biome. C'est un premier pas assumé, pas la version finale — comme le bâtiment de port de
`TB-CORE-008` l'était déjà pour les quais.

## Pas de nouveau biome de couleur

Contrairement au Marais et aux Falaises, ce n'est pas un carvage de la table `multi_noise` : une
« route commerciale jonchée d'épaves » est une propriété de **placement d'objets** dans l'océan
existant, pas une identité de terrain/couleur d'eau. Les épaves peuvent apparaître dans n'importe quel
biome océanique déjà en place (`ocean`, `deep_ocean`, `violet_shallows`...).

## Implémentation

- `WreckPlan` (`dev.tidebound.core.world`) : identité déterministe par région de 512 blocs, sur le
  même principe de hachage que `PortPlan` mais totalement indépendante (pas d'archétype ni de
  services — un seul type d'épave pour cette tranche).
- `WreckRegistry` (`SavedData` sur l'Overworld) : index des sites déjà matérialisés, version
  simplifiée de `HarborRegistry` (un simple ensemble d'identifiants, pas de métadonnées de site).
- `WreckPlacementService.placeNear` : cherche un fond marin exploitable dans un rayon de 64 blocs
  (biome tagué océan, profondeur d'eau ≥ 4 blocs), puis construit la coque : planches partiellement
  manquantes (remplacées par du gravier), murs latéraux à hauteur irrégulière avec quelques rondins
  faisant office de membrures cassées, une écoutille (trappe), un mât brisé couché à côté de l'épave,
  et un tonneau contenant la table de butin.
- `WreckPlacementEvents` : même cadence que `RegionalPortEvents` (vérification par joueur toutes les
  900 ticks), environ 1 chance sur 5 par région de 512 blocs — volontairement plus rare que les ports
  (1/3), cohérent avec « épaves rares ».
- `data/tidebound/loot_table/chests/shipwreck_plateau.json` : premier pool d'objets vanilla
  d'ambiance (papier, carte, pépite de fer, tesson de prismarine, algue), deuxième pool de pièces de
  maintenance Tidebound déjà existantes (`hull_plate`, `engine_parts`, `hold_fittings`, `repair_kit`,
  `caulking_kit`, `mechanical_oil`) — donne enfin une seconde voie d'acquisition à ces objets, jusque
  là uniquement obtenus via des recettes.

## Ce qui n'est pas dans cette tranche

- pas de vraie grande structure de navire échoué (ambition initiale, nécessiterait soit du `.nbt`
  soit un générateur bien plus élaboré) ;
- pas de journal de bord narratif (juste du `minecraft:paper` brut, pas de livre écrit avec un
  contenu spécifique) ;
- pas de carte au trésor menant à un point d'intérêt (le `minecraft:map` du butin est une carte vierge
  vanilla, pas une carte pointant vers quelque chose) ;
- pas de contrat régional dédié à ce biome pour l'instant.

## Vérification

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK` ;
- démarrage réel d'un serveur dédié sur un monde neuf : `Done`, aucune erreur, aucun problème de
  validation de table de butin au chargement du datapack.

## Non vérifié

Comme `HarborPlacementService.buildHut` avant lui, le code de construction de la coque n'a jamais
été exercé par un vrai joueur dans cette session : la commande de test existante
(`/tidebound world port-place`) exige un joueur connecté, ce qu'aucun outil disponible ici ne permet
de simuler. Compilation et chargement de datapack propres, mais rendu réel de la coque, exactitude du
placement sur le fond marin et apparence du tonneau/butin restent à confirmer par l'utilisateur.
