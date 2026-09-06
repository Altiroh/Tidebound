# Changelog

## 0.23.0-alpha — TB-WORLD-002 (tranche 2)

- 25 biomes vanilla (contre 5) : chaque bande de continentalité de l'archipel est désormais aussi
  découpée par température (glacial/froid/tempéré/chaud/torride), zéro nouvel asset ;
- deux biomes Tidebound à eau teintée en poches rares : `violet_shallows` (violet, océan tempéré) et
  `abyss_ocean` (bleu très sombre, océan profond tempéré) — uniquement des couleurs, pas de nouvel
  asset visuel non plus ;
- le tag `#tidebound:dangerous` (vide depuis `TB-CORE-007`) est peuplé : profondeurs, eaux glacées,
  `abyss_ocean` ;
- naviguer dans une zone dangereuse avec une coque de niveau < 3 use désormais le navire au fil du
  temps — conséquence mécanique de l'annonce de biome déjà en place ;
- chaque région de 512 blocs a ~1/3 de chance d'obtenir son propre port en l'explorant, en plus du
  port initial du spawn, pour ne jamais forcer un trajet de plusieurs milliers de blocs ;
- Devpack : ajoute Complementary (Reimagined), Photon, Solas et AstraLex comme shaders au choix dans
  Iris (aucun activé par défaut) ; Reverie demandé mais exclu, incompatible avec Minecraft 1.21.1.

Codé, compilé et démarré sur un serveur réel sans erreur ; **non validé visuellement en jeu** (rendu
des teintes, rareté effective des poches, distance réelle aux ports, équilibrage des dégâts de coque).

## 0.22.0-alpha — TB-WORLD-002 (tranche 1) + icônes TB-CORE-007

- le port initial (tirage ~1/3 par seed) se matérialise désormais automatiquement près du spawn au
  premier démarrage du serveur, au lieu d'exiger `/tidebound world port-place` en administrateur ;
- remplace les huit icônes jugées trop détaillées (`wake_compass`, `haven_compass`, `repair_kit`,
  `caulking_kit`, `engine_parts`, `hold_fittings`, `hull_plate`, `mechanical_oil`) par de nouveaux
  visuels réduits à l'échelle vanilla ;
- le Compas des Havres a maintenant sa propre texture et sa propre silhouette (son modèle pointait
  par erreur vers le compas vanilla) ;
- nouveau logo `tidebound_logo.png` (ancre/tentacule/lanterne).

## 0.21.0-alpha — TB-CORE-007

- renomme `tidebound:fast_smelting` en `tidebound:instant_smelting` et change sa mécanique : miner un
  bloc avec l'outil enchanté récupère directement le résultat fondu (`BlockDropsEvent`), au lieu
  d'accélérer un four surveillé ; niveau unique, incompatible avec Toucher de soie/Fortune ;
- ajoute un onglet créatif dédié regroupant tous les objets Tidebound ;
- affiche le solde de Tides dans l'écran d'inventaire (portefeuille désormais synchronisé au client) ;
- annonce le biome traversé à chaque changement, en rouge si tagué `#tidebound:dangerous` ;
- corrige l'icône du Compas de sillage, qui pointait par erreur vers le compas vanilla ;
- ajoute Lootr (loot indépendant par joueur) et FTB Quests Translator (traduction du livre à la
  demande) au Devpack ;
- retiré : `META-INF/accesstransformer.cfg`, devenu inutile avec la nouvelle mécanique d'Instant
  Smelting.

## Devpack — retrait temporaire de FancyMenu

- FancyMenu, Konkrete et Melody retirés du manifeste : premier test bloqué par les dépendances
  manquantes, retiré le temps de stabiliser le reste du pack plutôt que d'empiler les correctifs ;
- le fond et les cinq boutons d'écran-titre restent conservés dans `docs/assets/title_screen/` pour
  une reprise ultérieure, sans lien avec un mod précis.

## Devpack — écran-titre personnalisé

- ajoute FancyMenu (client uniquement) au manifeste pour un écran-titre aux couleurs de Tidebound ;
- fournit le fond et les cinq boutons (Singleplayer, Multiplayer, Mods, Options, Quit Game) dans
  `modpack/overrides/config/fancymenu/assets/tidebound/` ;
