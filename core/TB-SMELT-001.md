# TB-SMELT-001 — Fonte instantanée

## Révision après playtest

La première version (« Fast Smelting ») accélérait la cuisson d'un four tant que le joueur le
surveillait, outil en main. Le retour de `TB-QA-001` a montré que ce nom laissait attendre un
mécanisme différent — cuire instantanément le minerai à l'extraction — et que l'implémentation
d'origine ne le faisait pas. Renommé `tidebound:instant_smelting` et réécrit pour correspondre
réellement à son nom : miner avec l'outil enchanté transforme directement la prise en son équivalent
fondu, sans passer par un four. L'ancienne mécanique (menu de four ouvert, accès à des champs
package-private via un access transformer) est entièrement retirée : plus simple et plus proche de
ce qui était attendu.

## Résultat

Ajoute l'enchantement `tidebound:instant_smelting` (« Fonte instantanée »), obtenable à la table
d'enchantement et à l'enclume sur les mêmes outils qu'Efficacité (`#minecraft:enchantable/mining`,
objet primaire : pioches). Niveau unique (comme Toucher de soie), incompatible avec Toucher de soie
et Fortune via le tag vanilla `#minecraft:exclusive_set/mining`.

## Mécanique

- Miner un bloc avec l'outil enchanté transforme chaque prise possédant une recette de fonderie
  vanilla en son résultat fondu (minerai → lingot, sable → verre, etc.), en conservant Fortune et les
  quantités.
- Aucun bloc n'est concerné en dehors d'une recette de fonderie réelle : ne court-circuite ni les
  mécanismes Create (qui ne sont pas des recettes de fonderie vanilla), ni les recettes propres à un
  autre mod.
- Contrairement à l'ancienne version, ceci fonctionne sur un simple bris de bloc — aucune dépendance
  à un four, à un menu ouvert ou à un tick périodique.

## Implémentation technique

- `dev.tidebound.core.registry.TideboundEnchantments.INSTANT_SMELTING` porte la
  `ResourceKey<Enchantment>`.
- `dev.tidebound.core.event.InstantSmeltingEvents` écoute `net.neoforged.neoforge.event.level.
  BlockDropsEvent` (API vérifiée dans les sources NeoForge 21.1.249 locales) : pour chaque
  `ItemEntity` dans `event.getDrops()`, cherche une recette `RecipeType.SMELTING` correspondante via
  `level.getRecipeManager().getRecipeFor(...)` et remplace l'item par son résultat fondu si l'outil
  (`event.getTool()`) porte l'enchantement.
- `data/tidebound/enchantment/instant_smelting.json` : niveau max 1, poids 2, `slots` main et
  off-hand, `effects: {}` (aucun composant vanilla, toute la logique est en Java).
- Plus besoin d'access transformer : `core/src/main/resources/META-INF/accesstransformer.cfg` est
  supprimé, il ne servait qu'à l'ancienne mécanique.

## Validation effectuée localement

- `./gradlew build` : compilation et build réussis contre les vraies classes NeoForge 21.1.249 /
  Minecraft 1.21.1, `DomainSelfTest: OK`.
- `python3 tools/validate_content.py` : `OK`.

## Validation requise

- lancement client/serveur réel : enchanter une pioche, miner un minerai, confirmer que le lingot
  (et non le minerai brut) est directement récupéré ;
- confirmer l'incompatibilité avec Toucher de soie et Fortune à l'enclume ;
- confirmer qu'aucun bloc sans recette de fonderie (pierre, bois...) n'est affecté.

## Suite

`TB-CORE-005C` : modules v1 branchés aux systèmes réels (projecteur, sonar, treuil, filet).
