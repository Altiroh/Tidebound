# Changelog

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
