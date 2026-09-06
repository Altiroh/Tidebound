# TB-CORE-007 — Corrections issues du playtest (TB-QA-001)

## Contexte

Première vraie partie jouée sur le Devpack. Ce ticket couvre les retours qui étaient directement
actionnables sans nouvelle décision de design ; la variété de biomes/ports automatiques/gros navires
échoués est un chantier séparé (`TB-WORLD-002`), trop large pour être mêlé ici.

## Corrections

- **Instant Smelting** : voir `TB-SMELT-001.md`, révisé en profondeur (renommage et changement de
  mécanique, l'ancienne ne correspondait pas à ce que le nom laissait attendre).
- **Onglet créatif dédié** (`dev.tidebound.core.registry.TideboundCreativeTabs`) : tous les objets
  Tidebound sont regroupés dans un onglet « Tidebound », retirés de l'onglet vanilla Outils.
- **Solde de Tides dans l'inventaire** : le bouton qui ouvrait le carnet de bord affichait une icône
  d'ancre seule ; il affiche désormais le solde de Tides du joueur (`⚓ <solde> Tides`) et ouvre
  toujours le carnet au clic. Nécessite que `tidebound:tide_wallet` soit synchronisé au client
  (`AttachmentType.Builder#sync`, ajouté dans `TideboundAttachments`) — jusqu'ici uniquement
  persistant côté serveur.
- **Annonce de biome** (`dev.tidebound.core.event.BiomeAwarenessEvents`) : un message apparaît à
  chaque changement de biome, en rouge si le biome porte le tag `#tidebound:dangerous` (vide pour
  l'instant — la variété de biomes n'existe pas encore, `TB-WORLD-002` le peuplera).
- **Icône du Compas de sillage** : le modèle pointait vers `minecraft:item/compass_00` au lieu de sa
  propre texture `tidebound:item/wake_compass` déjà présente dans le pack — bug de câblage, pas un
  problème d'asset. Corrigé.
- **Lootr** ajouté au Devpack : loot indépendant par joueur sur coffres, tonneaux et minecarts.
- **FTB Quests Translator** ajouté au Devpack : traduction du livre à la demande, indépendante de la
  langue du jeu (le réglage `fallback_locale: fr_fr` existant ne fait que suivre la langue du client,
  ce n'est pas la même chose qu'un choix indépendant).

## Explicitement pas un bug

- **Port et PNJ introuvables en jeu** : attendu à ce stade. Le port initial n'est réservé que par
  seed (~1/3 de chance) et n'est jamais matérialisé automatiquement — il faut
  `/tidebound world port-place` en administrateur près d'un rivage pour le faire apparaître
  (voir `core/README.md` § Génération de l'archipel). L'automatisation de ce placement fait partie
  de `TB-WORLD-002`.

## Icônes remplacées

Les huit icônes jugées trop détaillées (`wake_compass`, `haven_compass`, `repair_kit`,
`caulking_kit`, `engine_parts`, `hold_fittings`, `hull_plate`, `mechanical_oil`) ont été remplacées
par de nouveaux visuels fournis par l'utilisateur, réduits de 1254×1254 à 32×32 pour se rapprocher de
l'échelle vanilla (voir `docs/assets/ASSET_CATALOG.md`). Le Compas des Havres a maintenant sa propre
texture et sa propre silhouette, distincte du Compas de sillage et du compas vanilla — son modèle
pointait par erreur vers `minecraft:item/compass_00`, corrigé au passage. Un nouveau logo
(`icon_tidebound.png`, ancre/tentacule/lanterne) remplace `tidebound_logo.png` (écran Mods).

## Validation effectuée localement

- `./gradlew build` : compilation et build réussis contre les vraies classes NeoForge 21.1.249 /
  Minecraft 1.21.1, `DomainSelfTest: OK`.
- `python3 tools/validate_content.py` : `OK`.

## Validation requise

- lancement client réel : vérifier l'onglet créatif, le solde de Tides dans l'inventaire, les
  nouvelles icônes (les deux compas, les six composants) et le message de changement de biome ;
- confirmer que Lootr donne bien un loot séparé par joueur sur un coffre de test ;
- confirmer que FTB Quests Translator fonctionne avec le livre Tidebound.

## Suite

`TB-WORLD-002` : variété de biomes maritimes, ports/PNJ placés automatiquement, gros navires échoués
rares, zones bornées par le niveau du navire.
