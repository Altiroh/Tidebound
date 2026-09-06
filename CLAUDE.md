# Instructions de reprise pour Claude

Commencer par lire `HANDOFF.md`, puis `docs/design/Tidebound_Lore.md`, `ROADMAP.md`,
`docs/DECISIONS.md` et `core/README.md`.

## Règles du projet

- Tidebound est un sandbox procédural, pas un mode histoire. `Le Voyage` est un guide facultatif.
- Le code cible Minecraft 1.21.1, NeoForge 21.1.249 et Java 21.
- La logique métier reste dans `core/` et derrière `TideboundApi`.
- FTB Quests, KubeJS, les PNJ et le chat sont des façades d'intégration.
- Ne pas remplacer la source de vérité serveur par des données uniquement côté client.
- Ne pas déclarer une fonctionnalité testée en jeu sans compilation et lancement réels.
- Préserver les sauvegardes et l'identité du navire lors de toute évolution d'entité ou de rendu.
- Ne pas rendre les chapitres FTB Quests obligatoires pour accéder aux systèmes sandbox.
- Préserver la règle de ton : plus le joueur comprend la mer, moins elle paraît normale.

## Travail courant

La prochaine unité de travail est `TB-NPC-001`, décrite dans `HANDOFF.md`. Les prises persistantes,
leur fraîcheur et leur valeur sont implémentées dans `TB-FISH-001` sans remplacer les items vanilla.
La direction visuelle est désormais canonique : consulter `docs/assets/ASSET_CATALOG.md` avant de créer
un nouvel asset. Le navire Tidebound existe ; une barque vanilla reste une Barque de fortune et ne
doit jamais recevoir ses améliorations. Ne pas présenter les cinq PNJ dédiés comme implémentés avant
validation de `TB-NPC-001`.

Le preset normal est volontairement remplacé par l'archipel de `TB-WORLD-001`. Ne jamais annoncer la
matrice 20/20 comme validée sans avoir créé et diagnostiqué vingt mondes neufs. Tout changement de
density function nécessite aussi le smoke test serveur de la CI.

Après toute modification matérielle, mettre à jour `HANDOFF.md`, `ROADMAP.md`, `CHANGELOG.md` et la note de ticket correspondante avant de committer.
