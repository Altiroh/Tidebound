# Tidebound — Roadmap globale & nouveaux biomes

Statut : **document de cadrage, reçu de l'utilisateur le 7 septembre 2026**
Source : `Tidebound_Roadmap_Biomes.pdf`, converti et versionné ici pour rester la référence
consultable (le PDF original n'est pas suivi dans le dépôt).

Ce document complète `Tidebound_Lore.md` : il ne le remplace pas. Le lore fixe la philosophie
générale (îles-biomes vanilla-friendly, progression cosy → étrange, règles de design) ; ce document
détaille dix régions maritimes nommées, avec fonction de gameplay et promesse narrative propres à
chacune, plus une feuille de route de conception en neuf phases.

## Principes directeurs (reçus tels quels)

- Tidebound ne copie pas Dredge ; il en emprunte la sensation (pêche, progression du bateau,
  exploration, étrangeté croissante), pas ses mécaniques précises.
- Le cœur reste Minecraft : construction libre, bases, ports personnels, stockage, cuisine,
  exploration procédurale, boucle sandbox.
- Narration progressive : début cosy et rassurant, milieu exploratoire et maritime, fin étrange et
  lovecraftienne.
- Le repère « 100 jours » est un rythme, pas une contrainte stricte.
- Boussole : **plus le joueur comprend la mer, moins elle paraît normale** — déjà la boussole
  centrale de `Tidebound_Lore.md`.

## Boucle de jeu centrale

Pêcher et récupérer des ressources en mer → vendre/cuisiner/livrer/exposer → améliorer bateau,
matériel et port personnel → explorer plus loin, cartographier, débloquer de nouvelles régions →
découvrir anomalies, espèces rares, épaves, reliques → préparer des expéditions plus longues et plus
risquées.

## Ce qui rend Tidebound original (reçu tel quel)

- **Port personnel évolutif** — un vrai hub maritime, pas juste un décor, contrairement à Dredge.
- **Restauration du monde** — phares, pontons, ateliers, ports secondaires, routes maritimes
  réhabilitables (rejoint `HarborPlacementService` et l'idée de ports procéduraux améliorables).
- **Mer en 3 dimensions** — pêche de surface, pêche profonde, plongée, épaves, ruines sous-marines.
- **Progression non-linéaire** — plusieurs activités viables sans suivre Le Voyage.
- **Couche cosmique discrète** — révélation et anomalies plutôt qu'un boss « Cthulhu ».

## Roadmap de conception en 9 phases

| Phase | But | Livrables attendus |
|---|---|---|
| 1 — ADN du projet | Définir la fantasy et les limites | vision, piliers de gameplay, ton cosy → étrange, MVP / nice-to-have |
| 2 — Boucle de base | Rendre le début de partie satisfaisant | pêche côtière, vente, premier bateau, petit port, premiers contrats, économie simple |
| 3 — Progression maritime | Faire du bateau le niveau du joueur | paliers d'amélioration, modules, stockage, navigation, réparation, ravitaillement |
| 4 — Monde maritime | Donner des destinations fortes | biomes, ports, structures, routes, poissons par région, ressources |
| 5 — Contenu de milieu de partie | Créer de la variété sans casser le cosy | pêche nocturne, tempêtes, phares, épaves, plongée simple, ports secondaires |
| 6 — Étrangeté progressive | Introduire le mystère | aberrations, événements rares, anomalies météo, dialogues PNJ, variable de connaissance |
| 7 — Contenu profond / endgame | Ouvrir la couche lovecraftienne | fosses abyssales, reliques, cartes interdites, ruines, mer extérieure, grandes expéditions |
| 8 — Boucles longues | Prolonger la durée de vie | catalogue, records, musée, aquarium, restauration complète des ports, pêche légendaire |
| 9 — Implémentation | Transformer les idées en production | tableau feature → mod/datapack/KubeJS, quêtes, structures, loot tables, équilibrage |

**État réel du projet au 7 septembre 2026** (voir `ROADMAP.md`/`HANDOFF.md` pour le détail) : les
phases 1 à 3 sont largement couvertes par l'existant (Tides, métiers, contrats, navire modulaire,
paliers d'amélioration, Voyage FTB Quests). La phase 4 est entamée mais rudimentaire (`TB-WORLD-002` :
22 biomes vanilla par climat + 2 biomes à eau teintée, sans identité de région distincte). Les phases
5 à 9 ne sont pas commencées. Ce document sert de spécification pour la suite de la phase 4 et le
début de la phase 5.

## Les dix biomes proposés

Chaque biome combine identité visuelle, fonction de gameplay et promesse narrative — pas de
multiplication d'océans sans raison.

### Côte paisible
- **Fantasy** : zone de départ accueillante, petites plages, eau claire, jetées en bois, villages de pêcheurs.
- **Gameplay** : apprentissage de la pêche côtière, premiers poissons communs, premiers contrats, petit port personnel.
- **Rôle** : point de comparaison — plus tard, le joueur mesure à quel point les autres mers s'éloignent de cette normalité.

### Archipel des Brisants
- **Fantasy** : îlots rocheux, criques, colonies d'oiseaux, petits phares et récifs.
- **Gameplay** : exploration insulaire, navigation entre obstacles, ports secondaires, commerce léger.
- **Rôle** : sensation d'aventure à courte distance, cabotage.

### Marais des Lanternes
- **Fantasy** : eaux saumâtres, roseaux, passerelles, lueurs flottantes, brume basse, chenaux étroits.
- **Gameplay** : navigation lente et précise, pêche de nuit, accès conditionné par les marées, collecte de ressources organiques.
- **Rôle** : ambiance forte sans basculer dans l'horreur ouverte — premiers mystères.

### Récifs du Verre Noir
- **Fantasy** : récifs tranchants, coraux sombres, eau turquoise mais dangereuse, relief sous-marin dense.
- **Gameplay** : risque de collision, faune plus rare, plongée rentable, cartographie précise.
- **Rôle** : biome « technique » où la lecture du terrain compte.

### Plateau des Épaves
- **Fantasy** : ancienne route commerciale jonchée de coques, bouées cassées, mâts émergents, cargaisons perdues.
- **Gameplay** : exploration d'épaves, récupération de pièces mécaniques, journaux de bord, cartes au trésor.
- **Rôle** : biome de milieu de partie riche en boucle loot / réparation / amélioration. Rejoint
  directement les « gros navires échoués rares » déjà identifiés comme prochaine étape de
  `TB-WORLD-002` mais mis de côté faute de décision `.nbt`.

### Falaises Abyssales
- **Fantasy** : muraille noire plongeant dans le bleu profond, houle lourde, gouffres proches du rivage.
- **Gameplay** : introduction à la pêche profonde, silhouettes sous-marines, ressources rares.
- **Rôle** : transition entre le monde encore normal et les profondeurs inquiétantes.

### Mer Brumeuse
- **Fantasy** : nappe de brouillard perpétuel, cloches lointaines, repères effacés, lueurs diffuses.
- **Gameplay** : navigation à faible visibilité, importance des phares et du compas, rencontres rares, événements d'ambiance.
- **Rôle** : tension douce — pas de jumpscare, perte de repères.

### Mer Morte
- **Fantasy** : eau plate, absence d'oiseaux, presque aucun son, bancs de poissons rarissimes, horizon vide.
- **Gameplay** : zone pauvre en ressources courantes, recherche de poissons spéciaux, sentiment d'expédition lointaine.
- **Rôle** : biome psychologique — le calme devient inquiétant.

### Fosses Abyssales
- **Fantasy** : grandes déchirures océaniques, obscurité verticale, faune bioluminescente, ruines englouties.
- **Gameplay** : pêche abyssale, plongée avancée, reliques, aberrations et espèces rares.
- **Rôle** : cœur du mid/late game étrange, révélation de la couche lovecraftienne. Rejoint les
  sections 16-17 de `Tidebound_Lore.md` (Les abysses, Reliques abyssales et vitalité).

### Mer Extérieure
- **Fantasy** : océan colossal, cieux immenses, météo instable, îles presque mythiques, sentiment de bord du monde.
- **Gameplay** : grandes expéditions, endgame narratif, poissons légendaires, structures majeures.
- **Rôle** : ultime horizon de l'aventure plutôt que « zone du boss final ».

## Matrice de progression

| Biome | Moment conseillé | Tonalité | Mécanique dominante | Récompense type |
|---|---|---|---|---|
| Côte paisible | Début | Cosy | pêche / installation | argent, nourriture, base |
| Archipel des Brisants | Début / milieu | Aventure maritime | exploration d'îles | routes, commerce, petits hubs |
| Marais des Lanternes | Début / milieu | Étrange doux | chenaux / marées / nuit | espèces nocturnes, ambiance |
| Récifs du Verre Noir | Milieu | Technique | lecture du terrain / plongée | poissons rares, ressources |
| Plateau des Épaves | Milieu | Aventure / récupération | loot d'épaves | pièces, cartes, journaux |
| Falaises Abyssales | Milieu | Mystère | pêche profonde | premiers grands secrets |
| Mer Brumeuse | Milieu / avancé | Tension | navigation à vue réduite | rencontres, anomalies |
| Mer Morte | Avancé | Malaise | longue traversée | poissons spéciaux, narration |
| Fosses Abyssales | Avancé | Lovecraftien | abysses / reliques | aberrations, artefacts |
| Mer Extérieure | Endgame | Sublime cosmique | expédition totale | révélation, contenu final |

## Priorisation recommandée

- **MVP** : Côte paisible, Archipel des Brisants, Marais des Lanternes, Falaises Abyssales, Plateau des Épaves.
- **Extension / V1.5** : Récifs du Verre Noir, Mer Brumeuse, Fosses Abyssales.
- **Endgame / contenu majeur** : Mer Morte, Mer Extérieure.

La Côte paisible et une bonne part des Falaises Abyssales (dangerosité liée au niveau de coque) sont
déjà partiellement posées par `TB-WORLD-002`. L'Archipel des Brisants recoupe le système de ports
régionaux déjà en place. Marais des Lanternes et Plateau des Épaves sont les deux ajouts MVP
entièrement nouveaux.

**État d'implémentation (mis à jour au fil des tickets)** : les cinq biomes MVP sont tous traités.
Marais des Lanternes (`TB-WORLD-003`) ; Falaises Abyssales (`TB-WORLD-004`) ; Plateau des Épaves en
premier passage procédural, sans `.nbt` (`TB-WORLD-005`) ; Archipel des Brisants, qui ne demandait
presque aucun code neuf (`TB-WORLD-006`). Le palier Extension/V1.5 est également traité (couche
biome/danger uniquement, gameplay dédié laissé à des tickets futurs) : Récifs du Verre Noir, Mer
Brumeuse, Fosses Abyssales (`TB-WORLD-007`, `docs/design/regions/recifs_du_verre_noir.md`,
`mer_brumeuse.md`, `fosses_abyssales.md`). Palier Endgame : Mer Morte implémentée
(`TB-WORLD-008`, `docs/design/regions/mer_morte.md`) ; Mer Extérieure **délibérément différée**
(`docs/design/regions/mer_exterieure.md`) — aucun des trois piliers de sa fantasy (poissons
légendaires, structures majeures, météo instable) n'a de système existant à réutiliser, un simple
clone de biome serait une coquille vide.

## Sorties demandées par ce document (à produire dans une prochaine session)

1. tableaux biome → poissons / ressources / structures / événements ;
2. roadmap de production réelle (mods, datapacks, KubeJS, quêtes) ;
3. progression du bateau et de ses modules ;
4. contrats PNJ par région ;
5. version MVP vs version idéale ;
6. propositions d'équilibrage économique et de rythme de découverte.

Vu l'ampleur (dix biomes détaillés), ces sorties seront produites région par région plutôt qu'en un
seul document, en commençant par les cinq biomes MVP.
