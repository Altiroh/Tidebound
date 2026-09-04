# Décisions de conception

## Identité

- Nom de travail retenu : **Tidebound**.
- Le projet reste une aventure Minecraft sandbox.
- Aucune campagne principale ou suite de quêtes narrative n'est obligatoire.

## Monde et démarrage

- Les îles, ports et points d'intérêt dépendent de la seed.
- L'île de départ n'a pas besoin de contenir immédiatement un loueur ou un intendant.
- Un démarrage doit toutefois fournir une voie praticable : obtenir du bois, fabriquer une barque vanilla et rejoindre un port.
- Les premières quêtes apprennent les systèmes et rendent le joueur efficace ; elles ne lui dictent pas sa partie.

## Navire

- Le navire Tidebound appartient au joueur, possède un nom et conserve son identité.
- Le premier prototype emploie un bateau-coffre vanilla ; cette entité pourra être remplacée sans perdre les données du navire.
- Une barque vanilla doit pouvoir servir avant le premier port, puis être enregistrée ou remplacée.
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

## Quêtes et contrats

- FTB Quests sert de livre visuel pour l'onboarding et les paliers uniques.
- Les contrats répétables restent gérés par Tidebound Core et l'intendant du port.
- Les récompenses FTB appellent les commandes serveur Tidebound avec `{p}`, permission 2 et un reçu unique.

## Architecture

- Socle verrouillé : Minecraft 1.21.1, NeoForge, Java 21.
- `Tidebound Core` possède la logique et les données persistantes.
- KubeJS, FTB Quests et les PNJ appellent l'API du core sans devenir la source de vérité.
- Toute validation économique importante se fait côté serveur.
