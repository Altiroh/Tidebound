# TB-CORE-016 — Retours du premier vrai test en jeu (post-correctifs de lancement)

## Contexte

Premier retour de jeu réel une fois le client capable de démarrer (`TB-CORE-012` à `015`). Cinq
points distincts, traités ici.

## 1. Cadre du nom de biome retiré

`TB-CORE-008` avait ajouté un cadre (fond en dégradé, bordure, ombre) autour du nom de biome. Retour :
« trop carré, pas besoin du cadre, juste le nom et le niveau de dangerosité ». Remplacé par du texte
simple (ombre par défaut de Minecraft, pas de fond) : nom du biome sur une ligne, statut « Eaux
sûres »/« Eaux dangereuses » juste en dessous, fondu de sortie conservé.

## 2. Ports trop peu peuplés

Retour : « dans mon port j'avais qu'un pêcheur ». Cause réelle trouvée dans `PortPlan.at` : sauf pour
`MARKET_HARBOR` (2 PNJ de base), tous les archétypes de port ne posent qu'**un seul** PNJ de base, et
le second PNJ n'avait qu'une chance sur 8 d'être ajouté. Un port `FISHING_HAMLET` (l'archétype le plus
fréquent, 34/100) n'avait donc un poissonnier seul que dans l'immense majorité des cas — pas un coup
de malchance, le comportement normal de la formule précédente.

Corrigé : le second PNJ est désormais **toujours** ajouté (le tirage aléatoire ne portait plus que sur
les extras : tableau de contrats, stockage, mécanisme Create). Chaque port a maintenant au moins 2 PNJ.

## 3. Deuxième port trop rare

Retour : « je peine à en trouver un autre ». `RegionalPortEvents` tirait un port par région de
512 blocs avec une chance sur 3. Remonté à **une chance sur 2**. La mécanique exacte (512 blocs par
tirage, vérification toutes les 30 secondes par joueur) est maintenant aussi documentée dans le
commande `/tidebound world biomes` et la checklist de test, pour que la prochaine session sache à
quoi s'attendre plutôt que de deviner.

## 4. Commandes de test ajoutées

- `/tidebound world biomes` : liste tous les identifiants de biome Tidebound avec la syntaxe
  `/locate biome <id>` prête à copier. **Aucun nouveau code de recherche n'était nécessaire** :
  `/locate biome` est une commande vanilla qui fonctionne nativement sur n'importe quel biome
  enregistré (vanilla ou datapack), avec un rayon de recherche de 6400 blocs — vérifié contre le vrai
  bytecode de `LocateCommand` plutôt que supposé.
- `/tidebound npc summon <role>` (intendant, shipwright, fishmonger, naturalist, lighthouse_keeper) :
  fait apparaître un PNJ du rôle demandé à la position du joueur, sans passer par un port réel. Un
  clic droit dessus ouvre son écran normalement (`PortNpcEntity.mobInteract` ne dépend d'aucun
  enregistrement de site) — exactement ce qu'il fallait pour tester skins et écrans sans dépendre de
  la génération procédurale.

## 5. Compteur de jours

Ajout de **Counter Day** (CurseForge `1116006`/`6657495`, NeoForge 1.21.1, « Counting the days to the
100 day challenge ») — choix délibéré : le rythme des « 100 jours » est déjà la référence de pacing
explicite du projet (`docs/design/Tidebound_Roadmap_Biomes.md`, principes directeurs).

## Non traité ici : structures procédurales « pas encore ça »

Retour noté mais sans action dans ce ticket : « les structures générées commencent à être pas mal,
mais c'est toujours pas ça ». L'utilisateur a proposé de fournir des structures `.nbt` faites à la
main — un changement de direction par rapport à la décision `TB-WORLD-002`/`005` (« pas de `.nbt` pour
l'instant »). Liste des structures nécessaires communiquée dans la conversation ; l'intégration réelle
des fichiers `.nbt` fournis fera l'objet d'un ticket dédié une fois reçus.

## Non traité ici : préférence JourneyMap

L'utilisateur a indiqué préférer JourneyMap à Xaero's Minimap. Discussion et décision communiquées
dans la conversation plutôt que tranchées unilatéralement, vu que deux versions différentes de
JourneyMap ont déjà planté de façon identique (`TB-CORE-012`/`013`).

## Vérification

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK (... Devpack: 27 pinned mods)` ;
- démarrage réel d'un serveur dédié sur un monde neuf : `Done`, aucune erreur.

## Non vérifié

Comme toujours : rendu réel du nom de biome simplifié, richesse effective des ports, fréquence
perçue du deuxième port, et bon fonctionnement des deux nouvelles commandes en jeu restent à confirmer
par l'utilisateur.
