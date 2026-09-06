# Tidebound Core — 0.19.0-alpha

Socle serveur du mod Tidebound pour **Minecraft Java 1.21.1 / NeoForge**.

## Ce qui est implémenté

- portefeuille personnel de **Tides**, persistant et conservé après la mort ;
- fiche `PlayerVessel` persistante : identité, nom, coque, moteur, cale et emplacements de modules ;
- déblocage idempotent du bateau, prêt à être appelé par un PNJ ou un palier de succès ;
- façade serveur `TideboundApi` pour raccorder plus tard contrats, PNJ, KubeJS et récompenses ;
- registre persistant de reçus empêchant une récompense unique d'être versée deux fois ;
- paliers uniques et contrats répétables chargés depuis des fichiers JSON de datapack ;
- consommation des denrées demandées, délais par contrat et récompenses en Tides, objets et XP ;
- quatre métiers à dix niveaux : pêche, navigation, commerce et récupération ;
- déclencheurs automatiques pour la pêche, la navigation océanique et la première livraison ;
- contrats pouvant exiger un niveau de métier ;
- intendant ouvrant un véritable menu graphique et synchronisé ;
- carnet de bord accessible par une icône dans l'inventaire ;
- entité de navire Tidebound dédiée, avec coque voxel et quatre places, liée à son propriétaire ;
- réclamation, mise à l'eau, localisation et renommage auprès de la capitainerie ;
- position du navire persistante entre les sessions et protection contre les autres joueurs ;
- effets physiques légers des améliorations de coque et de moteur ;
- transformation au port d'une barque vanilla en navire Tidebound, sans perte d'identité ni de cale ;
- Compas de sillage remis gratuitement au premier navire et reproductible ;
- lecture par clic droit de la direction, de la distance et de la dernière position connue ;
- suivi des états déployé, disparu, détruit et sans position ;
- ponts prêts à copier pour KubeJS et FTB Quests ;
- livre FTB Quests fourni par le Devpack avec les chapitres `Naufragé` et `Premier port` ;
- commandes de diagnostic et d'administration ;
- tests autonomes du domaine économique, du bateau et de la progression.
- achats au port avec coûts en Tides, matériaux et niveaux de métier ;
- réparation du navire au quai et remboursement sur échec ;
- cale progressive de 9, 18 puis 27 emplacements utilisables.
- Data Component persistant sur les quatre poissons vanilla obtenus à la pêche ;
- poids, qualité, fraîcheur dynamique, biome d'origine, anomalie rare et valeur estimée ;
- affichage de la prise dans la barre d'action et dans son tooltip ;
- première capture limitée aux vrais poissons, sans récompense sur les déchets de pêche.
- archipel appliqué par défaut aux nouvelles sauvegardes, avec îles boisées séparées par l'océan ;
- diagnostic administrateur de la viabilité du spawn et tirage stable du futur port initial ;
- prototype de quai portuaire matérialisé selon l'archétype et les services du plan régional ;
- apparence synchronisée de la coque, du moteur, de la cale et des supports de modules.
- six composants Tidebound illustrés, avec recettes et utilisation par le chantier naval ;
- enchantement `tidebound:fast_smelting` sur les outils de minage vanilla ;
- modules v1 (Projecteur, Sonar, Treuil, Filet) branchés aux emplacements déjà vendus par le chantier.

Le navire physique possède désormais son propre type d'entité. Il réutilise la physique éprouvée du bateau-coffre,
mais son rendu, son équipage et ses niveaux visuels appartiennent à Tidebound. Le livre `Le Voyage` guide le joueur
sans verrouiller le sandbox : ses objectifs actuels sont visibles et sans dépendances obligatoires.

## Versions verrouillées

- Minecraft `1.21.1`
- NeoForge `21.1.249`
- ModDevGradle `2.0.146`
- Java `21`
- Parchment `2024.11.17`

## Commandes

Commandes joueur :

- `/tidebound tide balance`
- `/tidebound catch inspect` — inspecte la prise tenue en main principale
- `/tidebound vessel inspect`
- `/tidebound vessel claim [name]` — près d'un intendant
- `/tidebound vessel register [name]` — enregistre la barque vanilla montée ou la plus proche
- `/tidebound vessel refit` — construit au port le navire Tidebound à partir de la Barque de fortune
- `/tidebound vessel deploy` — près d'un intendant et d'une zone d'eau
- `/tidebound vessel compass` — remplace gratuitement un Compas de sillage perdu au port
- `/tidebound vessel rename <name>` — près d'un intendant
- `/tidebound vessel purchase <hull|motor|hold|module>` — achat au port, navire présent
- `/tidebound vessel repair` — réparation au port, navire présent
- `/tidebound progression inspect`
- `/tidebound skills`
- `/tidebound progression content summary`
- `/tidebound progression content milestones`
- `/tidebound progression content contracts`
- `/tidebound progression contract status <id>`
- `/tidebound contracts` — uniquement près d'un intendant de port
- `/tidebound contracts deliver <id>` — uniquement près d'un intendant de port

