# TB-CORE-006 — Interface de cale dédiée

## Résultat

Le navire Tidebound (`TideboundVesselEntity`) n'expose plus que les lignes de cale réellement
débloquées : 1 ligne (9 cases) au niveau 1, 2 lignes (18 cases) au niveau 2, 3 lignes (27 cases) à
partir du niveau 3. Le conteneur physique conserve toujours ses 27 cases pour ne jamais perdre
d'objets lors d'une future amélioration ; seules les cases débloquées sont désormais affichées et
manipulables dans l'interface.

## Implémentation technique

`ChestMenu` (vanilla) accepte déjà un conteneur plus grand que le nombre de lignes qu'il affiche
(`checkContainerSize` vérifie seulement une taille minimale) : `TideboundVesselEntity#createMenu`
est surchargé pour construire un `ChestMenu` dimensionné sur `VesselHoldPolicy.usableSlots(holdTier)
/ 9` lignes, en réutilisant l'entité elle-même comme conteneur. Le niveau de cale vient de
`visualProfile().holdTier()`, déjà synchronisé côté client via `SynchedEntityData`, donc client et
serveur construisent toujours le même nombre de lignes sans donnée supplémentaire à transmettre.
Aucun nouveau menu, écran ou registre n'a été nécessaire — entièrement vanilla.

## Portée

Ce correctif couvre le navire Tidebound complet (construit au chantier). La **Barque de fortune**
(barque vanilla enregistrée avant le chantier) reste couverte par le mécanisme d'éviction existant
(`VesselDeploymentService.enforceHoldCapacity`), inchangé : elle ne peut de toute façon recevoir
aucune amélioration de cale tant qu'elle n'est pas refondue en navire Tidebound
(`docs/DECISIONS.md` §Navire).

## Validation effectuée localement

- `./gradlew build` : compilation et build réussis contre les vraies classes NeoForge 21.1.249 /
  Minecraft 1.21.1, `DomainSelfTest: OK` ;
- `python3 tools/validate_content.py` : `OK`.

## Validation requise

- lancement client/serveur réel : ouvrir le navire à chaque niveau de cale (1, 2, 3) et confirmer
  que seules les cases débloquées apparaissent, sans erreur de synchronisation client/serveur ;
- vérifier qu'une amélioration de cale en cours de partie agrandit bien le menu à la prochaine
  ouverture, sans perte des objets déjà stockés dans les cases au-delà de l'ancienne limite.
