# TB-CORE-009 — Aiguilles de compas animées (infrastructure)

## Statut : moitié serveur/client terminée, art de l'aiguille en attente

## Ce qui est fait

Le Compas de sillage et le Compas des Havres ne pointaient qu'au clic droit (message texte
direction/distance), sans retour visuel continu. Ajout de l'infrastructure nécessaire à une vraie
aiguille animée, à la manière du compas vanilla :

- `TideboundDataComponents.COMPASS_TARGET` (nouveau `DataComponentType<GlobalPos>`, persistant et
  synchronisé réseau) : la dernière position mondiale connue vers laquelle l'aiguille doit pointer ;
- `WakeCompassService.resolveTarget` / `HavenCompassService.resolveTarget` : factorisent le calcul de
  cible déjà utilisé par `read()`, réutilisables sans envoyer de message ;
- `WakeCompassItem`/`HavenCompassItem#inventoryTick` : rafraîchissent `COMPASS_TARGET` sur la pile
  toutes les 20 ticks tant que l'objet est en possession d'un joueur serveur, sans attendre un clic ;
- `TideboundClientModEvents#onClientSetup` : enregistre une propriété d'objet cliente
  `tidebound:angle` (`ItemProperties.register`, même mécanisme que le compas vanilla) qui calcule le
  cap réel vers `COMPASS_TARGET` relatif à l'orientation du porteur
  (`atan2` + normalisation, aucune dépendance à l'API interne du compas vanilla — recalculée et
  vérifiée indépendamment), avec une lente rotation de recherche quand aucune cible n'est connue ou
  que la cible est dans une autre dimension.

Vérifié : compilation réelle contre les classes Minecraft recompilées localement (API
`ItemProperties`, `DataComponentType.Builder`, `GlobalPos` confirmées par `javap`, pas supposées),
`./gradlew build`, `validate_content.py`, démarrage serveur réel — tout `OK`. Sans effet visuel pour
l'instant : aucun modèle d'objet ne référence encore la propriété `tidebound:angle`.

## Ce qui bloque : l'art de l'aiguille

Les deux icônes actuelles (`textures/item/wake_compass.png`, `haven_compass.png`, 32×32) sont peintes
à la main avec un dégradé continu — quasiment aucune couleur plate répétée. Une segmentation automatique
par couleur pour isoler l'aiguille du boîtier a été jugée trop risquée sur un asset que le projet
traite comme canonique (`docs/assets/ASSET_CATALOG.md`) : le résultat n'est pas vérifiable visuellement
dans cette session et pourrait abîmer l'art existant.

Décision prise avec l'utilisateur : il fournit l'aiguille séparément plutôt qu'une rotation de l'icône
entière (qui ferait aussi tourner l'anneau/la chaîne du pendentif) ou qu'une segmentation automatique
risquée.

### Spécification exacte attendue

Pour chaque boussole (Compas de sillage et Compas des Havres), deux fichiers PNG 32×32, même canevas
que l'icône actuelle, fond transparent :

1. **`<nom>_case.png`** — le boîtier complet (anneau doré, verre, chaîne) identique à l'icône actuelle
   mais **sans l'aiguille** : la zone où elle se trouvait doit rester transparente ou remplie par le
   fond du cadran, pas par du vide brut qui laisserait un trou.
2. **`<nom>_needle.png`** — l'aiguille seule, tout le reste transparent, positionnée/centrée comme sur
   l'icône actuelle mais **orientée vers le haut du canevas (12h)** pour la pose de référence — c'est
   cette orientation qui correspondra à « la cible est droit devant ». Je fais tourner cette image par
   pas de 11,25° (32 crans, comme le compas vanilla) pour produire les 32 textures de trame ; le
   boîtier ne bouge jamais.

Dès réception, j'assemble les 32 trames par boussole (rotation programmatique de la seule aiguille,
recomposée sur le boîtier fixe), je génère les modèles `overrides` correspondants
(`predicate: {"tidebound:angle": k/32}`), et je référence la nouvelle propriété dans
`wake_compass.json`/`haven_compass.json`. Aucune modification de code supplémentaire nécessaire à ce
moment-là.

## Fichiers touchés (cette étape)

- `core/src/main/java/dev/tidebound/core/registry/TideboundDataComponents.java` ;
- `core/src/main/java/dev/tidebound/core/item/WakeCompassItem.java`,
  `HavenCompassItem.java` ;
- `core/src/main/java/dev/tidebound/core/service/WakeCompassService.java`,
  `HavenCompassService.java` ;
- `core/src/main/java/dev/tidebound/core/client/TideboundClientModEvents.java`.
