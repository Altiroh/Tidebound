# Marais des Lanternes — spécification de région

Statut : **conception**, premier biome MVP entièrement nouveau désigné dans
`docs/design/Tidebound_Roadmap_Biomes.md`. Aucune ligne de code de ce document n'est encore
implémentée — à valider avant de commencer `TB-WORLD-003`.

## Identité

Eaux saumâtres, roseaux, passerelles, lueurs flottantes, brume basse, chenaux étroits. Le premier
mystère du jeu, sans basculer dans l'horreur ouverte : ambiance forte, danger nul ou très faible.

## Réalisation technique du biome

Vanilla possède déjà exactement l'identité recherchée : **`minecraft:mangrove_swamp`** (racines de
palétuvier = passerelles naturelles, boue = eau saumâtre, luxuriance humide). Même technique que
`violet_shallows`/`abyss_ocean` (`TB-WORLD-002`) : cloner `mangrove_swamp.json`, ne changer que
`water_color`/`water_fog_color` vers une teinte ambrée/verdâtre évoquant une lueur de lanterne, sans
toucher au terrain ni à la végétation.

Placement dans la table de biomes (`data/minecraft/worldgen/world_preset/normal.json`,
`data/tidebound/worldgen/world_preset/archipelago.json` — **les deux fichiers, comme toujours**) :
carver une bande étroite dans la cellule `beach` existante (`continentalness [0.05, 0.16]`,
actuellement `humidity [-0.15, 1.0]` uniforme) en la découpant à l'humidité la plus haute
(`[0.7, 1.0]`) pour `tidebound:lantern_marsh`, et en resserrant l'entrée `beach` existante à
`[-0.15, 0.7]` — exactement la méthode déjà éprouvée pour les deux biomes à eau teintée. Résultat :
des poches de marais rares en bordure de plage humide, jamais un remplacement massif des plages.

## Mécaniques de gameplay

### Navigation lente et précise (réalisable maintenant)

Aucun nouveau système requis : les chenaux étroits sont une propriété du terrain généré par le biome
vanilla mangrove lui-même (racines, hauts-fonds). Rien à coder.

### Pêche de nuit (réalisable maintenant, extension mineure)

`CatchData.originBiomeId()` existe déjà et est capturé à chaque prise, mais n'influence aujourd'hui
que l'affichage (tooltip) — jamais la génération. Pour ce biome, ajouter dans `CatchGenerator` (ou un
point d'entrée équivalent) une majoration du taux d'anomalie/qualité quand
`originBiomeId == "tidebound:lantern_marsh"` **et** `!level.isDay()` — même idiome que
`VesselModuleEvents`/`HullIntegrityEvents`, qui vérifient déjà `!serverLevel.isDay()`. Premier cas
réel où le biome d'origine affecte la génération, pas seulement l'étiquette.

### Accès conditionné par les marées (nécessite une décision produit)

Le lore parle de « marées » au sens propre (niveau d'eau qui monte/descend). Construire une vraie
simulation de marée dans Minecraft (blocs d'eau qui changent dynamiquement) est un système lourd et
fragile, jamais entamé dans ce projet. Deux options, à trancher avant d'implémenter :

1. **Marée simplifiée** (recommandé) : pas de changement de niveau d'eau réel — le chenal reste
   praticable à tout moment, mais un cycle périodique serveur (comme `RegionalPortEvents`, basé sur
   le temps de jeu) déclenche des fenêtres « marée haute »/« marée basse » qui changent seulement la
   *récompense* (ex. bonus de pêche nocturne actif uniquement à marée basse), sans toucher au terrain.
2. **Marée réelle** : swap de blocs dynamique dans une zone bornée (eau ↔ boue exposée) au fil d'un
   minuteur. Beaucoup plus coûteux à développer et à tester, risque de désync visuel client/serveur.

Recommandation : commencer par l'option 1, documenter l'option 2 comme extension possible si le
retour utilisateur la réclame après avoir testé la version simplifiée.

### Lucioles / lueurs flottantes (décoration, réalisable avec des assets vanilla)

Suggestion vanilla-friendly : particules de vase luisante (`minecraft:glow_lichen`) sur les racines
de palétuvier, blocs de lanterne (`minecraft:lantern`/`sea_lantern`) accrochés sous les passerelles
dans les rares îlots habités du biome. Aucun nouvel asset requis, cohérent avec la règle « Comment
Minecraft vanilla ferait-il cela ? » du lore.

## Poissons

Aucune nouvelle espèce : le mod reste limité aux quatre profils vanilla tant que
l'intégration de poissons modés n'est pas traitée (`HANDOFF.md`, point « Catalogue par datapack »).
Depuis `TB-CORE-010`, ajouter une variante régionale est un fichier JSON, pas du code — mais un même
item vanilla (`minecraft:cod`, etc.) ne peut porter qu'un seul profil de poids/valeur global,
partagé par tous les biomes. Deux façons de donner au Marais sa propre identité de pêche sans
dupliquer les profils :

- pondérer différemment les espèces *tirées* dans ce biome (ex. `pufferfish` et `tropical_fish` plus
  fréquents la nuit) plutôt que changer leurs statistiques — nécessite de faire dépendre le tirage
  d'espèce du biome d'origine, actuellement non paramétré (à vérifier dans le service qui choisit
  l'espèce avant d'appeler `CatchGenerator`) ;
- réserver la vraie différenciation de valeur/poids aux futures espèces propres à un mod (hors scope
  MVP).

## Structures et lieux habités

D'après `Tidebound_Lore.md` §5 (Familles d'îles), une île de marais peut être une île habitée
(cabane, petit quai) ou une île étrange, jamais systématiquement un port complet. Proposition MVP :

- un petit ponton de bois isolé (aucun Intendant), 1-2 PNJ optionnels au maximum ;
- pas de génération de port complet dans ce biome pour l'instant — laisser `HarborPlacementService`
  continuer à cibler les rivages classiques, éviter de complexifier le placement de port pour ce
  premier biome.

## Contrat régional proposé

Un contrat répétable simple utilisant le système `ContractDefinition` déjà existant : livrer un
poisson pêché la nuit dans le Marais (`requirement` sur un item vanilla + condition narrative dans le
texte, la vérification technique réelle restant l'objet livré comme pour tous les contrats actuels).
Pas de nouveau mécanisme de contrat nécessaire.

## Ce qui est réellement nouveau à construire (résumé)

1. Biome `tidebound:lantern_marsh` (clone teinté de `mangrove_swamp`) + entrées dans les deux presets.
2. Bonus qualité/anomalie nocturne conditionné par `originBiomeId`, premier branchement réel du champ
   d'origine sur la génération plutôt que sur l'affichage seul.
3. Décision : marée simplifiée (recommandée) vs marée réelle — bloquant avant tout code de marée.
4. Éventuel biais de tirage d'espèce par biome, si on veut que le Marais donne plus souvent des
   espèces précises la nuit plutôt que de changer leurs statistiques.

Rien dans cette liste ne nécessite de nouvel art : uniquement des blocs et items vanilla.
