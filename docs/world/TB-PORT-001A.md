# TB-PORT-001A — Planification des ports variables

Version : `0.15.0-alpha`  
Statut : fondation déterministe implémentée ; placement physique reporté à `TB-PORT-001B`.

## Archetypes

| Port | Poids | Services garantis |
| --- | ---: | --- |
| Hameau de pêche | 34 % | Poissonnier, contrats |
| Quai de chantier | 20 % | Charpentier, stockage |
| Avant-poste du phare | 16 % | Gardien de phare |
| Port marchand | 18 % | Intendant, poissonnier, contrats |
| Station de terrain | 12 % | Naturaliste, stockage |

Un service secondaire peut s'ajouter. Les mécanismes Create restent occasionnels et limités aux
ports où ils ont un sens visuel ou fonctionnel. Aucun port ne reçoit automatiquement les cinq PNJ.

## Déterminisme

Le plan dépend de la seed et d'une région de 512×512 blocs. Une même seed et une même région donnent
toujours le même `siteId`, le même archétype et les mêmes services. Le tirage séparé du port de départ
reste optionnel (environ une seed sur trois).

Diagnostic administrateur :

```mcfunction
/tidebound world port-plan
```

Cette commande ne place rien. `TB-PORT-001B` devra sélectionner un rivage viable, poser une structure
et invoquer uniquement les PNJ annoncés par ce plan.
