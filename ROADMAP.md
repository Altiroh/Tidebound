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
- [x] `TB-CORE-010` — catalogue des profils de prise migré du code vers le datapack, même mécanisme
  que paliers et contrats ; ajouter un poisson de mod ne demande plus de code.
- [x] `TB-CORE-011` — onglets morts « Demandes »/« Produits » de la poissonnerie désactivés avec une
  info-bulle plutôt que silencieusement inertes.
- [x] `TB-CORE-012` — première tentative : JourneyMap remplacé par sa version stable 6.0.7 — a planté
  à l'identique, crash réel remonté par l'utilisateur une deuxième fois.
- [x] `TB-CORE-013` — JourneyMap sorti du pack (instabilité connue du mod, pas un fichier mal épinglé)
  et remplacé par Xaero's Minimap.
- [x] `TB-CORE-014` — Kotlin for Forge ajouté au manifeste : dépendance manquante d'Inventory
  Profiles Next/libIPN, jamais détectée en environnement de développement Core, bloquant réel
  remonté par l'utilisateur.
- [x] `TB-WORLD-003` — Marais des Lanternes : premier biome nommé de la nouvelle roadmap maritime
  implémenté (clone teinté de `mangrove_swamp`, marée simplifiée, bonus de pêche nocturne).
- [x] `TB-WORLD-004` — Falaises Abyssales : clone teinté de `stony_shore`, carvé par érosion, premier
  biome terrestre dangereux (réutilise `#tidebound:dangerous`/`HullIntegrityEvents` sans code neuf).
- [x] `TB-WORLD-005` — Plateau des Épaves : épaves procédurales bornées (toujours pas de `.nbt`,
  décision `TB-WORLD-002` enfin traitée), tonneau relié à une vraie table de butin (première dans le
  projet), mêlant ambiance vanilla et pièces de maintenance Tidebound déjà existantes.
- [x] `TB-WORLD-006` — Archipel des Brisants, dernier biome MVP : les systèmes de ports/phares
  existaient déjà, seul ajout réel un décor de récifs rocheux autour des avant-postes de phare.
  **Les cinq biomes MVP de la nouvelle roadmap maritime sont désormais tous traités.**
- [x] `TB-WORLD-007` — Palier Extension/V1.5 : Récifs du Verre Noir, Mer Brumeuse, Fosses Abyssales
  (couche biome/danger, gameplay dédié laissé à des tickets futurs).
- [x] `TB-WORLD-008` — Mer Morte implémentée (teinte + suppression de la faune aquatique visible) ;
  Mer Extérieure **délibérément différée** — aucun de ses trois piliers (poissons légendaires,
  structures majeures, météo instable) n'a de système existant, un clone de biome serait une coquille
  vide (voir `docs/design/regions/mer_exterieure.md`).
- [x] `TB-CORE-015` — correctif de blocage réel remonté par l'utilisateur : libIPN 6.6.2 était hors de
  la plage acceptée par Inventory Profiles Next (`>=6.5.1, <6.6`), jamais détecté avant que le crash
  JourneyMap (`TB-CORE-012`/`013`) ne soit lui-même corrigé. Remplacé par libIPN 6.5.1.
- [x] `TB-CORE-016` — retours du premier vrai test en jeu : cadre du nom de biome retiré, ports
  toujours peuplés d'au moins 2 PNJ, deuxième port régional plus fréquent (1/2 au lieu de 1/3),
  commandes `/tidebound world biomes` et `/tidebound npc summon`, ajout de Counter Day.
- [x] `TB-CORE-017` — JourneyMap remis à la place de Xaero's Minimap : troisième tentative informée
  (choix par volume de téléchargements) à la demande explicite de l'utilisateur, risque assumé.

## En cours

