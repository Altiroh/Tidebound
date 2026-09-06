# Transmission Tidebound

Dernière mise à jour : **6 septembre 2026**

Branche de référence : `main`

État importé : `TB-CORE-008` (ports/PNJ/interfaces après un deuxième retour) / `0.25.0-alpha`

Ce fichier est la porte d'entrée pour reprendre le projet. Il doit être actualisé après chaque ticket terminé, même si les notes techniques détaillées existent ailleurs.

## Vision à préserver

Tidebound est une aventure Minecraft maritime **sandbox et procédurale**, pas une campagne scénarisée. Le monde, les îles et les ports varient selon la seed. Le livre facultatif **Le Voyage** donne une direction, un rythme et des récompenses sans bloquer les autres activités. Les objectifs prennent trois formes :

- des étapes du Voyage, visibles et non obligatoires ;
- des paliers uniques proches de succès, qui récompensent la progression ;
- des contrats répétables, qui fournissent Tides, denrées et matériaux.

Le joueur doit devenir efficace rapidement, tout en restant libre de construire, explorer, cultiver, combattre ou commercer. L'océan oriente la progression sans supprimer Minecraft.

## Ce qui fonctionne dans le code

- portefeuille de Tides persistant et copié après la mort ;
- données persistantes du navire : identité, nom et niveaux coque/moteur/cale/modules ;
- API serveur pour créditer/dépenser des Tides et piloter la progression ;
- reçus idempotents contre la double attribution d'une récompense ;
- paliers et contrats chargés depuis des JSON de datapack ;
- contrats répétables avec consommation d'objets et cooldown ;
- quatre métiers à dix niveaux : pêche, navigation, commerce, récupération ;
- XP automatique de pêche et de navigation ;
- intendant de port ouvrant un menu graphique synchronisé ;
- icône d'ancre dans l'inventaire et carnet de bord visuel ;
- entité physique `tidebound:vessel`, fondée sur la physique de `ChestBoat` mais dotée de son propre rendu ;
- quatre places et silhouette voxel modulaire avec progression visible ;
- barque vanilla enregistrée comme **Barque de fortune** détectable mais jamais améliorable ;
- construction explicite du navire Tidebound au chantier, avec conservation de la cale, des dégâts et de l'identité ;
- propriété, position, renommage, déploiement et protection du navire ;
- effets légers des niveaux de coque et de moteur ;
- enregistrement d'une barque vanilla existante comme navire personnel ;
- Compas de sillage utilisable pour direction, distance et dernière position ;
- états persistants déployé, disparu, détruit et sans position ;
- exemples de ponts KubeJS et FTB Quests ;
- achats de coque, moteur, cale et emplacements de modules avec Tides et matériaux ;
- prérequis de navigation, commerce ou récupération selon l'amélioration ;
- transactions anti-double clic avec remboursement en cas d'échec ;
- réparations payantes uniquement lorsque le navire est ramené au port ;
- capacité de cale progressive : 9, 18 puis 27 emplacements, sans suppression du surplus ;
- Gradle Wrapper, workflow GitHub Actions et guide de test reproductible ;
- Devpack CurseForge avec FTB Quests/Ultimine, Create, JEI, Tom's Storage, JourneyMap, Waystones,
  ambiance sonore, physique des objets, Sodium, Iris et leurs dépendances ;
- Waystones configuré sans coût d'expérience et validation des références épinglées ;
- cinq entités portuaires dédiées utilisant les atlas du pack artistique : Intendant, charpentier,
  poissonnier, naturaliste et gardien de phare ;
- séparation des responsabilités : l'Intendant gère le navire administratif, le charpentier est le
  seul service d'amélioration/réparation/construction ;
- écran illustré propre à chaque rôle, avec actions serveur uniquement lorsqu'elles existent ;
- estimation en direct et vente physique des prises estampillées auprès du poissonnier ;
- transaction de vente non duplicable créditant Tides et XP Commerce sans toucher aux poissons vanilla ;
- planificateur stable des ports par seed/région, avec cinq archétypes pondérés et services facultatifs ;
- placement administrateur d'un quai compact sur un rivage détecté, avec uniquement les PNJ du plan ;
- index persistant des havres et Compas des Havres pointant vers l'Intendant enregistré le plus proche ;
- livre FTB Quests bilingue au format SNBT v13 avec les chapitres `Naufragé` et `Premier port` ;
- neuf objectifs sans dépendances obligatoires et neuf récompenses idempotentes totalisant 105 Tides ;
- lore canonique consolidé dans `docs/design/Tidebound_Lore.md` ;
- composant persistant `tidebound:catch_data` posé sur les quatre poissons vanilla réellement pêchés ;
- génération serveur du poids, de la qualité, du biome d'origine et d'une anomalie très rare ;
- fraîcheur calculée depuis l'instant de capture et valeur estimée évolutive ;
- tooltip bilingue, résumé de capture et commande `/tidebound catch inspect` ;
- tests Java autonomes du domaine ;
- première intégration du pack artistique : interface d'intendant, Compas de sillage et catalogue.
- six composants de maintenance issus du pack artistique, avec items, modèles et recettes ;
- profil CurseForge `Tidebound — Into the Deep` incluant désormais Architectury API ;
- preset normal remplacé par un archipel pour toute nouvelle sauvegarde ;
- relief insulaire partagé par le terrain et les biomes, avec cœur forestier ciblé au spawn ;
- diagnostic serveur terre/eau/rivage/bois et détection de masse continentale ;
- tirage stable du futur port initial et smoke test CI créant réellement un monde neuf ;
- enchantement `tidebound:instant_smelting` sur les outils de minage vanilla : mine un minerai
  éligible et récupère directement son équivalent fondu, incompatible avec Toucher de soie/Fortune ;
