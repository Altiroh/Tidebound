# TB-CORE-005C — Modules v1

## Résultat

Les quatre emplacements de module déjà vendus par le chantier naval (`TB-CORE-005B`,
`PlayerVessel.moduleSlots`) ont désormais un effet réel en jeu. L'activation est fixe et cumulative,
sans nouvelle interface d'équipement — acheter le slot N active le module N (`VesselModule`) :

1. **Projecteur** — repousse les spawns hostiles la nuit autour du navire.
2. **Sonar** — signale les eaux prometteuses et les dangers proches ; à partir du niveau 5 de
   Navigation, détecte aussi les épaves et les objets flottants cachés à plus longue portée.
3. **Treuil** — attire les objets flottants vers le navire.
4. **Filet** — chance de prise supplémentaire à la pêche active, et pêche passive automatique tant que
   le navire est immobile et son propriétaire à proximité.

## Garanties anti-automatisation

Aucun module n'agit sur un navire sans surveillance :

- le Projecteur ne réagit qu'à une vraie tentative de spawn (événement, pas de scan permanent) ;
- Sonar, Treuil et Filet exigent que le propriétaire soit **en ligne et à moins de 12 blocs** de son
  propre navire (même filtre que `VesselDeploymentService.enforceNearbyHoldCapacity`) ;
- la pêche passive du Filet exige en plus que le navire soit quasiment immobile, et verse une XP de
  pêche réduite (1 au lieu de 5 par prise active) pour ne pas contourner l'économie de la pêche
  (`docs/DECISIONS.md` §Économie).

## Implémentation technique

- `dev.tidebound.core.vessel.VesselModule` : ordre d'activation fixe par nombre de slots.
- `dev.tidebound.core.event.VesselModuleEvents` : toute la logique de jeu, enregistrée dans
  `TideboundCore`. Le Projecteur écoute `MobSpawnEvent.PositionCheck` (API vérifiée dans les sources
  NeoForge 21.1.249 locales) ; les trois autres tournent sur `PlayerTickEvent.Post`, avec un
  étalement des joueurs sur l'intervalle (même idiome que
  `TideboundGameplayEvents.NAVIGATION_INTERVAL_TICKS`) pour éviter tout pic de charge.
- `TideboundGameplayEvents.onItemFished` appelle `VesselModuleEvents.isModuleActive(player,
  VesselModule.NET)` pour la multi-prise.
- Aucune nouvelle donnée persistante : `moduleSlots` est déjà synchronisé et sauvegardé directement sur
  `TideboundVesselEntity`, lisible sans dépendre du propriétaire en ligne.
- Petite touche d'UI : la ligne « modules » de la capitainerie (`HarborScreen`) affiche désormais une
  info-bulle au survol listant les quatre modules et leur état actif/inactif.

## Validation effectuée localement

- `./gradlew build` : compilation et build réussis contre les vraies classes NeoForge 21.1.249 /
  Minecraft 1.21.1 (JDK 21 local), `DomainSelfTest: OK`. Les signatures de `MobSpawnEvent.PositionCheck`,
  `ServerLevel#findNearestMapStructure`, `StructureTags.SHIPWRECK` et `GuiGraphics#renderComponentTooltip`
  ont été vérifiées dans les sources décompilées locales, pas supposées.
- `python3 tools/validate_content.py` : `OK`.

## Validation requise

- lancement client/serveur réel : acheter les slots 1 à 4 un par un et vérifier chaque module ;
- confirmer qu'aucun mob ne spawn près d'un navire à Projecteur actif la nuit, mais que les spawns
  restent normaux loin du navire ou de jour ;
- confirmer que le Filet passif ne se déclenche pas si le propriétaire est hors ligne ou trop loin ;
- équilibrage : ajuster les rayons, intervalles et la chance de multi-prise après un test de partie
  réelle (valeurs actuelles non testées en jeu).

## Suite

`TB-QA-001` : partie neuve de 30 minutes sur plusieurs seeds.
