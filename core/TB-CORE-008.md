# TB-CORE-008 — Deuxième vague de retours (ports, PNJ, interfaces)

## Contexte

Suite directe du premier retour visuel sur `TB-WORLD-002` : trois décisions ont été tranchées avec
l'utilisateur avant d'implémenter (voir la conversation) plutôt que de deviner une nouvelle fois.

## 1. Génération de port — un vrai bâtiment, toujours procédural

Décision : améliorer le générateur procédural existant plutôt que passer aux structures `.nbt`
(jigsaw). `HarborPlacementService` ne posait qu'un ponton plat avec quatre poteaux et des props
isolés à même le sol (« vieux port, blocs random »). Ajout de `buildHut` : un petit bâtiment fermé
derrière le quai (sol, quatre murs avec porte et fenêtres en verre, toit plat en dalles en
surplomb), matériaux cohérents avec l'archétype (mêmes planches que le ponton). Les props de service
(tonneau, lutrin, cloche, bloc de cuivre) sont désormais à l'intérieur/contre le bâtiment plutôt
qu'éparpillés sur le ponton.

Limite assumée : reste un cube procédural simple (pas de charpente, toit à un seul niveau), pas au
niveau d'une structure `.nbt` authentique. Amélioration incrémentale, pas une refonte complète.

## 2. Skins des PNJ — repartir de la base vanilla

Les cinq atlas custom (512×512, illustrations) rendaient en pixels mélangés sur le modèle
villageois : le moteur applique les coordonnées UV du modèle (des petites zones précises dans une
feuille de 64×64) à l'image fournie quel que soit son format, donc un atlas illustré à la mauvaise
échelle donne un résultat aléatoire. `tools/validate_content.py` vérifiait même explicitement
`(512, 512)` — corrigé en `(64, 64)`.

Nouvelle méthode : partir de la vraie texture vanilla (`base_villager_64x64.png`, envoyée à
l'utilisateur comme gabarit) et d'un calque de métier vanilla existant (déjà aligné pixel pour
pixel sur le bon UV), reteinté par luminance vers la palette de couleurs proposée par l'utilisateur
dans ses nouveaux visuels (toujours des rendus « concept » 1254×1254, pas des feuilles UV réelles —
un second essai avec le même problème que le premier). Correspondance retenue (à confirmer/corriger) :

| Rôle | Calque vanilla de base | Teinte |
|---|---|---|
| Intendant | `cleric` | bleu marine |
| Poissonnier | `fisherman` | inchangé (déjà brun/beige) |
| Charpentier naval | `toolsmith` | bleu marine |
| Naturaliste | `cartographer` | sarcelle |
| Gardien de phare | `shepherd` | jaune moutarde |

Moins détaillé que les illustrations originales, mais garanti aligné sur le modèle réel puisque
construit à partir des fichiers vanilla eux-mêmes.

## 3. Interfaces portuaires — échelle réduite

`HarborScreen` était calé sur l'échelle native de l'art (jusqu'à 783×573 px à 0,5×, donnant un écran
de 265×287). Passé à 0,375× (198×215), tous les décalages internes (boutons, zones cliquables,
info-bulles, jauges) recalculés par le même facteur ×0,75 pour garder la disposition relative
intacte. Plus proche de la taille d'un écran de commerce vanilla (176×166) sans redécouper la mise
en page à la main.

## Explicitement pas traité ici

- boutons « qui ne font rien » : le code de `HarborMenu#clickMenuButton` semble correct pour les
  actions réelles (Intendant/Charpentier). Hypothèse la plus probable : les PNJ mal placés par
  l'ancien générateur de port faisaient échouer la vérification de proximité serveur au moindre
  déplacement du joueur. À revalider maintenant que `buildHut` change le placement ;
- aiguilles de compas animées ;
- gros navires échoués rares.

## Validation effectuée localement

- `./gradlew build` : compilation et build réussis, `DomainSelfTest: OK`.
- `python3 tools/validate_content.py` : `OK` (dimension attendue des skins PNJ corrigée à 64×64).
- démarrage serveur réel sur un monde neuf : `Done`, aucune exception.

## Validation requise

- confirmer en jeu que les cinq PNJ s'affichent normalement (plus de pixels mélangés) et que la
  palette de couleurs approche l'intention ;
- confirmer que l'écran portuaire est visuellement plus proche d'un écran vanilla et que rien n'est
  coupé/mal aligné après la mise à l'échelle ;
- reconfirmer si les boutons de l'Intendant/Charpentier fonctionnent maintenant que les PNJ sont
  mieux positionnés par `buildHut` ;
- juger si la qualité du nouveau bâtiment de port est suffisante ou nécessite une itération
  supplémentaire (fenêtres, hauteur, toit).
