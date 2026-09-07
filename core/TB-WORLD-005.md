# TB-WORLD-005 — Plateau des Épaves (troisième biome nommé)

## Contexte

Troisième biome nommé de `docs/design/Tidebound_Roadmap_Biomes.md`. Spécification détaillée dans
`docs/design/regions/plateau_des_epaves.md`. Répond à la question laissée en suspens depuis
`TB-WORLD-002` sur les « gros navires échoués rares » : procédural, toujours pas de `.nbt`, cohérent
avec la décision déjà prise dans ce projet.

## Résumé de l'implémentation

- `dev.tidebound.core.world.WreckPlan` — identité déterministe par région, indépendante des ports ;
- `dev.tidebound.core.world.WreckRegistry` — index `SavedData` des sites déjà matérialisés ;
- `dev.tidebound.core.service.WreckPlacementService` — trouve un fond marin exploitable (biome
  océanique, profondeur ≥ 4 blocs) dans un rayon de 64 blocs, construit une coque procédurale bornée
  (planches partiellement manquantes, murs irréguliers, écoutille, mât brisé) et un tonneau relié à
  une vraie table de butin Minecraft (`RandomizableContainer.setBlockEntityLootTable`, jamais utilisée
  dans ce projet avant cette tranche) ;
- `dev.tidebound.core.event.WreckPlacementEvents` — même cadence que `RegionalPortEvents`
  (vérification toutes les 900 ticks par joueur), ~1 chance sur 5 par région de 512 blocs ;
- `data/tidebound/loot_table/chests/shipwreck_plateau.json` — ambiance vanilla + pièces de
  maintenance Tidebound déjà existantes, leur donnant une seconde voie d'acquisition hors recette.

Enregistré dans `TideboundCore.java` aux côtés de `RegionalPortEvents`.

## Vérification

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK` (nouvelle table de butin comptée dans les fichiers de
  ressources) ;
- démarrage réel d'un serveur dédié sur un monde neuf : `Done`, aucune erreur — confirme en particulier
  que la table de butin est syntaxiquement valide pour le vrai chargeur de datapack Minecraft (pas
  seulement un JSON bien formé), puisqu'un `/reload` implicite au démarrage validerait toute table de
  butin malformée.

## Non vérifié

Le code de construction de la coque (`WreckPlacementService.buildWreck`) n'a jamais été exécuté par
un vrai joueur dans cette session — la seule commande de test existante pour ce genre de placement
(`/tidebound world port-place`) exige un joueur réellement connecté, qu'aucun outil disponible ici ne
permet de simuler. Même limite que `HarborPlacementService.buildHut` en son temps : compilation et
datapack propres, mais rendu réel, position exacte sur le fond marin et apparence du butin restent à
confirmer par l'utilisateur.