- modules v1 branchés à de vrais systèmes selon les emplacements achetés : Projecteur (repousse les
  spawns hostiles nocturnes), Sonar (pêche prometteuse, dangers, puis épaves/objets cachés au niveau 5
  de Navigation), Treuil (attire les objets flottants) et Filet (multi-prise et pêche passive au
  mouillage), tous inactifs sans le propriétaire en ligne à proximité ;
- interface de cale du navire Tidebound n'affichant que les lignes débloquées (9/18/27 cases), en
  réutilisant directement `ChestMenu` vanilla sans nouveau menu ni écran ;
- onglet créatif dédié et solde de Tides visible dans l'inventaire, issus du retour de `TB-QA-001` ;
- nom du biome affiché en haut de l'écran à chaque changement, entièrement client (rouge si
  `#tidebound:dangerous`) ;
- port initial matérialisé automatiquement près du spawn, puis un port par région de 512 blocs avec
  ~1/3 de chance en explorant (jamais plus de quelques centaines/un millier de blocs à parcourir) ;
- 22 biomes vanilla (contre 5) répartis par continentalité et humidité (plus rapide à varier que la
  température, corrigé après un premier essai trop uniforme), plus deux biomes Tidebound à eau
  teintée (`violet_shallows`, `abyss_ocean`) en poches rares, `abyss_ocean` dans `#tidebound:dangerous` ;
- bruit de forme des îles élargi (×1,6) pour que les profondeurs/reliefs extrêmes soient réellement
  atteints, au lieu de rester coincés près du centre de la plage de valeurs ;
- dégâts progressifs au navire en eaux dangereuses (profondeurs, glace, `abyss_ocean`) si la coque est
  sous le niveau 3 ;
- bâtiment de port fermé (sol, murs, porte, fenêtres, toit en surplomb) généré derrière chaque quai à
  la place d'un ponton nu avec des props éparpillés, props de service rangés à l'intérieur ;
- cinq skins PNJ reconstruits à partir de la vraie texture villageoise vanilla (64×64) recolorée par
  calque de métier vanilla, remplaçant les atlas illustrés 512×512 qui rendaient buggés en jeu ;
- écran portuaire `HarborScreen` réduit à 0,375× (198×215 px) pour se rapprocher d'un écran de
  commerce vanilla, tous les décalages internes recalculés au même facteur.

Les détails et commandes sont dans `core/README.md`, les notes `core/TB-CORE-001.md` à
`core/TB-CORE-008.md`, `core/TB-WORLD-002.md`, `docs/quests/TB-QUEST-001.md` et
`docs/fishing/TB-FISH-001.md`.

## Ce qui n'est pas encore implémenté

Les points suivants sont des décisions ou besoins acceptés, mais ne doivent pas être présentés comme fonctionnels :

1. **Demandes du poissonnier.** La vente générale fonctionne ; les prix du jour et commandes spéciales
   affichés dans la maquette restent à rendre dynamiques.
2. **Catalogue par datapack.** Les quatre profils vanilla sont encore définis dans le code ; les poissons de mods ne sont pas intégrés.
3. **Stockage spécialisé.** Les caractéristiques uniques empêchent naturellement la plupart des prises de s'empiler ; casiers et viviers restent à créer.
4. **Contenu procédural.** Le port initial se matérialise désormais automatiquement près du spawn
   quand le tirage par seed le réserve (`TB-WORLD-002` tranche 1) ; épaves, phares et autres points
   d'intérêt restent à placer, de même que la variété de biomes maritimes (tranche 2, en cours).
5. **Automatisation complète du livre.** Six objectifs utilisent provisoirement une case manuelle tant que les événements Core correspondants n'existent pas.
6. **Équilibrage des modules.** Les effets de `TB-CORE-005C` sont branchés mais leurs rayons, intervalles
   et la chance de multi-prise du Filet n'ont pas encore été ajustés par un vrai test en jeu.
7. **Aiguille animée du Compas.** Le clic droit donne déjà direction et distance ; le modèle animé pointant physiquement vers le navire reste une amélioration future.
8. **Validation en jeu.** Le workflow compile automatiquement ; un lancement manuel du Devpack complet et le smoke test de `docs/TESTING.md` restent indispensables.
9. **PNJ : validation visuelle.** Les cinq skins repartent désormais de la texture villageoise vanilla
    recolorée (UV garanti correct), mais la palette de couleurs et la lisibilité à plusieurs distances
    doivent encore être confirmées dans un vrai client (`TB-CORE-008`).
