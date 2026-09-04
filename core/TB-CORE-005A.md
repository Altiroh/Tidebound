# TB-CORE-005A — Autonomie de départ et repérage

## Objectif

Permettre une partie viable lorsque l'île de départ procédurale ne contient aucun loueur : le joueur
fabrique une barque vanilla, atteint un port, l'enregistre et peut ensuite la retrouver sans commande.

## Boucle validée dans le code

`bois → barque vanilla → navigation → premier port → enregistrement → Compas de sillage`

L'intendant cherche d'abord la barque montée par le joueur, puis la barque libre la plus proche dans
un rayon de huit blocs. La barque conserve son entité et son type de bois ; elle reçoit l'identité,
le nom et la protection du navire Tidebound.

## Compas de sillage

- objet enregistré sous `tidebound:wake_compass` ;
- remis gratuitement lors d'une réclamation ou d'un enregistrement ;
- remplacement gratuit auprès de l'intendant si le joueur l'a perdu ;
- recette de secours disponible ;
- clic droit : direction cardinale, distance approximative et coordonnées X/Z ;
- autre dimension : indique la dimension du sillage ;
- navire disparu ou détruit : conserve et annonce la dernière position connue ;
- aucune position : explique qu'un navire doit être enregistré ou mis à l'eau.

Le modèle utilise provisoirement la texture vanilla du compas. Une aiguille animée propre au navire
n'est pas encore implémentée ; le retour direction/distance est autoritaire côté serveur.

## Commandes

Joueur, uniquement au port :

- `/tidebound vessel register [name]` ;
- `/tidebound vessel compass`.

La commande `/tidebound vessel locate` est maintenant réservée aux administrateurs et au diagnostic.

## Persistance

`VesselDeployment` conserve désormais la position bloc exacte et un état parmi :

- `docked` : aucune position connue ;
- `deployed` : entité chargée ou retrouvable ;
- `missing` : entité non retrouvée, dernière trace conservée ;
- `destroyed` : destruction observée côté serveur, dernière trace conservée.

Le codec accepte les données 0.4 antérieures grâce à l'état transitoire `legacy`.

## Validation effectuée

- `DomainSelfTest: OK`, incluant positions, états et huit directions ;
- six définitions Tidebound valides ;
- tous les JSON et `pack.mcmeta` passent `jq` ;
- `git diff --check` sans erreur ;
- analyse syntaxique Java sans erreur de structure.

## Validation encore requise

- compilation NeoForge complète avec JDK 21 et Gradle ;
- lancement client/serveur 1.21.1 ;
- test réel de destruction et de déchargement de chunk ;
- test multijoueur de l'enregistrement de deux barques proches.

## Suite

`TB-CORE-005B` : coûts en Tides et matériaux, réparations, prérequis de métiers et capacité de cale.
