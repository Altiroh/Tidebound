# TB-WORLD-002 — Monde et ports (en cours)

## Contexte

Retour direct de `TB-QA-001` : le joueur n'a trouvé ni port ni PNJ en jeu, ce qui bloque le test des
cinq PNJ portuaires et de leurs interfaces. Ticket large (variété de biomes, teintes/profondeurs
d'eau, zones bornées par le niveau du navire, gros navires échoués rares, placement automatique des
ports) ; ce document suit son avancement tranche par tranche plutôt qu'en un seul commit.

Décision prise avec l'utilisateur : pas de structures `.nbt` externes pour l'instant (aucune source
fiable identifiée sans risque de licence) — le système procédural actuel (`HarborPlacementService`)
est conservé, à réévaluer plus tard si besoin.

## Tranche 1 — Placement automatique des ports (fait)

**1a.** `dev.tidebound.core.event.StarterPortEvents` écoute `ServerStartedEvent` et matérialise le
port réservé par seed (`PortPlan.starter`, tirage ~1/3) près du spawn dès le premier démarrage.

**1b.** Un seul port de départ ne suffisait pas : l'utilisateur veut pouvoir en retrouver un sans
avoir à parcourir 2000 blocs, quitte à ce que ça reste une question de chance plutôt qu'une garantie
rigide. `dev.tidebound.core.event.RegionalPortEvents` généralise le principe : chaque région de 512
blocs a ~1/3 de chance de contenir un port réel, vérifié pendant que les joueurs explorent (toutes les
30 s, étalé par joueur). Sur plusieurs régions parcourues, ça place statistiquement le port le plus
proche entre 500 et 1000 blocs. `HarborRegistry.contains(long)` évite de retenter une région déjà
matérialisée ; `HarborPlacementService.placeNear` reste la même logique de construction/idempotence
que la commande manuelle `/tidebound world port-place`, toujours disponible.

Limite connue : la recherche de rivage reste bornée à 48 blocs autour du joueur/spawn. Une région
« gagnante » sans rivage exploitable à portée ne produit rien, silencieusement, et sera retentée à
chaque intervalle tant qu'elle reste non enregistrée — coût négligeable, pas d'optimisation prévue.

## Tranche 2 — Variété de biomes et zones à risque (fait, validation en jeu requise)

**2a. Climat.** Le sélecteur de biomes (`multi_noise`) ne variait que par continentalité (5 entrées
fixes) ; température, humidité, érosion et bizarrerie étaient toutes grand ouvertes. Chaque bande de
continentalité est désormais également découpée par température (5 paliers : glacial, froid,
tempéré, chaud, torride), avec uniquement des biomes vanilla :

- profondeurs : `deep_frozen_ocean`, `deep_cold_ocean`, `deep_ocean`, `deep_lukewarm_ocean` (pas de
  variante chaude profonde en vanilla) ;
- océan : `frozen_ocean`, `cold_ocean`, `ocean`, `lukewarm_ocean`, `warm_ocean` ;
- plage : `snowy_beach` (froid) ou `beach` (le reste) ;
- palier « plaine » : `snowy_plains`, `plains`, `savanna`, `desert` ;
- palier « forêt » : `snowy_taiga`, `taiga`, `forest`, `birch_forest`, `jungle`.

25 biomes au lieu de 5, zéro nouvel asset. Effet de bord assumé : le spawn (toujours ciblé sur la
bande de continentalité la plus haute) peut désormais tomber sur n'importe lequel des cinq biomes du
palier « forêt » selon la température locale, pas systématiquement `forest` — tous fournissent du
bois, donc la boucle bois → barque reste garantie ; seule l'ambiance du tout premier écran change.

**2b. Eaux teintées personnalisées.** Deux biomes Tidebound (uniquement des couleurs, aucun nouvel
asset visuel) :
- `tidebound:violet_shallows` — variante de `ocean` à eau violette, poche rare dans la bande d'océan
  tempéré (humidité > 0,7, l'entrée `ocean` vanilla est resserrée à humidité ≤ 0,7 pour éviter tout
  chevauchement) ;
- `tidebound:abyss_ocean` — variante de `deep_ocean` à eau bleu très sombre, même principe de poche
  rare dans la bande profonde tempérée. Ajouté au tag `#tidebound:dangerous`.

**2c. Zones à risque.** Le tag `#tidebound:dangerous` (vide depuis `TB-CORE-007`) est peuplé :
`deep_frozen_ocean`, `deep_cold_ocean`, `deep_ocean`, `deep_lukewarm_ocean`, `frozen_ocean`,
`tidebound:abyss_ocean`. `dev.tidebound.core.event.HullIntegrityEvents` inflige des dégâts périodiques
(2 tous les ~2 s) au bateau d'un joueur qui navigue dedans avec `hullTier < 3` — la Barque de fortune
(bloquée en tier 1) comme un navire Tidebound pas encore assez amélioré. L'annonce rouge de
`BiomeAwarenessEvents` prévient déjà à l'entrée ; ceci en est la conséquence mécanique.

### Ce qui reste explicitement hors de cette tranche

- gros navires échoués rares : nécessiterait soit une structure `.nbt` (écarté pour l'instant), soit
  un générateur procédural dédié — pas commencé ;
- variété de profondeur au-delà du découpage océan/océan profond existant : non touché, risque de
  casser la génération de terrain sans pouvoir le vérifier visuellement ;
- humidité/érosion comme axes de variété supplémentaires (mangrove, badlands, etc.) : pas fait,
  peut suivre le même principe que 2a si voulu.

### Validation effectuée localement

- `./gradlew build` : compilation et build réussis, `DomainSelfTest: OK`, `validate_content.py: OK`.
- lancement d'un serveur réel sur un monde neuf après chaque changement (placement de port, puis
  biomes/tag/dégâts) : démarrage propre jusqu'à `Done`, aucune exception, y compris avec les deux
  biomes personnalisés dans le registre.
- Limite assumée : un démarrage serveur sans exception confirme que le JSON est valide et que les
  biomes se résolvent, pas que le partitionnement humidité choisi produit visuellement l'effet de
  poche rare voulu, ni que les teintes rendent bien en jeu — non vérifiable sans client graphique.

### Validation requise

- confirmer en jeu qu'un port apparaît dans un rayon raisonnable (idéalement < 1000 blocs) sur
  plusieurs seeds, et que ses PNJ sont interactifs ;
- explorer plusieurs seeds pour confirmer la variété de biomes (types d'îles, teintes d'océan) et
  repérer si `violet_shallows`/`abyss_ocean` apparaissent avec une rareté raisonnable ;
- confirmer que naviguer en eaux profondes/glacées avec un hull < 3 endommage bien le bateau, et
  qu'un hull ≥ 3 protège correctement ;
- vérifier que le spawn reste toujours jouable (bois accessible) quel que soit le biome tiré.

## Suite

Shaders et devpack traités séparément (voir `CHANGELOG.md`). Prochain morceau de ce ticket si repris :
gros navires échoués et/ou variété d'humidité.