10. **Modèle final du navire.** L'entité dédiée existe avec une coque voxel fonctionnelle ; le modèle Blockbench et ses animations restent à importer.
11. **Finition des interfaces.** L'intendant utilise la maquette artistique, mais contrats, traduction complète des éléments graphiques et animations restent à raccorder.

## Prochaine tâche recommandée

`TB-CORE-008` a répondu aux trois décisions du deuxième retour client (voir `core/TB-CORE-008.md`) :
bâtiment de port fermé au lieu d'un ponton nu, skins PNJ reconstruits depuis la base vanilla, écran
portuaire réduit à l'échelle d'un écran de commerce classique — toujours sans structure `.nbt`, décision
maintenue explicitement par l'utilisateur.

### En attente du prochain retour client

- confirmer que les cinq PNJ s'affichent correctement (plus de pixels mélangés) et juger la palette de
  recolorisation ;
- confirmer que l'écran portuaire est bien proportionné après la réduction d'échelle ;
- reconfirmer si les boutons de l'Intendant/Charpentier fonctionnent maintenant (hypothèse : ils étaient
  gênés par le placement erratique des PNJ sur l'ancien ponton, pas par un bug de câblage) ;
- juger si le nouveau bâtiment de port est suffisant ou nécessite une itération supplémentaire ;
- valider en jeu ports/biomes/dégâts de coque sur plusieurs seeds (rien de tout ça n'a encore été vu
  tourner dans un vrai client).

### Ensuite

- animer l'aiguille des compas (Compas de sillage/des Havres) — nécessite soit des frames de texture
  façon compas vanilla, soit un modèle 3D animé ;
- gros navires échoués rares (nécessite soit une structure `.nbt`, soit un générateur procédural
  dédié — aucun des deux commencé) ;
- variété d'humidité supplémentaire (mangrove, badlands...).

Pas de structures `.nbt` externes pour l'instant (décision prise avec l'utilisateur) : le système
procédural actuel (`HarborPlacementService`) reste la source de vérité, à réévaluer plus tard si besoin.

La matrice de vingt seeds de `TB-WORLD-001` reste un test manuel obligatoire : la CI valide le décodage
du worldgen et la création d'un monde, mais ne permet pas encore d'annoncer 20/20 spawns jouables.

## Architecture et sources de vérité

- `core/src/main/java/dev/tidebound/core/data/` : modèles persistants ;
- `core/src/main/java/dev/tidebound/core/service/` : logique serveur ;
- `core/src/main/java/dev/tidebound/core/fishing/` : modèle, génération, fraîcheur et valeur des prises ;
- `core/src/main/java/dev/tidebound/core/api/TideboundApi.java` : façade publique ;
- `core/src/main/java/dev/tidebound/core/event/` : événements NeoForge ;
- `core/src/main/java/dev/tidebound/core/client/` et `menu/` : écrans et synchronisation visuelle ;
- `core/src/main/resources/data/` : contenu datapack ;
- `core/src/test/` : tests autonomes ;
- `modpack/overrides/config/ftbquests/quests/` : livre FTB Quests versionné ;
- `docs/design/Tidebound_Lore.md` : lore, tonalité et progression canoniques ;
- `docs/assets/ASSET_CATALOG.md` : inventaire et état de préparation des assets ;
- `docs/design/Tidebound_GDD_Prototype_v0.3.docx` : conception produit ;
- `docs/technical/Tidebound_Matrice_Technique_v0.1.xlsx` : choix des mods et versions.

Ne pas faire dépendre la logique métier de FTB Quests, KubeJS ou d'un PNJ précis. Ces éléments consomment
l'API Tidebound ; ils ne sont pas la source de vérité. Le Voyage peut guider et récompenser, mais ne doit
pas devenir une condition générale d'accès au sandbox.

## Vérification attendue

Le projet cible Java 21. Exécuter, depuis `core/` :

```bash
python tools/validate_content.py
./gradlew check
./gradlew build
```

Le wrapper est inclus ; Java 21 suffit. Le workflow `.github/workflows/build.yml` produit aussi un artifact.
Ne jamais annoncer qu'une boucle fonctionne en jeu avant un vrai lancement client/serveur NeoForge.

## Règle de transmission

Après chaque ticket :

1. mettre à jour la version dans `gradle.properties`, les métadonnées et la documentation si nécessaire ;
2. ajouter une note `core/TB-CORE-XXX.md` ;
3. déplacer la tâche courante dans la section « terminé » de `ROADMAP.md` ;
4. mettre à jour ce fichier avec l'état réel et la prochaine tâche ;
5. compléter `CHANGELOG.md` ;
6. exécuter les validations possibles et documenter explicitement celles qui restent impossibles ;
7. créer un commit Git ciblé et lisible.