- [ ] `TB-WORLD-002` suite — gros navires échoués rares (bloqué sans `.nbt`).
- [ ] `TB-CORE-009` — aiguilles de compas animées : infrastructure serveur/client terminée
  (cible persistante, calcul de cap, propriété d'objet cliente) ; bloqué sur l'art de l'aiguille,
  l'utilisateur fournit un calque séparé plutôt qu'une segmentation automatique risquée.
- [ ] `TB-CORE-008` suite — validation client des PNJ recolorés, du bâtiment de port, de l'écran
  redimensionné et du cadre de biome ; reconfirmer si les boutons de l'Intendant/Charpentier
  fonctionnent une fois les PNJ mieux positionnés.
- [ ] `TB-QUEST-002` suite — validation du livre complet (FTB Quests absent de l'environnement de
  développement Core) : chargement du nouveau chapitre, déclenchement réel des tâches `advancement`.

## Après validation du prototype

Cadrage détaillé reçu le 7 septembre 2026 : `docs/design/Tidebound_Roadmap_Biomes.md` (dix régions
maritimes nommées, roadmap de conception en 9 phases, priorisation MVP/V1.5/endgame). Les items
ci-dessous sont réorganisés selon cette priorisation plutôt qu'une liste plate.

**MVP (biomes nommés)** :
- [x] Marais des Lanternes — biome + marée simplifiée + bonus de pêche nocturne + contrat régional
  (`TB-WORLD-003`) ; reste : structures/PNJ dédiés (nouveau générateur à part entière, son propre
  ticket) — le biais de tirage d'espèce par biome est écarté (demanderait de surcharger la table de
  loot de pêche vanilla) ;
- [x] Plateau des Épaves — épaves procédurales bornées + table de butin posées (`TB-WORLD-005`) ;
  reste : vraie grande structure de navire (nécessiterait `.nbt` ou un générateur bien plus élaboré),
  journal de bord narratif, carte au trésor menant quelque part, contrat régional ;
- [x] Archipel des Brisants — récifs rocheux autour des avant-postes de phare (`TB-WORLD-006`), le
  reste (ports secondaires, phares, enchaînement d'îlots) existait déjà avant ce ticket.
- [x] Falaises Abyssales — biome + tag dangereux posés (`TB-WORLD-004`) ; reste : pêche profonde et
  silhouettes sous-marines (aucune des deux mécaniques n'existe encore, ticket futur dédié).

**Extension / V1.5** (couche biome/danger posée par `TB-WORLD-007`, gameplay dédié restant) :
- [x] Récifs du Verre Noir — biome posé ; reste : mécanique de cartographie précise, faune rare dédiée ;
- [x] Mer Brumeuse — biome posé (teinte uniquement) ; reste : vraie réduction de visibilité (aucun
  mécanisme de ce type n'existe, nécessite soit un effet client soit un hook de rendu dédié),
  cloches d'ambiance, rencontres rares ;
- [x] Fosses Abyssales — biome posé ; reste : faune bioluminescente, ruines englouties (pourrait
  réutiliser `WreckPlacementService`), reliques et aberrations (système du lore §17 pas encore codé).

**Endgame** :
- [x] Mer Morte — biome posé (`TB-WORLD-008`) ; reste : poissons spéciaux distincts (aucun palier de
  rareté propre à ce biome n'existe) ;
- [ ] Mer Extérieure — délibérément différée tant qu'aucun de ses trois piliers n'a de système propre
  (voir `docs/design/regions/mer_exterieure.md`), pas juste « à faire ».

**Systèmes transverses restant à faire, indépendamment des biomes** :
- [ ] ports secondaires et contrats régionaux par zone ;
- [ ] plongée et récupération (dragage déjà nommé dans le lore, non implémenté) ;
- [ ] événements de nuit, brume et danger maritime ;
- [ ] encyclopédie, records de pêche et objectifs long terme (Journal du marin) ;
- [ ] progression de la Connaissance interdite et révélations conditionnelles ;
- [ ] équilibrage multijoueur et protection anti-farm.

Sorties encore attendues de `Tidebound_Roadmap_Biomes.md`, à produire région par région : tableaux
biome → poissons/ressources/structures/événements, roadmap de production (mods/datapacks/KubeJS),
progression des modules du navire, contrats PNJ par région, version MVP vs idéale, équilibrage
économique.
