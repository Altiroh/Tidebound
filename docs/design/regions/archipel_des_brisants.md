# Archipel des Brisants — spécification de région

Statut : **conception + implémentation minimale**, quatrième et dernier biome MVP de
`docs/design/Tidebound_Roadmap_Biomes.md`.

## Identité

Enchaînement d'îlots rocheux, criques, colonies d'oiseaux, petits phares et récifs. Exploration
insulaire, navigation entre obstacles, découverte de ports secondaires, commerce léger. Sensation
d'aventure à courte distance et de cabotage.

## Pourquoi si peu de code, contrairement aux trois biomes précédents

Le document de cadrage le dit lui-même : cette région « recoupe le système de ports régionaux déjà
en place ». Vérification faite contre le code réel plutôt que supposée :

- **« petits phares »** → `PortArchetype.LIGHTHOUSE_OUTPOST` existe déjà (poids 16/100 dans le tirage
  d'archétype de `PortPlan`), avec le PNJ Gardien de phare déjà fonctionnel ;
- **« ports secondaires »** → `RegionalPortEvents` place déjà un port dans ~1/3 des régions de
  512 blocs explorées, indépendamment du port de départ ;
- **« commerce léger »** → `PortArchetype.FISHING_HAMLET`/`MARKET_HARBOR` existent déjà ;
- **« enchaînement d'îlots »** → propriété déjà intrinsèque du preset `archipelago`
  (`TB-WORLD-001`) : le monde entier est déjà composé d'une multitude de petites îles.

Il ne manquait donc pas un système, mais un seul élément concret et absent : les **récifs**. Aucun
port, aucune île n'affichait de relief rocheux émergé dans l'eau peu profonde.

## Implémentation

`HarborPlacementService.buildReef` (nouveau, appelé uniquement pour l'archétype
`LIGHTHOUSE_OUTPOST`) : 6 à 9 rochers (pierre, pierre moussue, cobblestone, andésite — mélange
délibéré pour un aspect naturel, pas un bloc unique) dispersés dans les hauts-fonds de part et
d'autre du quai, chacun posé sur le vrai fond marin local (pas une hauteur fixe, pour éviter des
rochers flottants si la profondeur varie), haut de 1 à 3 blocs. Déterministe par site
(`RandomSource.create(plan.siteId())`), comme les épaves de `TB-WORLD-005`.

## Ce qui n'est pas dans cette tranche

- pas de biome dédié : contrairement au Marais et aux Falaises, « un enchaînement d'îlots » n'est pas
  une identité de couleur/relief carvable dans la table `multi_noise` de la même façon — c'est déjà
  la nature du monde entier ;
- pas de colonies d'oiseaux (aucune entité ni mécanique de faune de ce type n'existe dans le projet) ;
- pas de biais pour que les ports `LIGHTHOUSE_OUTPOST` apparaissent préférentiellement dans des zones
  à forte densité d'îlots (aucune mesure de densité d'îlots n'existe, mesurer et exploiter cette
  densité serait un vrai nouveau système, hors scope de cette tranche) ;
- pas de récif pour les autres archétypes de port : réservé au phare, cohérent avec la fantasy.

## Bilan MVP

Avec cette tranche, les cinq biomes MVP de `docs/design/Tidebound_Roadmap_Biomes.md` sont tous
traités (à des degrés d'ambition différents, assumés et documentés) : Côte paisible (déjà couverte
par l'existant), Marais des Lanternes, Falaises Abyssales, Plateau des Épaves, Archipel des
Brisants.

## Vérification

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK` ;
- démarrage réel d'un serveur dédié sur un monde neuf : `Done`, aucune erreur.

## Non vérifié

Comme `buildHut` et `WreckPlacementService.buildWreck` avant lui, `buildReef` n'a jamais été exercé
par un vrai joueur dans cette session (nécessite un port `LIGHTHOUSE_OUTPOST` réellement matérialisé
en présence d'un joueur). Compilation et démarrage serveur propres, mais position, densité et
apparence réelle des rochers restent à confirmer par l'utilisateur.
