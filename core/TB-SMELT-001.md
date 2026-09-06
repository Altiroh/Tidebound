# TB-SMELT-001 — Fonte rapide

## Résultat

Ajoute l'enchantement `tidebound:fast_smelting` (« Fonte rapide »), obtenable à la table
d'enchantement et à l'enclume sur les mêmes outils qu'Efficacité (`#minecraft:enchantable/mining`,
objet primaire : pioches). Aucun candidat NeoForge 1.21.1 n'existait car vanilla n'a pas de composant
d'effet d'enchantement pour la vitesse de cuisson d'un four ; la logique est donc entièrement côté Java.

## Mécanique

- L'effet ne s'applique que **tant que le joueur a lui-même un menu de four ouvert** (fourneau,
  haut-fourneau ou fumoir) et tient l'outil enchanté en main principale ou secondaire.
- Chaque tick serveur où ces conditions sont réunies et où le four est allumé, la progression de
  cuisson avance d'autant de ticks supplémentaires que le niveau de l'enchantement (niveau 1 ≈ deux
  fois plus vite, niveau 3 ≈ quatre fois plus vite), plafonnée à `cookingTotalTime - 1` pour laisser
  la complétion normale du four (XP, avancement de recette, sortie de l'item) se produire au tick
  suivant sans la dupliquer.
- Ce choix garantit qu'aucune ligne automatisée (entonnoirs, Create) n'est jamais accélérée : sans
  joueur avec le menu ouvert, il ne se passe rien. Les recettes, l'économie et Create ne sont donc pas
  court-circuités.

## Implémentation technique

Aucun événement NeoForge n'expose la progression de cuisson d'un four (seul
`FurnaceFuelBurnTimeEvent` existe, et il ne concerne que la durée du combustible). La solution retenue :

- `core/src/main/resources/META-INF/accesstransformer.cfg` rend publics `AbstractFurnaceMenu#container`
  (qui référence directement le `AbstractFurnaceBlockEntity` pour un vrai four) ainsi que
  `AbstractFurnaceBlockEntity#litTime`, `#cookingProgress` et `#cookingTotalTime`, tous package-private
  en vanilla. Repéré et confirmé en inspectant les sources décompilées réelles pendant la compilation,
  pas en devinant l'API.
- `dev.tidebound.core.registry.TideboundEnchantments` porte la `ResourceKey<Enchantment>` de
  l'enchantement.
- `dev.tidebound.core.event.FastSmeltingEvents` écoute `PlayerTickEvent.Post` (même style que
  `TideboundGameplayEvents`) et applique le bonus décrit ci-dessus.
- `data/tidebound/enchantment/fast_smelting.json` : niveau max 3, poids 5, coûts proches
  d'Efficacité, `slots` main et off-hand, `effects: {}` (aucun composant vanilla, tout est en Java).

## Validation effectuée localement

- `./gradlew compileJava` puis `./gradlew build` : **compilation réussie** contre les vraies classes
  NeoForge 1.21.1/Minecraft 1.21.1 (un JDK 21 a été installé localement pour cette vérification) ;
  aucune régression de compilation, `DomainSelfTest: OK`.
- `python3 tools/validate_content.py` : `OK` (le JSON de l'enchantement n'est pas couvert par ce
  validateur, qui ne porte que sur milestones/contrats/FTB Quests).
- Le JAR produit contient bien `data/tidebound/enchantment/fast_smelting.json` et
  `META-INF/accesstransformer.cfg` (vérifié par inspection de l'archive).

## Validation requise

- lancement client/serveur réel (`runClient`) : enchanter une pioche, ouvrir un four contenant un
  minerai, comparer la vitesse de la barre de progression avec et sans l'outil en main ;
- vérifier qu'un four automatisé (entonnoir, ou ligne Create) n'est jamais accéléré, même avec un
  joueur enchanté à proximité mais sans menu ouvert ;
- test multijoueur : deux joueurs sur deux fours distincts ne doivent pas s'influencer.

## Suite

`TB-CORE-005C` : modules v1 branchés aux systèmes réels (projecteur, sonar, treuil, filet).
