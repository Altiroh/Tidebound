# TB-NAV-001 — Compas des Havres

Version : `0.17.0-alpha`  
Statut : registre persistant et localisation fonctionnelle implémentés.

## Deux instruments distincts

| Objet | Cible | Usage |
| --- | --- | --- |
| Compas des Havres | port enregistré contenant un Intendant | éviter qu'un tirage d'exploration bloque la progression |
| Compas de sillage | navire personnel du joueur | retrouver son embarcation ou sa dernière trace |

Le Compas des Havres lit un index sauvegardé sur l'Overworld. Il n'effectue aucun scan global d'entités
et ne force donc pas le chargement des chunks du port. Un clic droit affiche une direction cardinale et
une distance approximative ; à moins de 24 blocs, il confirme l'arrivée.

## Recette

La recette façonnée combine les six familles prévues par le lore : planches, cuivre, fer, redstone, or
et boussole vanilla. Elle n'est donc pas disponible pendant les toutes premières minutes.

L'icône utilise temporairement la lecture vanilla d'une boussole. Un asset propre au Compas des Havres
reste à produire ; l'asset existant `carte_des_havres` correspond à un autre futur objet et n'est pas
renommé artificiellement.

## Limites honnêtes

- seuls les ports déjà matérialisés sont indexés ;
- la génération automatique des îles habitées doit alimenter le même registre ;
- l'historique personnel des Intendants déjà découverts n'est pas encore suivi : la cible actuelle est
  le havre pertinent le plus proche.
