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
pixel sur le bon UV), reteinté par luminance vers la palette de couleurs de la référence artistique.
Toujours des rendus « concept » (1254×1254, puis un deuxième envoi), pas des feuilles UV réelles —
impossible à appliquer directement au modèle villageois, mais utilisable pour en extraire une palette
de couleurs précise (échantillonnage automatique des couleurs dominantes de chaque image, fond/teinte
de peau/contours exclus) plutôt que deviner à l'œil comme au premier essai. Correspondance retenue,
déduite de l'iconographie de chaque image (ancre → Intendant, lanterne/ciré → Gardien de phare,
marteau/tablier de cuir → Charpentier, écusson poisson → Poissonnier, canne à pêche/boussole →
Naturaliste) :

| Rôle | Calque vanilla de base | Teinte (RGB, extraite du 2ᵉ envoi) |
|---|---|---|
| Intendant | `cleric` | bleu marine (28, 42, 74) |
| Poissonnier | `fisherman` | bleu-sarcelle (32, 82, 92) |
| Charpentier naval | `toolsmith` | cuir brun foncé (80, 58, 45) |
| Naturaliste | `cartographer` | sarcelle (72, 118, 106) |
| Gardien de phare | `shepherd` | jaune moutarde (206, 160, 40) |

Écart avec la première tentative : le Poissonnier n'est plus laissé « tel quel » (brun/beige d'origine)
mais teinté bleu-sarcelle, et le Charpentier passe de bleu marine à cuir brun foncé — la deuxième
référence artistique montre clairement un tablier de cuir avec marteau, plus proche d'un charpentier
que d'un officier naval.

Moins détaillé que les illustrations originales, mais garanti aligné sur le modèle réel puisque
construit à partir des fichiers vanilla eux-mêmes. Structure UV et cohérence des couleurs vérifiées
par rendu agrandi (NEAREST) hors moteur de jeu ; rendu réel en jeu toujours à confirmer.

## 3. Interfaces portuaires — échelle réduite

`HarborScreen` était calé sur l'échelle native de l'art (jusqu'à 783×573 px à 0,5×, donnant un écran
de 265×287). Passé à 0,375× (198×215), tous les décalages internes (boutons, zones cliquables,
info-bulles, jauges) recalculés par le même facteur ×0,75 pour garder la disposition relative
intacte. Plus proche de la taille d'un écran de commerce vanilla (176×166) sans redécouper la mise
en page à la main.

## 4. Affichage du biome — cadre et statut de dangerosité explicite

Le nom du biome (HUD client, `TideboundClientEvents.onRenderGui`) n'était qu'un texte nu, coloré en
rouge ou cyan selon `#tidebound:dangerous` — pas assez lisible comme indicateur de danger réel. Ajout
d'un encadrement (fond semi-transparent + bordure colorée selon le statut) et d'une seconde ligne de
texte explicite sous le nom du biome : « Eaux sûres » ou « Eaux dangereuses » (au lieu de ne compter
que sur la couleur du nom). Le système de dangerosité reste binaire (tag `#tidebound:dangerous`, inchangé) : ce correctif rend
ce statut existant lisible, il n'introduit pas de nouveaux paliers de danger.

Polish visuel supplémentaire : fond en dégradé (plus opaque en haut) plutôt qu'une teinte plate,
ombre externe légère, ligne de séparation entre le nom et le statut, nom en gras/statut en italique,
et fondu en sortie sur les derniers 500 ms au lieu d'une disparition brutale.

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