- le montage précis (positions, textures par bouton) se fait dans l'éditeur FancyMenu en jeu, pas en
  code, conformément à l'usage du mod.

## 0.20.0-alpha — TB-CORE-006

- le navire Tidebound n'affiche plus que les lignes de cale débloquées (9/18/27 cases) au lieu des
  27 cases vanilla systématiques, en réutilisant `ChestMenu` sans nouveau menu ni écran ;
- le conteneur physique conserve toujours ses 27 cases : rien n'est perdu lors d'une amélioration ;
- versions de `neoforge.mods.toml` et `modpack/manifest.json` resynchronisées avec
  `gradle.properties` (elles dérivaient depuis plusieurs tickets) ;
- compilation vérifiée avec un JDK 21 réel contre NeoForge 21.1.249 / Minecraft 1.21.1.

## 0.19.0-alpha — TB-CORE-005C

- modules v1 branchés aux emplacements déjà vendus par le chantier, activation fixe et cumulative
  (1 = Projecteur, 2 = Sonar, 3 = Treuil, 4 = Filet) ;
- Projecteur : repousse les spawns hostiles la nuit autour du navire (`MobSpawnEvent.PositionCheck`) ;
- Sonar : signale les eaux prometteuses et les dangers proches, détecte épaves et objets flottants
  cachés à partir du niveau 5 de Navigation ;
- Treuil : attire les objets flottants vers le navire ;
- Filet : chance de prise supplémentaire à la pêche active, et pêche passive au mouillage à XP réduite ;
- aucun module n'agit sans le propriétaire en ligne et à proximité, pour ne jamais accélérer une
  installation automatisée ;
- compilation vérifiée avec un JDK 21 réel contre NeoForge 21.1.249 / Minecraft 1.21.1.

## 0.18.0-alpha — TB-SMELT-001

- enchantement `tidebound:fast_smelting` sur les outils de minage vanilla (comme Efficacité) ;
- accélère la cuisson d'un four uniquement tant que le joueur tient l'outil et surveille lui-même le
  menu ouvert ; aucun effet sur les lignes automatisées (entonnoirs, Create) ;
- access transformer dédié exposant les champs de cuisson du four, faute d'événement NeoForge adapté ;
- compilation vérifiée avec un JDK 21 réel contre NeoForge 21.1.249 / Minecraft 1.21.1.

## 0.17.0-alpha — TB-NAV-001

- registre mondial persistant des havres matérialisés, indépendant des chunks chargés ;
- Compas des Havres distinct du Compas de sillage ;
- direction et distance vers le havre enregistré doté d'un Intendant le plus proche ;
- recette avancée utilisant bois, cuivre, fer, redstone, or et boussole vanilla ;
- modèle d'item provisoirement vanilla-friendly en attente d'un asset dédié.

## 0.16.0-alpha — TB-PORT-001B

- recherche serveur d'un rivage exploitable dans un rayon borné ;
- quai compact différencié visuellement selon les cinq archétypes ;
- stockage, pupitre, cloche et marqueur mécanique posés uniquement si le plan les prévoit ;
- invocation des seuls PNJ annoncés par le plan, avec protection contre les doublons ;
- commande de test `/tidebound world port-place` réservée aux administrateurs.

## 0.15.0-alpha — TB-PORT-001A

- ajoute cinq archétypes pondérés de ports et avant-postes ;
- choisit par seed/région un roster stable de services ;
- garantit qu'un port ne contient jamais automatiquement les cinq PNJ ;
- limite les mécanismes Create aux variantes cohérentes et occasionnelles ;
- ajoute `/tidebound world port-plan` pour inspecter le futur site sans le placer.

## 0.14.0-alpha — TB-ECON-001

- affiche chez le poissonnier le nombre et la valeur actuelle des prises vendables ;
- ajoute une action visuelle **Tout vendre** exécutée uniquement près du poissonnier ;
- consomme exclusivement les prises estampillées à valeur positive ;
- crédite les Tides et l'XP Commerce dans une transaction non duplicable ;
- laisse intacts les poissons vanilla ordinaires et les prises sans valeur.
- répare les cinq atlas PNJ tronqués lors de leur premier transport et valide désormais tous les
  chunks PNG jusqu'à leur marque de fin.

