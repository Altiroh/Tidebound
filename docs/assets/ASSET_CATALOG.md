# Catalogue des assets Tidebound

Inventaire initial du pack artistique créé le 5 septembre 2026. Les sources restent canoniques ;
leur présence dans ce document ne signifie pas encore que l'objet ou le PNJ est implémenté.

## Intégrés au jeu

| Asset | Usage | État |
| --- | --- | --- |
| `tidebound_gui_intendant_port.png` | Habillage du menu de l'intendant | intégré dans `textures/gui/harbor_intendant.png` |
| `compas_de_sillage.png` | Texture du Compas de sillage | intégré dans `textures/item/wake_compass.png` |
| `kit_reparation.png` | Réparations au port | `tidebound:repair_kit` |
| `necessaire_calfatage.png` | Recette du kit de réparation | `tidebound:caulking_kit` |
| `pieces_moteur.png` | Améliorations de moteur | `tidebound:engine_parts` |
| `ferrures_cale.png` | Améliorations de cale | `tidebound:hold_fittings` |
| `plaque_coque.png` | Améliorations de coque | `tidebound:hull_plate` |
| `huile_mecanique.png` | Composant moteur futur | `tidebound:mechanical_oil` |

## Sources importées, conversion requise

| Asset | Usage prévu | Travail restant |
| --- | --- | --- |
| `tidebound_pnj_intendant_port.png` | PNJ d'intendance distinct | convertir la planche 1254×1254 en texture compatible avec le modèle retenu |

Une référence de travail optimisée est conservée sous
`docs/assets/source/harbor_intendant_skin_reference.png` et n'est pas embarquée dans le JAR. La source
originale 1254×1254 reste conservée avec le pack artistique.

## Pack identifié, à importer par ticket

- interfaces : intendant, charpentier/mécanicien, poissonnier, naturaliste/chercheur, gardien de phare ;
- PNJ : les cinq rôles correspondants ;
- poissons côtiers : maquereau, sardine, morue, hareng, bar, lieu jaune, rouget, anchois ;
- grands poissons : thon, bonite, espadon, dorade coryphène, flétan, merlu ;
- profondeurs et raretés : chauliodus, poisson-dragon, poisson-vipère, grenadier, poisson-ogre,
  anguille abyssale, coelacanthe, régalec, lune de mer, chimère, requin-lutin ;
- crustacés et prises marines : crabe, homard, crevette, langoustine, oursin, étoile de mer,
  méduse, concombre de mer ;
- variantes aberrantes de seize espèces ;
- navigation et modules : Compas de sillage, Carte des havres, longue-vue, balise de détresse,
  grappin, treuil, sonar, lanterne de pont ;
- maintenance : kit de réparation, nécessaire de calfatage, pièces moteur, ferrures de cale,
  plaque de coque, huile mécanique ;
- stations portuaires : tableau de contrats, poste d'intendant, établi de chantier naval,
  atelier de réparation, station de vente, table naturaliste, station d'analyse, vivier, aquarium,
  fumoir.

## Règle d'intégration

Une source est embarquée dans le mod uniquement lorsque son gameplay existe. Avant intégration :

1. vérifier transparence, dimensions et lisibilité à l'échelle GUI Minecraft ;
2. produire une texture finale sans texte figé lorsqu'elle doit être traduite ;
3. conserver le nom d'asset dans la documentation du ticket ;
4. vérifier le rendu en jeu avec plusieurs échelles d'interface.