Commandes administrateur, niveau de permission 2 :

- `/tidebound tide balance <player>`
- `/tidebound tide grant <player> <amount>`
- `/tidebound tide spend <player> <amount>`
- `/tidebound vessel inspect <player>`
- `/tidebound vessel locate`
- `/tidebound vessel unlock <player> [name]`
- `/tidebound vessel upgrade <player> <hull|motor|hold|module>`
- `/tidebound progression inspect <player>`
- `/tidebound progression reward-once <player> <receipt> <tides>`
- `/tidebound progression skill grant <player> <skill> <xp>`
- `/tidebound progression milestone complete <player> <id>`
- `/tidebound progression contract complete <player> <id>`
- `/tidebound skills <player>`
- `/tidebound harbor register <villager>`
- `/tidebound harbor unregister <villager>`
- `/tidebound world diagnose [radius]` — analyse le spawn sur 64 à 256 blocs

La limite d'une transaction de diagnostic est de 1 000 000 Tides. Le portefeuille utilise un `long`
et refuse les valeurs négatives ou supérieures à 1 000 000 000 000.

## Compiler le mod

Le projet suit le MDK ModDevGradle officiel. Installer un JDK 21 ; le Gradle Wrapper est inclus.
Depuis ce dossier, sous Windows :

```bash
.\gradlew.bat build
```

Sous Linux ou macOS :

```bash
./gradlew build
```

Le JAR est produit dans `build/libs/tidebound-0.17.0-alpha.jar`. Pour lancer un client de développement,
utiliser `runClient` à la place de `build`. Voir `../docs/TESTING.md`.

## Lancer le test autonome

Ce test ne dépend ni de Minecraft ni de NeoForge :

```bash
mkdir -p build/domain-self-test
javac --release 17 -d build/domain-self-test \
  src/main/java/dev/tidebound/core/data/TideWallet.java \
  src/main/java/dev/tidebound/core/data/PlayerVessel.java \
  src/main/java/dev/tidebound/core/data/ContractProgress.java \
  src/main/java/dev/tidebound/core/data/PlayerProgress.java \
  src/main/java/dev/tidebound/core/data/VesselEntityLink.java \
  src/main/java/dev/tidebound/core/data/VesselDeploymentState.java \
  src/main/java/dev/tidebound/core/data/VesselDeployment.java \
  src/main/java/dev/tidebound/core/data/VesselUpgrade.java \
  src/main/java/dev/tidebound/core/data/VesselUpgradeQuote.java \
  src/main/java/dev/tidebound/core/data/VesselRepairQuote.java \
  src/main/java/dev/tidebound/core/data/VesselHoldPolicy.java \
  src/main/java/dev/tidebound/core/fishing/CatchAnomaly.java \
  src/main/java/dev/tidebound/core/fishing/CatchData.java \
  src/main/java/dev/tidebound/core/fishing/CatchFreshness.java \
  src/main/java/dev/tidebound/core/fishing/CatchGenerator.java \
  src/main/java/dev/tidebound/core/fishing/CatchProfile.java \
  src/main/java/dev/tidebound/core/fishing/CatchProfiles.java \
  src/main/java/dev/tidebound/core/fishing/CatchQuality.java \
  src/main/java/dev/tidebound/core/fishing/CatchValuation.java \
  src/main/java/dev/tidebound/core/navigation/WakeBearing.java \
  src/main/java/dev/tidebound/core/progression/SkillProgression.java \
  src/main/java/dev/tidebound/core/world/ArchipelagoSurvey.java \
  src/main/java/dev/tidebound/core/world/StarterPortPlan.java \
  src/main/java/dev/tidebound/core/world/PortService.java \
  src/main/java/dev/tidebound/core/world/PortArchetype.java \
  src/main/java/dev/tidebound/core/world/PortPlan.java \
  src/main/java/dev/tidebound/core/vessel/VesselVisualProfile.java \
  src/test/java/dev/tidebound/core/data/DomainSelfTest.java
java -cp build/domain-self-test dev.tidebound.core.data.DomainSelfTest
```

## Points d'intégration

Les intégrations ne doivent pas modifier les attachments directement. Utiliser :

```java
TideboundApi.grantTides(player, amount);
TideboundApi.spendTides(player, amount);
TideboundApi.wallet(player);
TideboundApi.unlockVessel(player, vesselName);
TideboundApi.vessel(player);
TideboundApi.upgradeVessel(player, VesselUpgrade.HULL);
TideboundApi.nextVesselUpgrade(player, VesselUpgrade.HULL);
TideboundApi.purchaseVesselUpgrade(player, VesselUpgrade.HULL);
TideboundApi.repairVessel(player);
TideboundApi.renameVessel(player, vesselName);
TideboundApi.vesselDeployment(player);
TideboundApi.deployVessel(player);
TideboundApi.registerNearbyVanillaBoat(player, vesselName);
TideboundApi.refitVessel(player);
TideboundApi.giveWakeCompass(player);
TideboundApi.locateVessel(player);
TideboundApi.grantTidesOnce(player, receiptId, amount);
TideboundApi.completeMilestone(player, milestoneId);
TideboundApi.completeContract(player, contractId);
TideboundApi.grantSkillXp(player, skillId, amount);
```

