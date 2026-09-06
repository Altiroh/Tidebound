# TB-FISH-001 — Modèle de prise Tidebound

Statut : **terminé**  
Version : `0.8.0-alpha`

## But

Faire de chaque poisson pêché une prise identifiable et valorisable sans remplacer les items vanilla.
Le système doit survivre aux coffres, échanges, déconnexions et futurs raccordements au Journal.

## Espèces du prototype

| Item | Poids | Poids de référence | Valeur de référence |
|---|---:|---:|---:|
| `minecraft:cod` | 500–5 000 g | 1 800 g | 12 Tides |
| `minecraft:salmon` | 1 200–10 000 g | 4 500 g | 20 Tides |
| `minecraft:tropical_fish` | 80–700 g | 250 g | 24 Tides |
| `minecraft:pufferfish` | 250–1 800 g | 700 g | 18 Tides |

Ces profils sont désormais définis en datapack (`data/tidebound/tidebound/catch_profiles/*.json`,
chargés par `TideboundContentManager` comme les paliers et contrats) plutôt que codés en dur dans
`CatchProfiles`, qui n'est plus qu'une façade de lecture (`TB-CORE-010`). L'intégration de poissons
d'autres mods ne nécessite plus de modification de code, seulement l'ajout d'un fichier JSON.

## Données persistantes

Le composant `tidebound:catch_data` contient :

- identifiant de l'espèce ;
- poids en grammes ;
- qualité ;
- temps global de capture ;
- biome d'origine ;
- anomalie éventuelle.

La fraîcheur n'est pas écrite à chaque tick. Elle est déduite de l'âge de la prise :

| État | Âge | Multiplicateur de valeur |
|---|---:|---:|
| fraîche | moins de 24 000 ticks | 100 % |
| vieillissante | moins de 72 000 ticks | 85 % |
| passée | moins de 144 000 ticks | 50 % |
| avariée | à partir de 144 000 ticks | 15 % |

## Qualité et anomalies

Qualités : ordinaire, belle, exceptionnelle et légendaire. Le niveau de Pêche améliore légèrement le
tirage sans garantir une prise rare.

Anomalies : cendrée, yeux creux et veines d'encre. La probabilité reste volontairement basse : environ
0,08 % en temps normal et 0,35 % la nuit dans un biome océanique. Une anomalie augmente la valeur mais
sert surtout de futur point d'entrée pour le Journal et la Connaissance interdite.

## Valeur

La valeur de référence de l'espèce est d'abord pondérée par le poids, puis par la qualité, l'anomalie
et la fraîcheur. Le calcul pur est centralisé dans `CatchValuation`; le futur poissonnier devra toujours
le refaire côté serveur au moment de la vente.

## Intégration

- `ItemFishedEvent` identifie les vrais poissons et enrichit leurs ItemStacks avant leur remise au joueur ;
- les déchets de pêche ne donnent plus le palier `first_catch` ni l'XP associée ;
- `ItemTooltipEvent` affiche les données synchronisées au client ;
- `TideboundApi.catchData` et `catchValue` exposent une façade aux futurs services ;
- `/tidebound catch inspect` diagnostique la prise tenue en main.

## Limites connues

- les poissons obtenus par commande, coffre ou mise à mort ne sont pas estampillés ;
- la plupart des prises ne s'empilent plus puisque leurs composants diffèrent ;
- aucune vente automatique n'existe encore ;
- l'affichage du biome utilise encore son identifiant technique ;
- le test en client NeoForge reste nécessaire après le build CI.

## Validation

```bash
cd core
python tools/validate_content.py
./gradlew check
./gradlew build
```

Le protocole en jeu est décrit dans `docs/TESTING.md`.
