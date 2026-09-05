# Tidebound — Into the Deep (modpack de développement)

Ce dossier est le profil minimal en attendant l'assemblage complet du modpack.

## Import CurseForge

Compresser le contenu de ce dossier en gardant `manifest.json` à la racine, puis importer l'archive
comme profil personnalisé. Le manifeste installe :

- Minecraft `1.21.1` et NeoForge `21.1.249` ;
- Architectury API `13.0.11` ;
- FTB Library `2101.1.35` ;
- FTB Teams `2101.1.11` ;
- FTB Quests `2101.1.34`.

Le JAR Tidebound n'est pas téléchargé par CurseForge. Après import du dossier seul, copier
`tidebound-0.11.1-alpha.jar` dans le dossier `mods` de l'instance.

Le workflow GitHub Actions automatise cette étape : son artifact contient
`Tidebound_Devpack_0.11.1-alpha.zip`, directement importable dans CurseForge, ainsi que le JAR seul.

CurseForge avertit que le JAR Tidebound et les fichiers de quêtes ne viennent pas encore de son catalogue.
Il faut cocher **I understand** puis choisir **All Files**. Le bouton **CurseForge Files Only** retire précisément
le mod Tidebound et son livre ; il ne doit pas être utilisé. Cet avertissement disparaîtra lorsque le mod aura sa
propre fiche CurseForge et pourra être référencé par un `projectID`.

Pour fabriquer la même archive localement après `./gradlew build` :

```bash
bash modpack/package.sh
```

## Pourquoi ce pack reste petit

Cette première instance sert à valider la boucle Tidebound sans masquer les erreurs derrière des
dizaines de mods. Aquaculture, bateaux avancés, génération de monde, performances et ambiance seront
ajoutés par lots après le premier lancement réel.

KubeJS n'est pas nécessaire pour tester le cœur. Lorsqu'il sera ajouté, le pont d'exemple se trouve
dans `core/examples/kubejs/server_scripts/tidebound_bridge.js`.

Le profil fournit déjà un livre FTB Quests bilingue sous `overrides/config/ftbquests/quests/` :

- groupe `Le Voyage` ;
- chapitre `Naufragé` ;
- chapitre `Premier port` ;
- 9 objectifs et 105 Tides de récompenses de démarrage ;
- aucune dépendance de quête imposant une progression linéaire.

Les contrats répétables ne sont pas dupliqués dans le livre : ils restent gérés par Tidebound Core.

Le guide complet est `docs/TESTING.md`.