## 0.13.0-alpha — TB-DESIGN-002 / TB-NPC-001

- adopte le lore canonique vanilla-friendly : archipel vivant, îles spécialisées, abysses,
  Connaissance interdite et bénéfices uniques de reliques ;
- distingue définitivement Compas des Havres et Compas de sillage ;
- ajoute cinq entités de PNJ portuaires proches du villageois vanilla, avec leurs textures dédiées ;
- branche les cinq maquettes d'interface du pack artistique ;
- réserve enregistrement/navigation à l'Intendant et construction/améliorations au charpentier ;
- ajoute `/function tidebound:create_test_harbor` pour tester les cinq rôles.

Toutes les évolutions notables de Tidebound sont consignées ici.

## Non publié

## 0.12.0-alpha — TB-PACK-001

- extension du Devpack à Create, JEI, Tom's Simple Storage, JourneyMap, Waystones et FTB Ultimine ;
- ajout d'AmbientSounds, ItemPhysic, Sodium, Iris et ImmediatelyFast pour l'ambiance et le rendu ;
- ajout explicite de Balm et CreativeCore, dépendances requises ;
- téléportations Waystones configurées sans coût d'expérience ;
- validation statique des dix-sept références CurseForge et de la configuration du pack ;
- documentation de la séparation client/serveur, du test des shaders et du choix de remplacer NEI par JEI ;
- enchantement Fast Smelting différé faute de candidat NeoForge 1.21.1 vérifié.

## 0.11.1-alpha — Correctif de classification des navires

- l'enregistrement conserve la barque vanilla comme **Barque de fortune** ;
- les améliorations sont refusées avant la construction du navire Tidebound au chantier ;
- l'interface masque les jauges de la barque et explique visuellement la transition.

## 0.11.0-alpha — TB-VESSEL-001

- ajout du type d'entité `tidebound:vessel`, distinct des barques vanilla ;
- première coque voxel Tidebound, plus large et dotée d'un pont, d'un mât et d'une voile ;
- quatre places à bord avec positions séparées ;
- renforts de coque, cale fermée, moteur et supports de modules visibles selon la progression ;
- synchronisation client des quatre axes visuels et persistance NBT de secours ;
- construction explicite du navire Tidebound au chantier après l'enregistrement d'une Barque de fortune ;
- action **Chantier naval** et commande `/tidebound vessel refit` pour les anciennes sauvegardes ;
- conservation du propriétaire, du nom, des dégâts, du mouvement, des passagers et du contenu lors de la migration ;
- architecture de rendu isolée pour permettre le remplacement ultérieur par un modèle Blockbench.
- intégration de six composants illustrés : kit de réparation, calfatage, pièces moteur, ferrures, plaques et huile ;
- recettes vanilla de transition et utilisation des composants par les réparations et améliorations ;
- logo Tidebound affiché dans la fiche du mod et nom **Into the Deep** dans le profil de développement ;
- ajout d'Architectury API au manifeste, corrigeant le démarrage de FTB Library, Teams et Quests.

## 0.10.0-alpha — TB-WORLD-001

- remplacement du monde normal des nouvelles sauvegardes par le générateur d'archipel Tidebound ;
- preset explicite `tidebound:archipelago` disponible dans l'écran de création ;
- relief insulaire et continentalité partagés entre terrain et biomes ;
- océans profonds, océans, plages, plaines et cœurs forestiers garantissant la boucle bois → barque ;
- cible de spawn déplacée vers l'intérieur boisé des îles ;
- commande `/tidebound world diagnose [radius]` pour mesurer terre, eau, rivages et bois ;
- tirage déterministe d'un futur port de départ sur environ une seed sur trois ;
- smoke test CI démarrant un serveur et générant réellement un monde neuf ;
- avertissement de compatibilité : monde neuf requis pour éviter les raccords de chunks.

## 0.9.0-alpha — TB-UX-001

