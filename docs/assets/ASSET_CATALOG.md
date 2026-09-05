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
| `tidebound_pnj_intendant_port.png` | Texture de l'Intendant du port | `tidebound:harbor_intendant` |
| `tidebound_pnj_charpentier_mecanicien.png` | Texture du charpentier naval | `tidebound:shipwright` |
| `tidebound_pnj_poissonnier.png` | Texture du poissonnier | `tidebound:fishmonger` |
| `tidebound_pnj_naturaliste_chercheur.png` | Texture du naturaliste | `tidebound:naturalist` |
| `tidebound_pnj_gardien_phare.png` | Texture du gardien de phare | `tidebound:lighthouse_keeper` |
| `tidebound_gui_charpentier_mecanicien.png` | Chantier naval visuel et actions d'amélioration | `textures/gui/harbor_shipwright.png` |
| `tidebound_gui_poissonnier.png` | Interface visuelle du poissonnier | raccord économique prévu par `TB-ECON-001` |
| `tidebound_gui_naturaliste_chercheur.png` | Interface visuelle du Journal | raccord encyclopédie/reliques à venir |
| `tidebound_gui_gardien_phare.png` | Interface visuelle du phare | raccord météo/rumeurs à venir |

## Sources importées, conversion requise

Les cinq atlas source 1254×1254 sont réduits par voisin le plus proche en textures runtime 512×512,
sans modifier leur découpage UV. Leur rendu à distance doit encore être contrôlé en jeu. Le validateur
contrôle désormais chaque chunk PNG jusqu'à `IEND`, afin qu'un transfert tronqué ne puisse plus passer.

## Pack identifié, à importer par ticket

- finitions d'interface : zones interactives, textes traduisibles et animation des cinq écrans ;
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
