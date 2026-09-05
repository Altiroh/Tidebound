# TB-PACK-001 — Socle cosy, exploration et performances

Statut : **implémenté dans le manifeste, validation en jeu requise**  
Version : `0.12.0-alpha`

## Intention

Le Devpack doit déjà ressembler à Tidebound dans le menu des mods et offrir les outils de confort
nécessaires au playtest, sans noyer le prototype sous des systèmes concurrents. Create reste le plafond
technique ; Tidebound Core conserve l'autorité sur les navires, la pêche, les Tides et la progression.

## Sélection verrouillée

| Fonction | Mods | Rôle dans Tidebound |
|---|---|---|
| Identité | Tidebound Core | Logo, items, interfaces, monde et systèmes propriétaires |
| Quêtes | FTB Library, Teams, Quests | Journal facultatif et onboarding visuel |
| Récolte | FTB Ultimine | Réduit les tâches répétitives de construction |
| Atelier | Create | Grues, quais et petits mécanismes visuels |
| Recettes | JEI | Remplace l'ancien NEI sur Minecraft 1.21.1 |
| Stockage | Tom's Simple Storage | Réseau de coffres simple pour le port du joueur |
| Exploration | JourneyMap, Waystones, Balm | Carte et retours rapides entre lieux découverts |
| Ambiance | AmbientSounds, ItemPhysic, CreativeCore | Paysages sonores et objets au sol plus matériels |
| Rendu | Sodium, Iris, ImmediatelyFast | Performances client et prise en charge des shaders |

Tous les fichiers du manifeste ciblent Minecraft `1.21.1` et NeoForge. Les dépendances directes
Architectury, Balm et CreativeCore sont déclarées explicitement.

## Configuration imposée

`overrides/config/waystones-common.toml` définit `teleports.enableCosts = false`. La téléportation par
Waystone ne dépense donc aucun niveau d'expérience. Les restrictions restantes du mod (découverte,
activation et disponibilité d'une pierre) restent actives.

## Choix différés

- Aucun shaderpack n'est imposé : Iris rend le menu Shaders disponible, mais le joueur choisit un pack
  adapté à son GPU.
- Aucun mod prétendument « Fast Smelting Enchantment » n'est ajouté sans version NeoForge 1.21.1 vérifiée.
  Un enchantement Tidebound natif ou un datapack dédié constitue le ticket recommandé.
- Les mods décoratifs, agricoles et culinaires seront ajoutés après un démarrage complet de cette base,
  afin de mesurer mémoire, temps de chargement et conflits avant d'élargir vers 50 à 80 mods.

## Validation manuelle attendue

1. importer le Devpack avec l'option **All Files** ;
2. vérifier que Tidebound apparaît avec son logo dans **Mods** ;
3. créer un monde neuf et confirmer l'archipel ;
4. ouvrir JEI, JourneyMap et le livre FTB Quests ;
5. relier deux Waystones et contrôler que le niveau d'XP ne change pas ;
6. créer un petit réseau Tom's Storage et un mécanisme Create ;
7. activer un shader léger dans Iris et contrôler les FPS ;
8. observer les sons de biome et la physique d'un item jeté au sol.

La compilation de Tidebound ne prouve pas la compatibilité d'exécution des seize entrées du pack :
ce ticket reste soumis à un lancement CurseForge réel sur une machine cliente.
