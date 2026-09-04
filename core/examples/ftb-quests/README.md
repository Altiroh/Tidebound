# Pont FTB Quests → Tidebound

Utiliser une récompense de type **Command** exécutée par le serveur. Remplacer `{player}` par le
placeholder joueur proposé par la version de FTB Quests installée.

## Palier unique

```text
/tidebound progression milestone complete {player} tidebound:first_catch
```

## Récompense monétaire idempotente

Le reçu doit être unique pour la récompense concernée :

```text
/tidebound progression reward-once {player} ftb:welcome_reward 25
```

## XP de métier

```text
/tidebound progression skill grant {player} salvage 20
```

Ces commandes exigent le niveau de permission 2 afin qu'un joueur ne puisse pas se récompenser
lui-même depuis le chat. Les commandes joueur du tableau de contrats restent protégées par la
présence physique d'un intendant dans un rayon de huit blocs.
