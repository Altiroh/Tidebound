# Modpack de développement Tidebound

Ce dossier est le profil minimal en attendant l'assemblage complet du modpack.

## Import CurseForge

Compresser le contenu de ce dossier en gardant `manifest.json` à la racine, puis importer l'archive
comme profil personnalisé. Le manifeste installe :

- Minecraft `1.21.1` et NeoForge `21.1.249` ;
- FTB Library `2101.1.35` ;
- FTB Teams `2101.1.11` ;
- FTB Quests `2101.1.34`.

Le JAR Tidebound n'est pas téléchargé par CurseForge. Après import, copier
`tidebound-0.6.0-alpha.jar` dans le dossier `mods` de l'instance.

## Pourquoi ce pack reste petit

Cette première instance sert à valider la boucle Tidebound sans masquer les erreurs derrière des
dizaines de mods. Aquaculture, bateaux avancés, génération de monde, performances et ambiance seront
ajoutés par lots après le premier lancement réel.

KubeJS n'est pas nécessaire pour tester le cœur. Lorsqu'il sera ajouté, le pont d'exemple se trouve
dans `core/examples/kubejs/server_scripts/tidebound_bridge.js`.

Le guide complet est `docs/TESTING.md`.
