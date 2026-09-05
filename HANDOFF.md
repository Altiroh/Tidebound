# Transmission Tidebound

Dernière mise à jour : **5 septembre 2026**

Branche de référence : `main`

État importé : `TB-VESSEL-001` / `0.11.1-alpha`

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
- manifeste CurseForge minimal avec FTB Library, Teams et Quests ;
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
- tirage stable du futur port initial et smoke test CI créant réellement un monde neuf.

Les détails et commandes sont dans `core/README.md`, les notes `core/TB-CORE-001.md` à
`core/TB-CORE-005B.md`, `docs/quests/TB-QUEST-001.md` et `docs/fishing/TB-FISH-001.md`.

## Ce qui n'est pas encore implémenté

Les points suivants sont des décisions ou besoins acceptés, mais ne doivent pas être présentés comme fonctionnels :

1. **Vente des prises.** La valeur est calculée, mais aucun poissonnier ne consomme encore les poissons contre des Tides.
2. **Catalogue par datapack.** Les quatre profils vanilla sont encore définis dans le code ; les poissons de mods ne sont pas intégrés.
3. **Stockage spécialisé.** Les caractéristiques uniques empêchent naturellement la plupart des prises de s'empiler ; casiers et viviers restent à créer.
4. **Contenu procédural.** L'archipel et l'île de départ existent, mais ports, épaves, phares et autres points d'intérêt ne sont pas encore placés.
5. **Automatisation complète du livre.** Six objectifs utilisent provisoirement une case manuelle tant que les événements Core correspondants n'existent pas.
6. **Modules.** Sonar, treuil, projecteur et filet ne sont pas encore équipables.
7. **Interface de cale dédiée.** Le conteneur vanilla montre encore 27 cases ; le serveur rend le contenu des cases verrouillées au joueur au lieu de le supprimer.
8. **Aiguille animée du Compas.** Le clic droit donne déjà direction et distance ; le modèle animé pointant physiquement vers le navire reste une amélioration future.
9. **Validation en jeu.** Le workflow compile automatiquement ; un lancement manuel client/serveur et le smoke test de `docs/TESTING.md` restent indispensables.
10. **PNJ original.** Le skin de l'intendant existe comme référence artistique mais n'est pas encore converti en texture d'entité compatible.
11. **Modèle final du navire.** L'entité dédiée existe avec une coque voxel fonctionnelle ; le modèle Blockbench et ses animations restent à importer.
12. **Finition des interfaces.** L'intendant utilise la maquette artistique, mais contrats, traduction complète des éléments graphiques et animations restent à raccorder.

## Prochaine tâche recommandée

### TB-ECON-001 — Vente physique des prises

À réaliser maintenant :

- ajouter un poissonnier ou une station de vente avec une interface visuelle ;
- consommer physiquement les prises Tidebound déposées par le joueur ;
- créditer exactement leur valeur actuelle en Tides dans une transaction atomique ;
- attribuer l'XP de Commerce et raccorder la première vente au Voyage ;
- afficher avant validation le total, la fraîcheur et les prises sélectionnées ;
- préparer les prix régionaux sans les activer avant l'existence de plusieurs ports.

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
