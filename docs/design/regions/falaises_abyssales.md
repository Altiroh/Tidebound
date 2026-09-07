# Falaises Abyssales — spécification de région

Statut : **conception**, deuxième biome nommé de `docs/design/Tidebound_Roadmap_Biomes.md` à être
implémenté. Choisi après le Marais des Lanternes car c'est celui qui recoupe le plus directement des
systèmes déjà en place (`TB-WORLD-002` : `abyss_ocean`, tag `#tidebound:dangerous`, dégâts de coque).

## Identité

Muraille noire plongeant directement dans le bleu profond, houle lourde, gouffres proches du rivage.
Transition entre le monde encore normal et les profondeurs inquiétantes — introduction à la pêche
profonde, silhouettes sous-marines, ressources rares.

## Différence avec `Récifs du Verre Noir`

Les deux biomes partagent une tonalité « danger technique », mais leur nature diffère : les Récifs
sont un relief **sous-marin** (plongée, lecture de terrain immergé), les Falaises sont un relief
**émergé** (une côte à pic, on la longe en surface). Ne pas les confondre au moment d'implémenter les
Récifs plus tard.

## Réalisation technique du biome

Contrairement au Marais (simple recolorisation d'un biome existant), l'identité des Falaises est
d'abord une question de **relief**, pas seulement de couleur : « muraille... plongeant directement »
implique une côte abrupte, pas une plage. Vanilla possède déjà ce relief : **`minecraft:stony_shore`**
(rivage rocheux escarpé) est la base la plus proche, très différente visuellement de `beach`/`snowy_beach`
déjà utilisés partout ailleurs dans la table de biomes actuelle.

- **Biome** `tidebound:abyssal_cliffs` : clone de `stony_shore` (extrait du jar client réel), teinté
  vers un bleu-ardoise très sombre (`water_color: 1645603`, `water_fog_color: 658966`) — cohérent
  avec la palette déjà posée pour `abyss_ocean`, sans la dupliquer.
- **Placement** : vanilla associe déjà le relief accidenté à un **erosion bas** (plus l'érosion est
  faible, plus le relief généré est escarpé — c'est la même logique qui a servi à élargir le bruit de
  forme des îles dans `TB-WORLD-002`). Carver une tranche à faible érosion (`[-1.0, -0.7]`) dans la
  cellule `beach` existante (`continentalness [0.05, 0.16]`, `humidity [-0.15, 0.7]` après le carvage
  du Marais) plutôt que dans une nouvelle bande d'humidité : les Falaises doivent pouvoir apparaître
  n'importe où le long des côtes tempérées, pas seulement dans un climat particulier.
- Le Marais garde sa tranche d'humidité `[0.7, 1.0]` intacte, sans érosion carvée : pas de falaises
  dans le marais, cohérent avec sa propre identité (marécage plat, pas escarpé).

## Danger — réutilise `TB-WORLD-002` sans code nouveau

Le tag `#tidebound:dangerous` et les dégâts de coque progressifs (`HullIntegrityEvents`, coque sous
le niveau 3) existent déjà et ne dépendent que de ce tag. Ajouter `tidebound:abyssal_cliffs` à
`data/tidebound/tags/worldgen/biome/dangerous.json` suffit à rendre ce biome dangereux sans toucher un
seul fichier Java — exactement l'infrastructure que ce biome vient enfin utiliser sur la terre ferme
(jusqu'ici le tag ne couvrait que des biomes océaniques).

## Pêche profonde et silhouettes (hors scope de cette tranche)

Le lore et la roadmap mentionnent « introduction à la pêche profonde » et « observation de
silhouettes sous-marines » pour ce biome. Ni l'un ni l'autre n'existe encore comme mécanique
(pas de notion de profondeur de pêche distincte, pas d'entité de silhouette) : à traiter dans un
ticket dédié une fois le socle du biome posé et confirmé en jeu, pas deviné ici.

## Ce qui est réellement nouveau à construire (résumé)

1. Biome `tidebound:abyssal_cliffs` (clone teinté de `stony_shore`).
2. Carvage par érosion dans la cellule `beach` des deux fichiers de preset.
3. Ajout au tag `#tidebound:dangerous` (aucun code Java touché).

Rien dans cette liste ne nécessite de nouvel art. La pêche profonde et les silhouettes restent des
tickets futurs distincts, non traités ici.
