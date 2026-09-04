# TB-CORE-001 — Persistance joueur

## Objectif

Créer une source de vérité serveur indépendante des mods de quêtes et de bateau pour les deux
éléments structurants du prototype : la monnaie **Tide** et le bateau personnel.

## Critères d'acceptation

| Critère | État | Preuve dans le projet |
|---|---:|---|
| Solde de Tides propre à chaque joueur | Fait | `TideWallet` + attachment `tide_wallet` |
| Solde sauvegardé avec le joueur | Fait | sérialisation `Codec` de l'attachment |
| Solde conservé après la mort | Fait | `copyOnDeath()` |
| Aucun solde négatif | Fait | invariants de `TideWallet` |
| Bateau propre à chaque joueur | Fait | `PlayerVessel` + attachment `player_vessel` |
| Déblocage unique et rejouable sans duplication | Fait | `VesselService.unlock` idempotent |
| Axes d'amélioration du bateau | Fait | coque, moteur, cale, emplacement de module |
| Accès futur pour contrats, succès et PNJ | Fait | façade `TideboundApi` |
| Commandes de contrôle en jeu | Fait | arbre `/tidebound` |
| Vérification automatisée du domaine | Prête | `DomainSelfTest`, relié à `gradle check` |
| Compilation du JAR dans cet environnement | Non exécutée | JDK 21 et Gradle absents de l'environnement |

## Hors périmètre volontaire

- modèle 3D et physique du bateau ;
- menus et HUD ;
- prix et paiement atomique des améliorations ;
- définitions JSON des succès et contrats ;
- protection anti-double-récompense par identifiant de reçu ;
- synchronisation client des données, inutile tant qu'aucun HUD ne les lit directement.

## Suite proposée

`TB-CORE-002` doit ajouter un registre de récompenses idempotentes et deux moteurs pilotés par JSON :
les paliers uniques et les contrats répétables. Il pourra ensuite exposer des commandes/fonctions
appelables depuis KubeJS, FTB Quests et Easy NPC.
