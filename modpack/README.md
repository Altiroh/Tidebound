# Tidebound — Into the Deep (Devpack 0.16.0-alpha)

Ce dossier assemble le premier socle jouable du modpack : Tidebound, quêtes, exploration, construction,
stockage, ambiance et performances. Il reste volontairement plus petit que la cible finale de 50 à 80 mods.

## Import CurseForge

Compresser le contenu de ce dossier en gardant `manifest.json` à la racine, puis importer l'archive
comme profil personnalisé. Le manifeste installe :

- Minecraft `1.21.1` et NeoForge `21.1.249` ;
- FTB Library, Teams, Quests et Ultimine ;
- Create, JEI et Tom's Simple Storage ;
- JourneyMap, Waystones et Balm ;
- AmbientSounds, ItemPhysic et CreativeCore ;
- Sodium, Iris et ImmediatelyFast ;
- Architectury API, requise par le socle FTB.

Le JAR Tidebound n'est pas téléchargé par CurseForge. Après import du dossier seul, copier
`tidebound-0.16.0-alpha.jar` dans le dossier `mods` de l'instance.

Le workflow GitHub Actions automatise cette étape : son artifact contient
`Tidebound_Devpack_0.16.0-alpha.zip`, directement importable dans CurseForge, ainsi que le JAR seul.

CurseForge avertit que le JAR Tidebound et les fichiers de quêtes ne viennent pas encore de son catalogue.
Il faut cocher **I understand** puis choisir **All Files**. Le bouton **CurseForge Files Only** retire précisément
le mod Tidebound et son livre ; il ne doit pas être utilisé. Cet avertissement disparaîtra lorsque le mod aura sa
propre fiche CurseForge et pourra être référencé par un `projectID`.

Pour fabriquer la même archive localement après `./gradlew build` :

```bash
bash modpack/package.sh
```

## Réglages Tidebound

- Waystones ne consomme aucun niveau d'expérience grâce à `config/waystones-common.toml`.
- Iris apporte le menu des shaders, mais aucun shaderpack n'est imposé afin de rester compatible avec
  les petites configurations.
- JEI remplace NEI, qui n'est pas la branche moderne adaptée à Minecraft 1.21.1.
- Create sert aux quais et ateliers visuels ; il ne remplace jamais le navire ou l'économie Tidebound.

L'enchantement **Fast Smelting** n'est pas inclus dans cette version : aucun candidat NeoForge 1.21.1
vérifié ne correspondait précisément à cette demande. `FastFurnace` est un optimiseur interne de fours,
pas un enchantement de gameplay, et n'est donc pas présenté comme tel.

## Périmètre de test

Ce premier ensemble doit être lancé dans CurseForge avant d'ajouter d'autres mods cosy. Vérifier en
priorité le démarrage, la création d'un archipel, les contraptions Create, le réseau Tom's, la carte,
une téléportation sans perte d'XP, l'ambiance sonore, la physique des objets et un shader léger.

KubeJS n'est pas nécessaire pour tester le cœur. Lorsqu'il sera ajouté, le pont d'exemple se trouve
dans `core/examples/kubejs/server_scripts/tidebound_bridge.js`.

Le profil fournit déjà un livre FTB Quests bilingue sous `overrides/config/ftbquests/quests/` :

- groupe `Le Voyage` ;
- chapitre `Naufragé` ;
- chapitre `Premier port` ;
- 9 objectifs et 105 Tides de récompenses de démarrage ;
- aucune dépendance de quête imposant une progression linéaire.

Les contrats répétables ne sont pas dupliqués dans le livre : ils restent gérés par Tidebound Core.

La sélection exacte et ses responsabilités sont consignées dans `docs/modpack/TB-PACK-001.md`.
Le guide complet est `docs/TESTING.md`.
