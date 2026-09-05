# TB-NPC-001 — Rôles portuaires visuels

Version : `0.13.0-alpha`  
Statut : implémenté, compilation NeoForge et validation visuelle en attente de CI/client.

## Portée

- cinq entités dédiées : Intendant, charpentier naval, poissonnier, naturaliste et gardien de phare ;
- modèle conservant la silhouette familière du villageois vanilla ;
- cinq atlas UV issus du pack artistique Tidebound, réduits en 512×512 par voisin le plus proche,
  et cinq maquettes d'interface ;
- écran de l'Intendant limité à l'enregistrement, la navigation, le Compas et les contrats ;
- écran du charpentier autorisant construction, réparation et améliorations ;
- interfaces du poissonnier, du Naturaliste et du Gardien visibles mais sans mutation serveur fictive ;
- compatibilité temporaire avec les anciens villageois portant le tag `tidebound_contract_board`.

## Règles serveur

Les boutons ne déterminent jamais l'autorisation. Le serveur vérifie le rôle réellement proche :

- `INTENDANT` : enregistrer, réclamer/déployer, localiser et consulter les contrats ;
- `SHIPWRIGHT` : convertir une Barque de fortune, réparer et améliorer le navire Tidebound ;
- les autres rôles n'exécutent aucune action avant leur ticket métier.

Une barque vanilla demeure non améliorable dans les deux écrans.

## Test manuel

```mcfunction
/function tidebound:create_test_harbor
```

Vérifier les cinq skins, leur nom, leur interface et la séparation des actions. Tester ensuite une
Barque de fortune auprès de l'Intendant, puis sa conversion auprès du charpentier.
