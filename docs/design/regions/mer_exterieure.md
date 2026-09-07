# Mer Extérieure — différé délibérément

Statut : **non implémenté, par choix** (pas oublié). Dernier biome de
`docs/design/Tidebound_Roadmap_Biomes.md`, palier Endgame.

## Identité (rappel)

Océan colossal, cieux immenses, météo instable, îles presque mythiques, sentiment de bord du monde.
Grandes expéditions, endgame narratif, poissons légendaires, structures majeures — l'ultime horizon
de l'aventure, pas « la zone du boss final ».

## Pourquoi ne pas juste cloner un biome de plus

Chaque biome posé jusqu'ici (`TB-WORLD-003` à `TB-WORLD-008`) apportait au moins un élément réel au
moment de son implémentation, même modeste : une teinte motivée, une mécanique (marée, danger,
suppression de faune), un système (épaves, récifs). La Mer Extérieure n'a, à ce stade du projet,
**aucun** des trois piliers de sa propre fantasy :

- **poissons légendaires** : n'existent pas — `CatchProfiles` reste limité à quatre espèces vanilla
  sans palier de rareté « légendaire » distinct de la qualité `LEGENDARY` déjà générée aléatoirement
  sur n'importe quelle prise, n'importe où ;
- **structures majeures** : aucune notion de structure « majeure » n'existe (les plus grosses
  constructions actuelles sont les ports procéduraux et les épaves de `TB-WORLD-005`, toutes
  modestes) ;
- **météo instable** : la météo est globale dans Minecraft vanilla, pas pilotable par biome sans un
  système météo entièrement nouveau.

Ajouter un simple clone d'océan teinté ici serait une coquille vide : le biome existerait sur la
carte mais ne délivrerait rien de ce que sa propre fantasy promet — contrairement à l'Archipel des
Brisants (`TB-WORLD-006`), où la fantasy était déjà largement couverte par des systèmes existants, ici
c'est l'inverse : rien n'existe encore.

## Recommandation

Traiter la Mer Extérieure seulement une fois qu'au moins un des trois piliers a son propre système :
un vrai palier de poisson légendaire distinct, une structure majeure (à conception dédiée), ou une
réflexion sur une météo pilotable. Ne pas la cocher prématurément dans `ROADMAP.md`.
