# Instructions de reprise pour Claude

Commencer par lire `HANDOFF.md`, puis `ROADMAP.md`, `docs/DECISIONS.md` et `core/README.md`.

## Règles du projet

- Tidebound est un sandbox procédural, pas un mode histoire.
- Le code cible Minecraft 1.21.1, NeoForge 21.1.249 et Java 21.
- La logique métier reste dans `core/` et derrière `TideboundApi`.
- FTB Quests, KubeJS, les PNJ et le chat sont des façades d'intégration.
- Ne pas remplacer la source de vérité serveur par des données uniquement côté client.
- Ne pas déclarer une fonctionnalité testée en jeu sans compilation et lancement réels.
- Préserver les sauvegardes et l'identité du navire lors de toute évolution d'entité ou de rendu.
- Ne pas introduire d'ordre narratif obligatoire entre les paliers.

## Travail courant

La prochaine unité de travail est `TB-CORE-005A`, décrite dans `HANDOFF.md`. L'objet de localisation et le démarrage sans intendant sont décidés mais pas encore codés.

Après toute modification matérielle, mettre à jour `HANDOFF.md`, `ROADMAP.md`, `CHANGELOG.md` et la note de ticket correspondante avant de committer.
