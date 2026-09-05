# Tidebound Core — 0.8.0-alpha

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
- villageois-intendant affichant un tableau de contrats cliquable ;
- navire physique personnel basé sur un bateau-coffre, lié à son propriétaire ;
- réclamation, mise à l'eau, localisation et renommage auprès de la capitainerie ;
- position du navire persistante entre les sessions et protection contre les autres joueurs ;
- effets physiques légers des améliorations de coque et de moteur ;
- enregistrement d'une barque vanilla existante auprès d'un intendant ;
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

Le navire utilise volontairement l'entité bateau-coffre vanilla comme première coque jouable. La source de vérité
serveur survivra ainsi au futur remplacement du modèle ou du moteur par une entité propriétaire. Le livre `Le Voyage`
guide le joueur sans verrouiller le sandbox : ses objectifs actuels sont visibles et sans dépendances obligatoires.

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

Le JAR est produit dans `build/libs/tidebound-0.8.0-alpha.jar`. Pour lancer un client de développement,
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

Ou créer directement un intendant à la position d'exécution :

```text
/function tidebound:create_harbor_intendant
```

Un clic droit sur cet **Intendant du port** affiche la capitainerie, le navire personnel, les contrats
et leurs boutons d'action. Voir `TB-CORE-005A.md` pour la boucle de départ et `examples/` pour les ponts.

## Boucle de départ sans intendant au spawn

Le spawn n'a pas besoin de contenir un port. Le joueur peut suivre la boucle suivante :

1. récolter du bois sur son île procédurale ;
2. fabriquer une barque vanilla ;
3. naviguer jusqu'à un port contenant un intendant Tidebound ;
4. rester dans la barque ou la placer à moins de huit blocs de l'intendant ;
5. utiliser `[ENREGISTRER LA BARQUE]` pour en faire son navire personnel ;
6. employer le Compas de sillage remis par l'intendant pour la retrouver.

Une recette de remplacement du compas est disponible avec un compas vanilla, du cuivre et du papier.

## Prises Tidebound

Une prise conserve son item vanilla et reçoit le composant `tidebound:catch_data`. La valeur estimée
dépend du profil de l'espèce, du poids, de la qualité, de l'anomalie et de la fraîcheur actuelle.
Les durées du prototype sont : fraîche moins de 24 000 ticks, vieillissante jusqu'à 72 000, passée
jusqu'à 144 000, puis avariée. Aucun scan d'inventaire n'est nécessaire : l'état est calculé depuis
l'instant de capture.

La prochaine brique recommandée est `TB-ECON-001` : poissonnier de port, vente physique des prises et
versement transactionnel des Tides. Les modules du navire reprendront après les API de pêche et de monde.
