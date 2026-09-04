# Transmission Tidebound

Dernière mise à jour : **4 septembre 2026**

Branche de référence : `main`

État importé : `TB-CORE-005A` / `0.5.0-alpha`

Ce fichier est la porte d'entrée pour reprendre le projet. Il doit être actualisé après chaque ticket terminé, même si les notes techniques détaillées existent ailleurs.

## Vision à préserver

Tidebound est une aventure Minecraft maritime **sandbox et procédurale**, pas une campagne scénarisée. Le monde, les îles et les ports varient selon la seed. Les quêtes prennent deux formes :

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
- tests Java autonomes du domaine.

Les détails et commandes sont dans `core/README.md` et `core/TB-CORE-001.md` à `core/TB-CORE-005A.md`.

## Ce qui n'est pas encore implémenté

Les points suivants sont des décisions ou besoins acceptés, mais ne doivent pas être présentés comme fonctionnels :

1. **Économie des améliorations.** Les coûts en Tides et matériaux, les réparations et les prérequis de métier restent à coder.
2. **Cale progressive.** Les 27 emplacements vanilla sont encore disponibles à tous les niveaux.
3. **Modules.** Sonar, treuil, projecteur et filet ne sont pas encore équipables.
4. **Aiguille animée du Compas.** Le clic droit donne déjà direction et distance ; le modèle animé pointant physiquement vers le navire reste une amélioration future.
5. **Génération procédurale Tidebound.** Le mod ne génère pas encore les archipels, ports, épaves et îles de départ.
6. **Compilation en jeu.** Le mod complet n'a pas encore été compilé ni lancé dans un client NeoForge 1.21.1.

## Prochaine tâche recommandée

### TB-CORE-005B — Économie et entretien du navire

À réaliser maintenant :

- définir les coûts par niveau de coque, moteur, cale et emplacement de module ;
- débiter atomiquement Tides et matériaux ;
- ajouter la réparation au port avec un prix dépendant des dégâts ;
- poser les premiers prérequis de métiers ;
- rendre la capacité de cale progressive sans perdre les objets existants ;
- exposer ces services dans la capitainerie et dans `TideboundApi` ;
- tester les refus, remboursements et doubles clics.

Ensuite, poursuivre avec `TB-CORE-005C` : projecteur, sonar, treuil et filet.

## Architecture et sources de vérité

- `core/src/main/java/dev/tidebound/core/data/` : modèles persistants ;
- `core/src/main/java/dev/tidebound/core/service/` : logique serveur ;
- `core/src/main/java/dev/tidebound/core/api/TideboundApi.java` : façade publique ;
- `core/src/main/java/dev/tidebound/core/event/` : événements NeoForge ;
- `core/src/main/resources/data/` : contenu datapack ;
- `core/src/test/` : tests autonomes ;
- `docs/design/Tidebound_GDD_Prototype_v0.3.docx` : conception produit ;
- `docs/technical/Tidebound_Matrice_Technique_v0.1.xlsx` : choix des mods et versions.

Ne pas faire dépendre la logique métier de FTB Quests, KubeJS ou d'un PNJ précis. Ces éléments consomment l'API Tidebound ; ils ne sont pas la source de vérité.

## Vérification attendue

Le projet cible Java 21. Exécuter, depuis `core/` :

```bash
python tools/validate_content.py
gradle check
gradle build
```

Si Gradle n'est pas installé, installer un JDK 21 et générer le wrapper avec `gradle wrapper`. Ne jamais annoncer que le JAR fonctionne en jeu avant un vrai lancement client/serveur NeoForge.

## Règle de transmission

Après chaque ticket :

1. mettre à jour la version dans `gradle.properties`, les métadonnées et la documentation si nécessaire ;
2. ajouter une note `core/TB-CORE-XXX.md` ;
3. déplacer la tâche courante dans la section « terminé » de `ROADMAP.md` ;
4. mettre à jour ce fichier avec l'état réel et la prochaine tâche ;
5. compléter `CHANGELOG.md` ;
6. exécuter les validations possibles et documenter explicitement celles qui restent impossibles ;
7. créer un commit Git ciblé et lisible.
