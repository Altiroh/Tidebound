# Décisions de conception

## Identité

- Nom de travail retenu : **Tidebound**.
- Le projet reste une aventure Minecraft sandbox.
- `Le Voyage` est une progression principale facultative : il oriente sans verrouiller le sandbox.
- La phrase directrice est : **« Plus le joueur comprend la mer, moins elle paraît normale. »**

## Monde et démarrage

- Les îles, ports et points d'intérêt dépendent de la seed.
- L'île de départ n'a pas besoin de contenir immédiatement un loueur ou un intendant.
- Un démarrage doit toutefois fournir une voie praticable : obtenir du bois, fabriquer une barque vanilla et rejoindre un port.
- Les premières quêtes apprennent les systèmes et rendent le joueur efficace ; elles ne lui dictent pas sa partie.
- Le confort du port sert de contraste et donne envie de revenir entre deux expéditions.
- Le preset normal d'une nouvelle sauvegarde Tidebound est remplacé par l'archipel ; une ancienne
  sauvegarde n'est jamais convertie silencieusement.
- Le premier profil d'archipel privilégie océan profond, océan, plage, plaine et forêt afin de garantir
  eau, rivage et bois sans réintroduire de continent.
- Un futur port initial est réservé par seed avec une probabilité d'environ un tiers ; son absence ne
  bloque jamais la boucle bois → barque → premier port.

## Navire

- Le navire Tidebound appartient au joueur, possède un nom et conserve son identité.
- Le premier prototype emploie un bateau-coffre vanilla ; cette entité pourra être remplacée sans perdre les données du navire.
- Une barque vanilla doit pouvoir servir avant le premier port, puis être enregistrée ou remplacée.
- Une barque vanilla enregistrée reste une **Barque de fortune** : elle est localisable et réparable,
  mais ne peut recevoir aucune amélioration. Le chantier construit explicitement le navire Tidebound.
- La localisation normale passe par un objet de jeu. La commande reste réservée au diagnostic et à l'administration.

## Progression

- Les paliers sont uniques et comparables à des succès récompensés.
- Les contrats sont répétables, soumis à rotation ou cooldown et indépendants d'une histoire.
- Les quatre métiers initiaux sont pêche, navigation, commerce et récupération.
- La progression superpose récompenses courtes, améliorations intermédiaires et objectifs de collection à long terme.

## Économie

- La monnaie propre s'appelle le **Tide** ; le pluriel affiché est **Tides**.
- Les améliorations du navire, réparations et services portuaires doivent créer les principaux débouchés monétaires.
- Une amélioration exige le navire au quai, des Tides, un matériau et un niveau de métier.
- La cale du prototype utilise 9, 18 puis 27 cases ; les cases verrouillées ne détruisent jamais leur contenu.
- Les émeraudes et fermes vanilla ne doivent pas permettre de contourner l'économie maritime.

## Pêche

- Une prise reste un ItemStack du poisson d'origine et reçoit le Data Component `tidebound:catch_data`.
- Le prototype couvre morue, saumon, poisson tropical et poisson-globe obtenus par la pêche.
- Le poids et la qualité sont fixés à la capture ; la fraîcheur est calculée depuis le temps du monde.
- La valeur combine espèce, poids, qualité, anomalie et fraîcheur, toujours côté serveur lors d'une vente.
- Les anomalies restent rares : environ 0,08 % normalement et 0,35 % la nuit en biome océan.
- Une prise unique peut ne plus s'empiler ; les casiers, viviers et caisses devront résoudre ce coût d'inventaire.

## Quêtes et contrats

- FTB Quests sert de livre visuel pour l'onboarding et les paliers uniques.
- Le livre versionné commence par `Naufragé` et `Premier port` ; tous les objectifs restent visibles et sans dépendance obligatoire.
- Les récompenses de ces deux chapitres totalisent 105 Tides et utilisent des reçus idempotents.
- Les tâches non observables par le Core utilisent temporairement une validation manuelle clairement indiquée.
- Les contrats répétables restent gérés par Tidebound Core et l'intendant du port.
- Les récompenses FTB appellent les commandes serveur Tidebound avec `{p}`, permission 2 et un reçu unique.

## Lore et horreur

- Aucun PNJ ne possède toute la vérité ; les fragments et témoignages peuvent se contredire.
- Cthulhu reste un horizon mythologique tardif et ambigu, pas un boss à invoquer ou vaincre.
- L'horreur doit rester rare, mémorable et partiellement inexpliquée.
- La future Connaissance interdite fera apparaître de nouveaux signes et phénomènes à mesure que le joueur comprend la mer.
- Le Journal séparera poissons, aberrations, créatures, lieux et reliques.

## Philosophie du modpack

- Viser un pack lisible et léger, idéalement 50 à 80 mods dépendances comprises.
- Ajouter les mods par fonction démontrée ; `Create` représente le plafond de complexité acceptable.
- Le contenu maritime propriétaire doit primer sur l'empilement de systèmes généralistes.
- Le premier socle externe utilise JEI plutôt que NEI, Create comme plafond technique, Tom's Storage
  pour les coffres, Xaero's Minimap/Waystones pour l'exploration et Sodium/Iris pour le rendu.
- Les téléportations Waystones ne coûtent pas d'XP ; leur découverte reste nécessaire.

## Architecture

- Socle verrouillé : Minecraft 1.21.1, NeoForge, Java 21.
- `Tidebound Core` possède la logique et les données persistantes.
- KubeJS, FTB Quests et les PNJ appellent l'API du core sans devenir la source de vérité.
- Toute validation économique importante se fait côté serveur.
