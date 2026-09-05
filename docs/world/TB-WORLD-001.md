# TB-WORLD-001 — Archipel et île de départ

Version : `0.10.0-alpha`  
Statut : implémenté, validation multi-seed à poursuivre en jeu

## Résultat

Le monde normal de toute nouvelle sauvegarde Tidebound utilise désormais une topologie d'archipel.
Le même générateur est aussi exposé sous le preset `tidebound:archipelago`.

- le relief est piloté par un bruit horizontal dédié, comprimé pour produire des îles séparées ;
- le fond marin et la hauteur des îles restent continus entre les côtes ;
- les grottes, aquifères, minerais et règles de surface vanilla sont conservés ;
- les biomes sont limités à océan profond, océan, plage, plaine et forêt dans ce premier profil ;
- la recherche du spawn cible le cœur forestier d'une île ;
- le Nether et l'End restent vanilla.

Le fichier `data/minecraft/worldgen/world_preset/normal.json` remplace volontairement le preset normal
tant que Tidebound est installé. Le joueur n'a donc pas besoin de sélectionner manuellement un type de
monde. Cette modification ne transforme pas les chunks déjà générés et peut créer des raccords nets sur
une ancienne sauvegarde : la `0.10.0-alpha` doit être testée dans un monde neuf.

## Diagnostic

La commande administrateur suivante analyse le spawn partagé sur un rayon de 64 à 256 blocs :

```mcfunction
/tidebound world diagnose 128
```

Elle échantillonne la proportion de terre et d'eau, le nombre de rivages et la présence de troncs. Un
spawn est déclaré jouable lorsqu'il contient assez de terre, au moins 35 % d'eau, un rivage et du bois.
Une couverture terrestre supérieure à 55 % est signalée comme masse continentale.

`StarterPortPlan` réserve de manière déterministe environ une seed sur trois à un futur port initial.
`TB-PORT-001` consommera ce tirage ; aucun port n'est encore généré par ce ticket.

## Validation

- validation JSON et cohérence des références worldgen par `tools/validate_content.py` ;
- tests autonomes du verdict d'archipel et du tirage de port ;
- démarrage automatique d'un serveur avec création d'un monde neuf dans GitHub Actions ;
- matrice manuelle de 20 seeds documentée dans `docs/TESTING.md`.

La cible d'acceptation reste 20 spawns jouables sur 20, sans masse continentale dominante. Elle ne doit
être annoncée comme atteinte qu'après exécution réelle de cette matrice en jeu.
