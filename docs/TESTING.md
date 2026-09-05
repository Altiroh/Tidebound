# Tester Tidebound

Ce guide couvre le lancement développeur, le test dans un profil Minecraft séparé et le raccordement
minimal à FTB Quests. Toujours sauvegarder le monde avant de passer d'une alpha à la suivante.

## 1. Prérequis

- Minecraft Java Edition `1.21.1` ;
- NeoForge `21.1.249` ;
- JDK `21` pour compiler ou lancer le client de développement ;
- au moins 4 Go de mémoire disponibles pour le profil de test.

Vérifier Java dans PowerShell :

```powershell
java -version
```

La sortie doit annoncer Java 21. Le Gradle Wrapper est inclus : il n'est pas nécessaire d'installer
Gradle séparément.

## 2. Lancer directement depuis les sources

Sous Windows :

```powershell
git clone https://github.com/Altiroh/Tidebound.git
cd Tidebound\core
.\gradlew.bat runClient
```

Sous Linux ou macOS :

```bash
git clone https://github.com/Altiroh/Tidebound.git
cd Tidebound/core
./gradlew runClient
```

Le premier lancement télécharge Minecraft, NeoForge et les mappings. Pour produire uniquement le JAR :

```powershell
.\gradlew.bat build
```

Le résultat attendu est `core/build/libs/tidebound-0.10.0-alpha.jar`.

## 3. Récupérer le JAR construit par GitHub

Chaque push et chaque pull request lance le workflow **Build Tidebound**. Dans GitHub :

1. ouvrir l'onglet **Actions** du dépôt ;
2. ouvrir le dernier workflow vert **Build Tidebound** ;
3. télécharger l'artifact `tidebound-build-<commit>` ;
4. utiliser soit le JAR seul, soit `Tidebound_Devpack_0.10.0-alpha.zip` prêt à importer.

Cela permet de tester sans environnement de développement local, une fois le premier workflow validé.

## 4. Profil de test minimal

Créer un profil séparé dans CurseForge, Prism Launcher ou Modrinth App :

1. Minecraft `1.21.1` ;
2. chargeur **NeoForge `21.1.249`** ;
3. ajouter `tidebound-0.10.0-alpha.jar` au dossier `mods` ;
4. lancer d'abord sans autre mod ;
5. créer un monde avec les commandes autorisées.

Le dossier `modpack/` contient aussi un manifeste CurseForge de développement qui installe les trois
mods FTB compatibles. Dans l'archive produite par GitHub Actions, Tidebound Core est déjà placé dans
`overrides/mods` ; lors d'un import manuel du manifeste seul, il faut encore ajouter le JAR.

## 5. Tester l'archipel sur un monde neuf

Ne pas réutiliser un monde créé avant la `0.10.0-alpha`. Créer un monde normal : Tidebound remplace
automatiquement son relief par l'archipel. Le preset explicite **Tidebound — Archipel** peut aussi être
sélectionné dans les options avancées et doit produire le même résultat.

Avec les commandes autorisées, exécuter au spawn :

```mcfunction
/tidebound world diagnose 128
```

Le résultat attendu est `Spawn JOUABLE — archipel confirmé`, avec du bois et au moins un rivage. Vérifier
visuellement que le joueur apparaît sur une île, que l'océan est accessible sans creuser et qu'aucune
masse continentale ne remplit l'horizon proche.

Pour la matrice d'acceptation, créer vingt mondes avec les seeds `tidebound-01` à `tidebound-20`, lancer
le diagnostic dans chacun et noter : verdict, proportion de terre/eau, rivages, bois et tirage du port.
La cible est 20/20 spawns jouables et 0/20 masse continentale. Le tirage du port ne place encore aucune
structure : il prépare `TB-PORT-001`.

Le workflow GitHub lance aussi `tools/smoke_test_worldgen.sh`. Ce contrôle crée un monde par défaut et
attend que le serveur atteigne son état prêt, ce qui détecte les références ou codecs worldgen invalides.

## 6. Smoke test Tidebound Core

Créer l'intendant :

```mcfunction
/function tidebound:create_harbor_intendant
```

Ouvrir d'abord l'inventaire du joueur et vérifier l'icône d'ancre dans son coin supérieur droit. Elle
doit ouvrir le **Carnet de bord**, afficher six étapes visuelles et permettre d'ouvrir `Le Voyage`.

Cliquer ensuite sur l'intendant. Le menu graphique doit afficher le portrait, le solde de Tides, l'état
du navire et quatre jauges. Vérifier à plusieurs valeurs d'**Échelle de l'interface** que le menu reste
centré, cliquable et lisible.

Tester le départ de secours :

1. se donner ou fabriquer une barque vanilla : `/give @s minecraft:oak_boat` ;
2. la poser à moins de huit blocs de l'intendant ;
3. cliquer l'intendant puis utiliser le bouton **Enregistrer** ;
4. vérifier la réception du Compas de sillage ;
5. éloigner le bateau et utiliser le compas par clic droit.

