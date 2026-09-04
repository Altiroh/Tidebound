# TB-CORE-004 — Navire personnel physique

## Résultat

Le `PlayerVessel` abstrait possède maintenant une incarnation dans le monde : un bateau-coffre
vanilla persistant, identifié et relié à un seul joueur.

La boucle de départ reste très rapide :

1. le joueur trouve un intendant de port ;
2. il clique sur **Réclamer la barque** et peut choisir son nom ;
3. il clique sur **Mettre à l'eau** ;
4. Tidebound cherche une surface d'eau libre dans un rayon de six blocs ;
5. le bateau-coffre apparaît avec son nom et ses niveaux visibles.

## Règles du navire

| Élément | Comportement |
|---|---|
| Propriété | UUID du capitaine et identité du `PlayerVessel` sur l'entité |
| Sauvegarde | lien de l'entité et dernière dimension/zone persistants |
| Doublon | l'ancienne zone est chargée et vérifiée avant un nouveau déploiement |
| Accès | les autres joueurs ne peuvent ni monter ni ouvrir la cale |
| Cale | inventaire vanilla de 27 emplacements immédiatement utilisable |
| Coque | récupération progressive des dégâts à partir du niveau 2 |
| Moteur | faible bonus de mouvement à partir du niveau 2 |
| Apparence | nom flottant avec niveaux Coque, Moteur, Cale et Modules |
| Suivi | dernière zone actualisée chaque seconde quand le capitaine navigue |

## Commandes joueur

```text
/tidebound vessel claim [nom]
/tidebound vessel deploy
/tidebound vessel locate
/tidebound vessel rename <nom>
/tidebound vessel inspect
```

La réclamation, le déploiement et le renommage nécessitent la proximité d'un intendant. La
localisation reste disponible en mer ou après une reconnexion.

## Choix d'architecture

Cette version emploie `ChestBoat` comme coque de référence au lieu de figer dès maintenant une
entité 3D propriétaire. Les données Tidebound ne dépendent pas de son rendu : un futur bateau de
Small Ships, Eureka ou une entité Tidebound peut reprendre le même `VesselEntityLink`.

## Limites assumées

- la cale possède encore les 27 cases du bateau-coffre à tous les niveaux ;
- aucun prix n'est encore demandé pour les améliorations administratives ;
- aucun module n'est encore équipable malgré les emplacements persistants ;
- le moteur apporte un bonus volontairement discret en attendant les tests en jeu ;
- la récupération d'un bateau détruit est automatique au prochain déploiement, mais sa cargaison
  suit les règles de destruction vanilla.

## Suite recommandée

`TB-CORE-005` doit rendre l'amélioration économique : prix en Tides et matériaux, réparations au
port, vraie cale progressive, modules (sonar, treuil, lumière, filet) et prérequis de métier.
