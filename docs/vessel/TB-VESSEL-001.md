# TB-VESSEL-001 — Navire Tidebound modulaire

Version : `0.11.1-alpha`
État : implémenté, compilation CI et test manuel en jeu requis

## Résultat

Le navire personnel n'est plus matérialisé par une simple barque vanilla. `tidebound:vessel` est un type
d'entité distinct, sauvegardé et suivi par le serveur. Il conserve la physique nautique et le conteneur fiables de
`ChestBoat`, mais expose son propre rendu voxel, quatre places et un état visuel synchronisé.

| Progression | Changement visible |
| --- | --- |
| Coque 2+ | renforts de cuivre sur les flancs |
| Coque 4+ | bois sombre et plaques de cuivre plus nettes |
| Moteur 2+ | moteur à l'arrière, renforcé au niveau 4 |
| Cale 2+ | compartiment fermé sur le pont |
| Modules 1–4 | un support lumineux par emplacement disponible |

Le rendu emploie temporairement des textures de blocs Minecraft. Cette géométrie est volontairement isolée dans
`TideboundVesselRenderer` : un modèle Blockbench final pourra la remplacer sans toucher aux données, aux sauvegardes
ou à la logique du navire.

## Migration

- une barque vanilla nouvellement enregistrée reste une **Barque de fortune**, localisable mais non améliorable ;
- le chantier naval est la transition explicite vers le véritable navire Tidebound ;
- `/tidebound vessel refit` offre le même chemin de secours ;
- UUID métier `PlayerVessel`, propriétaire, nom, position, rotation, mouvement, dégâts, contenu de cale et passagers
  sont conservés ;
- l'UUID de l'entité physique change et `VesselDeployment` est mis à jour atomiquement.

## Limites assumées

- le modèle voxel est une première silhouette jouable, pas l'asset final ;
- la physique reste dérivée de `ChestBoat` pour éviter un moteur nautique fragile à ce stade ;
- les supports de modules sont visibles mais leurs fonctions arriveront avec `TB-CORE-005C` ;
- l'interface de cale dédiée n'est pas incluse dans ce ticket.

## Vérification

Les données de rendu sont couvertes par `DomainSelfTest`. Le validateur contrôle les ressources. GitHub Actions doit
encore confirmer la compilation Java 21/NeoForge, puis un test multijoueur manuel doit vérifier les quatre sièges,
la migration d'une cale remplie et la lisibilité de la silhouette sous plusieurs angles.
