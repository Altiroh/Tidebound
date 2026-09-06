# Feuille de route Tidebound

## Terminé

- [x] `TB-CORE-001` — portefeuille de Tides et données du navire ;
- [x] `TB-CORE-002` — paliers, contrats JSON et récompenses idempotentes ;
- [x] `TB-CORE-003` — métiers, déclencheurs automatiques et intendant ;
- [x] `TB-CORE-004` — bateau-coffre personnel, déploiement et suivi.
- [x] `TB-CORE-005A` — Compas de sillage, barque vanilla de secours et enregistrement au port.
- [x] `TB-CORE-005B` — coûts d'amélioration, réparations, capacité de cale et prérequis ;
- [x] `TB-DESIGN-001` — lore canonique, tonalité et structure facultative du Voyage ;
- [x] `TB-QUEST-001` — livre FTB Quests `Naufragé` et `Premier port`.
- [x] `TB-FISH-001` — prises vanilla enrichies : poids, qualité, fraîcheur, origine, anomalie et valeur.
- [x] `TB-UX-001` — carnet dans l'inventaire, menu visuel de l'intendant et premier raccord des assets.
- [x] `TB-WORLD-001` — preset d'archipel par défaut, île boisée ciblée et diagnostic de spawn.
- [x] `TB-VESSEL-001` — entité de navire modulaire, quatre places, rendu progressif et migration des barques.
- [x] `TB-PACK-001` — socle cosy : Create, stockage, carte, voyage, ambiance, objets physiques et shaders.
- [x] `TB-DESIGN-002` — lore vanilla-friendly, familles d'îles, rôles portuaires, abysses et vitalité.
- [x] `TB-NPC-001` — cinq PNJ portuaires dédiés, skins et écrans visuels par métier.
- [x] `TB-ECON-001` — estimation et vente atomique des prises auprès du poissonnier.
- [x] `TB-PORT-001A` — planificateur par seed des archétypes et services portuaires.
- [x] `TB-PORT-001B` — générateur runtime de quai, recherche de rivage et PNJ conformes au plan.
- [x] `TB-NAV-001` — registre monde persistant et Compas des Havres à recette avancée.
- [x] `TB-SMELT-001` — enchantement de fonte rapide compatible avec l'économie et Create.
- [x] `TB-CORE-005C` — modules v1 branchés aux systèmes réels : projecteur, sonar, treuil et filet.
- [x] `TB-CORE-006` — interface de cale dédiée n'affichant que les emplacements débloqués.
- [x] `TB-QA-001` — partie neuve jouée, retours consignés dans `core/TB-CORE-007.md`.
- [x] `TB-CORE-007` — corrections issues du playtest : Instant Smelting, onglet créatif, Tides dans
  l'inventaire, annonce de biome, icône du Compas de sillage, Lootr, FTB Quests Translator.
- [x] `TB-WORLD-002` tranche 1 — placement automatique du port initial, puis par région explorée.
- [x] `TB-WORLD-002` tranche 2 — 22 biomes vanilla par climat (humidité, pas température : la
  première version produisait de trop grandes zones uniformes), deux biomes à eau teintée, bruit
  d'îles élargi ×1,6, zones bornées par le niveau de coque.
- [x] `TB-CORE-008` — deuxième retour client : bâtiment de port fermé au lieu d'un ponton nu, skins
  PNJ reconstruits depuis la base vanilla (fini les atlas 512×512 buggés), écran portuaire réduit à
  l'échelle d'un écran de commerce vanilla, palette PNJ affinée sur un deuxième envoi, cadre de
  dangerosité du biome.
- [x] `TB-QUEST-002` — livre FTB Quests durci : cinq tâches passent d'une case manuelle à un vrai
  avancement déclenché par Core, troisième chapitre « Les eaux côtières », dépendances explicites
  entre quêtes.

## En cours

- [ ] `TB-WORLD-002` suite — gros navires échoués rares (bloqué sans `.nbt`), aiguilles de compas
  animées.
- [ ] `TB-CORE-008` suite — validation client des PNJ recolorés, du bâtiment de port, de l'écran
  redimensionné et du cadre de biome ; reconfirmer si les boutons de l'Intendant/Charpentier
  fonctionnent une fois les PNJ mieux positionnés.
- [ ] `TB-QUEST-002` suite — validation du livre complet (FTB Quests absent de l'environnement de
  développement Core) : chargement du nouveau chapitre, déclenchement réel des tâches `advancement`.

## Après validation du prototype

- [ ] ports secondaires et contrats régionaux ;
- [ ] épaves, plongée et récupération ;
- [ ] événements de nuit, brume et danger maritime ;
- [ ] encyclopédie, records de pêche et objectifs long terme ;
- [ ] progression de la Connaissance interdite et révélations conditionnelles ;
- [ ] équilibrage multijoueur et protection anti-farm.