Préparer le premier achat de coque :

```mcfunction
/tidebound tide grant @s 1000
/tidebound progression skill grant @s navigation 100
/give @s minecraft:oak_planks 16
```

Ramener le navire au quai, cliquer l'intendant, puis acheter **COQUE 2**. Vérifier ensuite :

- retrait de 120 Tides et de 16 planches ;
- refus si les Tides, matériaux ou le niveau sont insuffisants ;
- absence de second débit lors d'un double clic ;
- niveau visible dans le nom du navire.

Pour la cale 2 :

```mcfunction
/tidebound progression skill grant @s trade 100
/give @s minecraft:chest 2
```

La cale utilise 9 emplacements au niveau 1, 18 au niveau 2 et 27 à partir du niveau 3. Le menu vanilla
affiche provisoirement les 27 cases : tout objet placé dans une case verrouillée est rendu dans
l'inventaire, ou déposé près du navire si l'inventaire est plein.

Pour tester la réparation, frapper légèrement le navire, le laisser près de l'intendant et rouvrir le
tableau. Le bouton **RÉPARER LE NAVIRE** apparaît si des dégâts sont détectés.

## 7. Tester les prises Tidebound

Pêcher normalement avec une canne vanilla. Une morue, un saumon, un poisson tropical ou un
poisson-globe doit afficher immédiatement son poids, sa qualité, sa valeur estimée et le gain d'XP.
Les bâtons, bols et autres déchets ne doivent recevoir aucune donnée et ne valident pas la première prise.

Dans l'inventaire, survoler le poisson et vérifier :

- poids en grammes ;
- qualité colorée ;
- fraîcheur ;
- identifiant du biome d'origine ;
- valeur estimée en Tides ;
- anomalie uniquement sur une prise exceptionnellement rare.

Tenir ensuite le poisson dans la main principale :

```mcfunction
/tidebound catch inspect
```

La commande doit afficher les mêmes données. Conserver une prise pendant au moins 24 000 ticks ou
utiliser `/time add 24000` pour vérifier le passage de `fraîche` à `vieillissante` et la baisse de valeur.
Le poisson reste utilisable par les recettes et contrats vanilla, car son type d'item n'est pas remplacé.

## 8. Tester le livre FTB Quests fourni

Installer dans le même profil, côté client et serveur :

- FTB Library `2101.1.35` ;
- FTB Teams `2101.1.11` ;
- FTB Quests `2101.1.34`.

Le Devpack contient déjà le livre `Le Voyage`. Dans un monde neuf :

```mcfunction
/ftbquests open_book
```

Vérifier les chapitres **Naufragé** et **Premier port**. Tous les objectifs restent visibles et aucun ne
verrouille l'exploration. La première boucle de test est :

1. fabriquer une table de craft — validation d'objet automatique ;
2. obtenir une barque vanilla — case à cocher temporaire ;
3. obtenir une morue — validation d'objet automatique ;
4. atteindre ou créer un port — case à cocher temporaire ;
5. enregistrer le navire, recevoir le Compas de sillage et ouvrir le tableau de contrats ;
6. livrer un contrat puis améliorer ou réparer le navire.

Les objectifs automatiques versent leur récompense avec une commande de cette forme :

```mcfunction
/tidebound progression reward-once {p} ftb:first_catch 25
```

Le livre fourni utilise **Permission Level = 2**, le placeholder `{p}` et neuf reçus uniques. Même si
une commande est rejouée, Tidebound ne verse pas deux fois la récompense. Les deux chapitres distribuent
au maximum 105 Tides pour accélérer le départ sans financer toutes les améliorations.

Vérifier le résultat avec :

```mcfunction
/tidebound tide balance
```

FTB Quests sert de journal visuel et d'onboarding. Les cases manuelles sont provisoires jusqu'à l'ajout
de déclencheurs Core dédiés. Les contrats répétables restent dans les JSON Tidebound et dans le tableau
de l'intendant, où le serveur contrôle la livraison et le cooldown.

Pour modifier le livre, activer `/ftbquests editing_mode true`, éditer dans un monde jetable, puis recopier
le dossier généré `config/ftbquests/quests/` dans `modpack/overrides/config/ftbquests/quests/`. Exécuter
ensuite `python core/tools/validate_content.py` avant de committer.

Limitation actuelle : les ports ne sont pas encore générés. Créer l'intendant avec la fonction décrite
dans le smoke test pour valider le chapitre `Premier port`.

## 9. Informations à fournir en cas de bug

- le commit ou la version du JAR ;
- la liste exacte des mods et leurs versions ;
- `logs/latest.log` ;
- le fichier de `crash-reports/` si présent ;
- les étapes courtes pour reproduire ;
- préciser si le problème existe avec Tidebound Core seul.
