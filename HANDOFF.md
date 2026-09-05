# Transmission Tidebound

Dernière mise à jour : **5 septembre 2026**

Branche de référence : `main`

État importé : `TB-DESIGN-001` + `TB-QUEST-001` / `0.7.0-alpha`

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
- intendant de port et tableau de contrats cliquable dans le chat ;
- navire personnel physique basé sur `ChestBoat` ;
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
- tests Java autonomes du domaine.

Les détails et commandes sont dans `core/README.md`, les notes `core/TB-CORE-001.md` à
`core/TB-CORE-005B.md` et `docs/quests/TB-QUEST-001.md`.

## Ce qui n'est pas encore implémenté

Les points suivants sont des décisions ou besoins acceptés, mais ne doivent pas être présentés comme fonctionnels :

1. **Pêche Tidebound.** Les prises ne portent pas encore poids, qualité, fraîcheur, valeur ou mutation.
2. **Génération procédurale Tidebound.** Le mod ne génère pas encore les archipels, ports, épaves et îles de départ.
3. **Automatisation complète du livre.** Six objectifs utilisent provisoirement une case manuelle tant que les événements Core correspondants n'existent pas.
4. **Modules.** Sonar, treuil, projecteur et filet ne sont pas encore équipables.
5. **Interface de cale dédiée.** Le conteneur vanilla montre encore 27 cases ; le serveur rend le contenu des cases verrouillées au joueur au lieu de le supprimer.
6. **Aiguille animée du Compas.** Le clic droit donne déjà direction et distance ; le modèle animé pointant physiquement vers le navire reste une amélioration future.
7. **Validation en jeu.** Le workflow compile automatiquement ; un lancement manuel client/serveur et le smoke test de `docs/TESTING.md` restent indispensables.

## Prochaine tâche recommandée

### TB-FISH-001 — Modèle de prise Tidebound

À réaliser maintenant :

- définir les données d'une prise : espèce, poids, qualité, fraîcheur, origine et anomalie ;
- stocker ces données sur l'ItemStack sans créer un item par combinaison ;
- convertir la première capture vanilla en prise Tidebound ;
- calculer une valeur serveur déterministe et exposer l'affichage joueur ;
- fournir des tests de sérialisation, de valeur et de compatibilité des sauvegardes ;
- préparer les points d'intégration pour la vente au port et le Journal.

## Architecture et sources de vérité

- `core/src/main/java/dev/tidebound/core/data/` : modèles persistants ;
- `core/src/main/java/dev/tidebound/core/service/` : logique serveur ;
- `core/src/main/java/dev/tidebound/core/api/TideboundApi.java` : façade publique ;
- `core/src/main/java/dev/tidebound/core/event/` : événements NeoForge ;
- `core/src/main/resources/data/` : contenu datapack ;
- `core/src/test/` : tests autonomes ;
- `modpack/overrides/config/ftbquests/quests/` : livre FTB Quests versionné ;
- `docs/design/Tidebound_Lore.md` : lore, tonalité et progression canoniques ;
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