- ajout d'une icône Tidebound dans l'inventaire du joueur ;
- ajout d'un carnet de bord visuel présentant la boucle du prototype ;
- remplacement du tableau textuel de l'intendant par un menu serveur/client synchronisé ;
- affichage graphique des Tides, de l'état du navire et des niveaux coque/moteur/cale/modules ;
- boutons pour enregistrer, mettre à l'eau, retrouver, réparer et améliorer le navire ;
- intégration de l'interface d'intendant et du Compas de sillage issus du pack artistique ;
- catalogue des PNJ, interfaces, poissons, modules, objets et stations déjà créés ;
- conservation d'une référence optimisée du futur skin d'intendant, sans déclarer encore une entité dédiée.

## 0.8.0-alpha — TB-FISH-001

- ajout du Data Component persistant et synchronisé `tidebound:catch_data` ;
- enrichissement des morues, saumons, poissons tropicaux et poissons-globes réellement pêchés ;
- poids, qualité, biome d'origine, instant de capture et anomalies rares ;
- fraîcheur dynamique sans scan ni tick d'inventaire ;
- estimation serveur de la valeur selon tous les attributs de la prise ;
- tooltips français/anglais, résumé de capture et commande `/tidebound catch inspect` ;
- le palier et l'XP de première prise ignorent désormais les déchets de pêche ;
- tests de génération déterministe, vieillissement, profils et multiplicateurs de valeur.

## 0.7.0-alpha — TB-DESIGN-001 / TB-QUEST-001

- intégration d'un lore canonique compatible avec le sandbox et d'un fil directeur facultatif `Le Voyage` ;
- définition des sept chapitres à long terme, du Journal et de la Connaissance interdite ;
- ajout du vrai livre FTB Quests SNBT v13 dans le Devpack ;
- chapitres `Naufragé` et `Premier port`, neuf objectifs bilingues et 105 Tides de démarrage ;
- récompenses automatiques idempotentes avec `{p}` et permission 2 ;
- aucun verrouillage narratif entre les objectifs ;
- validation statique des identifiants, tâches, récompenses, traductions et délimiteurs SNBT.

## 0.6.0-alpha — TB-CORE-005B

- achats d'améliorations au port avec Tides, matériaux et prérequis de métier ;
- transactions serveur protégées contre les doubles clics et restaurées en cas d'échec ;
- réparation payante d'un navire physiquement ramené au quai ;
- capacité de cale progressive de 9, 18 puis 27 emplacements sans suppression d'objets ;
- conversion d'une barque enregistrée en bateau-coffre lors de l'amélioration de cale ;
- Gradle Wrapper officiel et workflow GitHub Actions Java 21 ;
- guide de test complet et manifeste de modpack CurseForge minimal ;
- artifact CI contenant un Devpack CurseForge directement importable ;
- pont FTB Quests corrigé avec `{p}` et permission 2.

## 0.5.0-alpha — TB-CORE-005A

- dépôt GitHub public initialisé avec la documentation de transmission ;
- ajout du Compas de sillage, remis au premier enregistrement et reproductible ;
- direction, distance et dernière position du navire accessibles par clic droit ;
- états persistants déployé, disparu, détruit et sans position connue ;
- enregistrement au port d'une barque vanilla existante ;
- commande de localisation réservée aux administrateurs.

## 0.4.0-alpha — TB-CORE-004

- navire physique personnel basé sur un bateau-coffre vanilla ;
- lien persistant entre le joueur et l'entité ;
- réclamation, déploiement, localisation et renommage ;
- protection contre l'utilisation par les autres joueurs ;
- premiers effets de coque et de moteur.

## 0.3.0-alpha — TB-CORE-003

- métiers, XP automatique de pêche et de navigation ;
- intendant de port et tableau de contrats ;
- ponts KubeJS et FTB Quests.

## 0.2.0-alpha — TB-CORE-002

- paliers uniques et contrats répétables en JSON ;
- cooldowns, livraisons et récompenses idempotentes.

## 0.1.0-alpha — TB-CORE-001

- portefeuille de Tides ;
- modèle persistant du navire ;
- première API et commandes de diagnostic.
