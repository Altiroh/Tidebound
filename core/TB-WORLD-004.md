# TB-WORLD-004 — Falaises Abyssales (deuxième biome nommé)

## Contexte

Suite de `docs/design/Tidebound_Roadmap_Biomes.md` et de sa spécification détaillée
`docs/design/regions/falaises_abyssales.md`. Deuxième biome nommé implémenté après le Marais des
Lanternes (`TB-WORLD-003`), choisi car il recoupe directement des systèmes déjà en place
(`abyss_ocean`, tag `#tidebound:dangerous`, dégâts de coque de `TB-WORLD-002`).

Contrairement au Marais, l'identité de ce biome est d'abord une question de **relief** (une côte
abrupte), pas seulement de couleur — d'où un choix de biome vanilla de base différent
(`stony_shore`, rivage rocheux escarpé, plutôt que `beach`) et un carvage par **érosion** plutôt que
par humidité.

## Implémentation

1. **Biome `tidebound:abyssal_cliffs`** (`data/tidebound/worldgen/biome/abyssal_cliffs.json`) —
   clone exact de `minecraft:stony_shore` (extrait du jar client réel), seuls
   `water_color`/`water_fog_color` changés vers un bleu-ardoise très sombre (`1645603`/`658966`),
   cohérent avec la palette déjà posée pour `abyss_ocean` sans la dupliquer.
2. **Table de biomes** — les deux fichiers de preset : la cellule `beach` (post-Marais,
   `continentalness [0.05, 0.16]`, `humidity [-0.15, 0.7]`) est carvée par érosion plutôt que par
   humidité — `erosion [-1.0, -0.7]` (le quart le plus escarpé de la plage de valeurs, la même
   logique qui relie déjà érosion basse et relief accidenté dans `island_continentalness.json`) donne
   `tidebound:abyssal_cliffs`, le reste (`erosion [-0.7, 1.0]`) reste `beach`. Résultat : des falaises
   peuvent apparaître le long de n'importe quelle côte tempérée, pas seulement dans un climat donné.
3. **`data/tidebound/tags/worldgen/biome/dangerous.json`** — `tidebound:abyssal_cliffs` ajouté.
   Aucun code Java touché : `HullIntegrityEvents` ne dépend que de ce tag, déjà branché depuis
   `TB-WORLD-002`. Premier biome **terrestre** dangereux (jusqu'ici le tag ne couvrait que des
   biomes océaniques).

## Ce qui n'est pas dans cette tranche

- pêche profonde et silhouettes sous-marines mentionnées dans le lore/la roadmap pour ce biome :
  aucune des deux mécaniques n'existe (pas de notion de profondeur de pêche, pas d'entité de
  silhouette) — ticket dédié futur, pas deviné ici ;
- pas de distinction avec les futurs « Récifs du Verre Noir » (relief sous-marin, pas émergé) —
  notée dans la spécification pour ne pas les confondre au moment de leur implémentation.

## Vérification

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK` ;
- démarrage réel d'un serveur dédié sur un monde neuf généré avec la nouvelle table de biomes :
  `Done`, aucune erreur.

## Non vérifié

Comme pour tous les biomes ajoutés dans cette session, aucune vérification visuelle en jeu n'est
possible ici : relief réel généré par `stony_shore` le long des côtes, teinte de l'eau, et perception
du danger (dégâts de coque) à cet endroit précis restent à confirmer par l'utilisateur.
