# Pont FTB Quests → Tidebound

FTB Quests fournit le livre, les chapitres et les tâches d'onboarding. Tidebound Core reste la source
de vérité des Tides, métiers et contrats répétables.

Versions de développement vérifiées pour Minecraft 1.21.1 / NeoForge :

- FTB Library `2101.1.35` ;
- FTB Teams `2101.1.11` ;
- FTB Quests `2101.1.34`.

Activer l'éditeur avec `/ftbquests editing_mode true`, puis ouvrir le livre avec
`/ftbquests open_book`. Utiliser une récompense de type **Command**, avec **Permission Level = 2**.
Le placeholder joueur de cette branche est `{p}` et non `{player}`.

## Palier unique

```text
/tidebound progression milestone complete {p} tidebound:first_catch
```

## Récompense monétaire idempotente

Le reçu doit être unique pour la récompense concernée :

```text
/tidebound progression reward-once {p} ftb:welcome_reward 25
```

## XP de métier

```text
/tidebound progression skill grant {p} salvage 20
```

Préférer `reward-once` pour les récompenses de livre : son reçu empêche tout double paiement. La
commande de palier est également idempotente, mais certains paliers intégrés sont déjà déclenchés
automatiquement par Tidebound.

Ne pas faire des contrats répétables des quêtes FTB obligatoires. Ils restent protégés par la présence
physique d'un intendant, la consommation serveur des denrées et leur cooldown propre.

Après édition, copier `config/ftbquests/quests/` dans
`modpack/overrides/config/ftbquests/quests/` pour l'intégrer au pack.
