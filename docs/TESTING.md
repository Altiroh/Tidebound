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

Le résultat attendu est `core/build/libs/tidebound-0.6.0-alpha.jar`.

## 3. Récupérer le JAR construit par GitHub

Chaque push et chaque pull request lance le workflow **Build Tidebound**. Dans GitHub :

1. ouvrir l'onglet **Actions** du dépôt ;
2. ouvrir le dernier workflow vert **Build Tidebound** ;
3. télécharger l'artifact `tidebound-core-<commit>` ;
4. extraire le JAR avant de le placer dans le dossier `mods` du profil.

Cela permet de tester sans environnement de développement local, une fois le premier workflow validé.

## 4. Profil de test minimal

Créer un profil séparé dans CurseForge, Prism Launcher ou Modrinth App :

1. Minecraft `1.21.1` ;
2. chargeur **NeoForge `21.1.249`** ;
3. ajouter `tidebound-0.6.0-alpha.jar` au dossier `mods` ;
4. lancer d'abord sans autre mod ;
5. créer un monde avec les commandes autorisées.

Le dossier `modpack/` contient aussi un manifeste CurseForge de développement qui installe les trois
mods FTB compatibles. Tidebound Core doit encore être ajouté manuellement, car il n'est pas publié sur
CurseForge.

## 5. Smoke test Tidebound Core

Créer l'intendant :

```mcfunction
/function tidebound:create_harbor_intendant
```

Tester le départ de secours :

1. se donner ou fabriquer une barque vanilla : `/give @s minecraft:oak_boat` ;
2. la poser à moins de huit blocs de l'intendant ;
3. cliquer l'intendant puis **ENREGISTRER LA BARQUE** ;
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

## 6. Ajouter FTB Quests

Installer dans le même profil, côté client et serveur :

- FTB Library `2101.1.35` ;
- FTB Teams `2101.1.11` ;
- FTB Quests `2101.1.34`.

Dans le monde de test :

```mcfunction
/ftbquests editing_mode true
/ftbquests open_book
```

Créer un chapitre **Paliers Tidebound**, puis une quête avec une tâche d'objet, par exemple un poisson.
Ajouter une récompense **Command** avec :

```mcfunction
/tidebound progression reward-once {p} ftb:first_catch 25
```

Configurer la récompense avec **Permission Level = 2**. Le placeholder correct de FTB Quests 1.21.1
est `{p}`. Le reçu `ftb:first_catch` doit être unique : même si la commande est rejouée, Tidebound ne
verse pas deux fois les 25 Tides.

Vérifier le résultat avec :

```mcfunction
/tidebound tide balance
```

FTB Quests sert ici de journal visuel et d'onboarding. Les contrats répétables restent dans les JSON
Tidebound et dans le tableau de l'intendant, où le serveur contrôle la livraison et le cooldown.

Quand le livre est prêt, copier le dossier généré `config/ftbquests/quests/` dans
`modpack/overrides/config/ftbquests/quests/`, puis le committer.

## 7. Informations à fournir en cas de bug

- le commit ou la version du JAR ;
- la liste exacte des mods et leurs versions ;
- `logs/latest.log` ;
- le fichier de `crash-reports/` si présent ;
- les étapes courtes pour reproduire ;
- préciser si le problème existe avec Tidebound Core seul.