## Contenu JSON

Les définitions sont placées dans :

- `data/<namespace>/tidebound/milestones/*.json` ;
- `data/<namespace>/tidebound/contracts/*.json`.

Le nom du fichier devient son identifiant. Par exemple
`data/tidebound/tidebound/contracts/coastal_delivery.json` devient
`tidebound:coastal_delivery`. La commande vanilla `/reload` recharge atomiquement les catalogues.

Avant de lancer Minecraft, valider les fichiers avec :

```bash
python tools/validate_content.py
```

Les six exemples fournis utilisent uniquement des objets Vanilla afin de démarrer sans dépendance.
Ils pourront être remplacés dans le modpack par les poissons Tidebound. Le même validateur contrôle aussi
la structure SNBT et les traductions du livre FTB Quests placé sous `../modpack/overrides/config/ftbquests/quests/`.

## Démarrage rapide du tableau de contrats

Placer un villageois, puis exécuter en administrateur :

```text
/tidebound harbor register @e[type=minecraft:villager,sort=nearest,limit=1]
```

Ou créer les cinq rôles du port autour du joueur :

```text
/function tidebound:create_test_harbor
```

Un clic droit sur l'**Intendant du port** ouvre la capitainerie. Le **charpentier naval** possède son
propre écran et devient le seul service autorisé à construire, réparer ou améliorer le navire. Les
trois autres rôles exposent déjà leur écran visuel, sans action serveur fictive. Voir `TB-NPC-001.md`.

## Boucle de départ sans intendant au spawn

Le spawn n'a pas besoin de contenir un port. Le joueur peut suivre la boucle suivante :

1. récolter du bois sur son île procédurale ;
2. fabriquer une barque vanilla ;
3. naviguer jusqu'à un port contenant un intendant Tidebound ;
4. rester dans la barque ou la placer à moins de huit blocs de l'intendant ;
5. utiliser `[ENREGISTRER LA BARQUE]` pour en faire son navire personnel ;
6. employer le Compas de sillage remis par l'intendant pour la retrouver.

## Génération de l'archipel

Toute nouvelle sauvegarde créée avec Tidebound remplace le preset normal par l'archipel. Le preset
`Tidebound — Archipel` est également exposé dans la liste des types de monde. Le spawn cible une zone
forestière entourée de plages et d'océan ; les dimensions du Nether et de l'End restent vanilla.

Ne pas ouvrir une ancienne sauvegarde importante sans copie : les chunks déjà présents restent intacts,
mais les nouveaux chunks suivront le relief insulaire et peuvent former une frontière visible. Pour
contrôler une seed neuve avec les commandes autorisées :

```mcfunction
/tidebound world diagnose 128
```

Le port initial reste facultatif et n'est pas encore généré automatiquement. Pour valider le placement
runtime sur un rivage proche, utiliser `/tidebound world port-place` ; une île sans port doit toujours
permettre de fabriquer une barque vanilla.

Une recette de remplacement du compas est disponible avec un compas vanilla, du cuivre et du papier.

## Prises Tidebound

Une prise conserve son item vanilla et reçoit le composant `tidebound:catch_data`. La valeur estimée
dépend du profil de l'espèce, du poids, de la qualité, de l'anomalie et de la fraîcheur actuelle.
Les durées du prototype sont : fraîche moins de 24 000 ticks, vieillissante jusqu'à 72 000, passée
jusqu'à 144 000, puis avariée. Aucun scan d'inventaire n'est nécessaire : l'état est calculé depuis
l'instant de capture.

Le prototype `/tidebound world port-place` matérialise désormais le plan régional près d'un rivage et
l'inscrit dans l'index persistant utilisé par le Compas des Havres.

## Fonte rapide

L'enchantement `tidebound:fast_smelting` se pose sur les mêmes outils qu'Efficacité, via la table
d'enchantement ou l'enclume. Il n'accélère la cuisson d'un four (fourneau, haut-fourneau, fumoir) que
si le joueur a lui-même le menu ouvert et tient l'outil enchanté en main : aucune ligne automatisée
(entonneur, Create) n'en bénéficie jamais. Voir `TB-SMELT-001.md` pour l'implémentation technique
(access transformer sur les champs de cuisson du four, aucun événement NeoForge dédié n'existant).

## Modules v1

Acheter un emplacement de module au chantier active le module correspondant, dans un ordre fixe :
1 = Projecteur, 2 = Sonar, 3 = Treuil, 4 = Filet (`VesselModule`). Aucun n'agit sur un navire sans
surveillance : le Projecteur ne réagit qu'à une vraie tentative de spawn hostile nocturne à proximité,
les trois autres exigent que le propriétaire soit en ligne et à moins de douze blocs de son navire. Voir
`TB-CORE-005C.md` pour le détail de chaque module et les garanties anti-automatisation.
