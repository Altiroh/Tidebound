# TB-QUEST-002 — Durcir le livre FTB Quests

## Contexte

Retour utilisateur : « blinder les quêtes ». Précisé en quatre axes (tous retenus) : anti-triche/anti-bug,
traduction complète, plus de contenu, prérequis plus stricts. `TB-QUEST-001` notait déjà explicitement
six tâches à validation manuelle comme un choix provisoire, à remplacer « lorsque Core exposera les
signaux port, navire, contrat et amélioration » — c'est exactement ce que ce ticket fait.

## 1. Anti-triche / anti-bug : fini les cases à cocher soi-même

Six des neuf tâches d'origine étaient des `type: "checkmark"` — le joueur validait lui-même l'étape
sans que le serveur ne vérifie quoi que ce soit. Un joueur pouvait cocher « J'ai enregistré mon navire »
sans navire, ou « J'ai livré un contrat » sans jamais approcher d'intendant.

Nouvelle mécanique, sans dépendance Java vers FTB Quests (conforme à la règle du projet : FTB Quests
reste une façade) : un déclencheur d'avancement Minecraft générique,
`dev.tidebound.core.advancement.TideboundSignalTrigger` (`tidebound:quest_signal`), enregistré via
`TideboundCriteriaTriggers`. Cinq avancements cachés (sans `display`, sans `parent` — jamais visibles
dans l'écran vanilla des progrès) sont posés dans `data/tidebound/advancement/signal/*.json`, chacun
avec un seul critère qui compare le signal reçu à une chaîne fixe. Les tâches FTB Quests concernées
passent de `checkmark` à `advancement`, pointant vers ces fichiers — la coche ne s'active que lorsque
le signal est réellement déclenché côté serveur, au point de succès effectif de chaque action :

| Signal | Déclenché dans | Point exact |
|---|---|---|
| `vessel_registered` | `VesselService.unlock` | juste avant le retour, sur la même transition idempotente `unlocked() == false → true` qui couvre à la fois l'enregistrement d'une barque existante et la réclamation d'un navire neuf |
| `port_visited` | `HarborBoardService.open` | à chaque ouverture d'un écran de PNJ portuaire, quel que soit le rôle |
| `contract_board_opened` | `HarborBoardService.open` | uniquement quand le rôle est `INTENDANT` |
| `contract_delivered` | `ContractService.complete` | juste après `RewardService.apply`, sur le chemin de succès réel |
| `vessel_upgraded` | `VesselMaintenanceService.purchaseUpgrade` | après paiement débité, matériaux consommés et amélioration appliquée — jamais sur le chemin de remboursement |

La tâche « J'ai obtenu une barque » (obtenir n'importe quelle barque vanilla) reste une case manuelle :
aucun point d'ancrage serveur fiable n'existe pour cet événement précis, et l'enjeu de triche y est nul
(10 Tides, action triviale de toute façon nécessaire pour la suite).

## 2. Traduction complète

Vérification : les 9 quêtes/tâches/récompenses d'origine étaient déjà intégralement traduites en
français et en anglais (`lang/fr_fr.snbt`, `lang/en_us.snbt`) — pas de dette trouvée ici. Les 3 nouvelles
quêtes du chapitre `Les eaux côtières` sont ajoutées dans les deux langues dès leur création, et
`validate_content.py` vérifie désormais que les deux fichiers de langue contiennent exactement les
mêmes 12 identifiants de quête/tâche/récompense (échec bloquant si l'un des deux prend du retard).

## 3. Plus de contenu : chapitre « Les eaux côtières »

Troisième chapitre du Voyage (le prochain de la liste canonique à sept chapitres,
`docs/design/Tidebound_Lore.md`), volontairement borné à des signaux déjà fiables :

1. **Une pêche plus généreuse** — rapporter un saumon (tâche `item`, même mécanique que la morue de
   `Naufragé`) ;
2. **Le Compas des Havres** — posséder l'objet (tâche `item`, recette déjà existante) ;
3. **Lire la mer** — atteindre le niveau 3 en Navigation, nouveau signal `navigation_level_3` déclenché
   dans `TideboundGameplayEvents.notifySkillProgress` au moment réel du passage de palier.

## 4. Prérequis plus stricts

`TB-QUEST-001` avait délibérément choisi zéro dépendance entre quêtes (`progression_mode: "flexible"`,
toujours inchangé). Ce ticket ajoute des `dependencies` explicites par quête, sans toucher au mode
global :

- `Naufragé` reste linéaire (établi → barque → prise → port), déjà implicite par la position mais pas
  imposé jusqu'ici ;
- `Premier port` s'enchaîne (enregistrement → compas → tableau → livraison → amélioration), et sa
  première quête dépend désormais de la dernière quête de `Naufragé` (trouver un port avant de
  s'enregistrer) ;
- `Les eaux côtières` démarre après « Une lumière au loin » et s'enchaîne en interne.

## Fichiers touchés

- `core/src/main/java/dev/tidebound/core/advancement/TideboundSignalTrigger.java` (nouveau) ;
- `core/src/main/java/dev/tidebound/core/advancement/TideboundCriteriaTriggers.java` (nouveau) ;
- `core/src/main/resources/data/tidebound/advancement/signal/*.json` (6 nouveaux fichiers) ;
- `core/src/main/java/dev/tidebound/core/service/VesselService.java`,
  `HarborBoardService.java`, `ContractService.java`, `VesselMaintenanceService.java`,
  `event/TideboundGameplayEvents.java` (déclenchement des signaux) ;
- `core/src/main/java/dev/tidebound/core/TideboundCore.java` (enregistrement) ;
- `modpack/overrides/config/ftbquests/quests/chapters/naufrage.snbt`,
  `premier_port.snbt` (checkmark → advancement, dépendances) ;
- `modpack/overrides/config/ftbquests/quests/chapters/les_eaux_cotieres.snbt` (nouveau chapitre) ;
- `modpack/overrides/config/ftbquests/quests/lang/{fr_fr,en_us}.snbt` (nouvelles traductions) ;
- `core/tools/validate_content.py` (compteurs mis à jour : 3 chapitres, 12 quêtes, 6 tâches
  `advancement`, 5 `item`, 1 `checkmark` restant, 40 identifiants d'objet).

## Vérification effectuée

- `./gradlew build` : compilation et `DomainSelfTest` réussis (l'API réelle de
  `SimpleCriterionTrigger`/`EntityPredicate.ADVANCEMENT_CODEC`/`Registries.TRIGGER_TYPE` a été confirmée
  par `javap` sur les classes Minecraft recompilées localement avant d'écrire le code, pas devinée) ;
- `python3 tools/validate_content.py` : `OK` avec les nouveaux compteurs ;
- démarrage réel d'un serveur dédié sur un monde neuf : `Loaded 1405 advancements` (1404 vanilla + 1),
  aucune erreur, `Done`.

## Non vérifié

- FTB Quests n'est pas présent dans l'environnement de développement `runServer` (module Core testé
  seul) : la syntaxe SNBT est validée par `validate_content.py`, mais l'affichage réel du livre, le
  déverrouillage visuel des dépendances et le déclenchement effectif des tâches `advancement` en jeu
  restent à confirmer sur le Devpack complet ;
- aucune vérification visuelle possible dans cette session, comme pour le reste de `TB-CORE-008`.
