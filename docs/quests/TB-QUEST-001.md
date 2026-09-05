# TB-QUEST-001 — Fondation du Voyage

Statut : **terminé**  
Version : `0.7.0-alpha`

## But

Fournir un premier livre FTB Quests réellement importable qui accélère le départ sans transformer
Tidebound en campagne linéaire. Le livre est une façade : Tidebound Core reste la source de vérité
des Tides, métiers, contrats et données du navire.

## Contenu livré

- format FTB Quests SNBT `version: 13` ;
- langue de repli `fr_fr` et traductions françaises/anglaises séparées ;
- groupe `Le Voyage` ;
- chapitre `Naufragé` : table de craft, barque, première morue et arrivée au port ;
- chapitre `Premier port` : enregistrement, Compas de sillage, contrats, livraison et amélioration ;
- 9 quêtes, 9 tâches et 9 récompenses ;
- 105 Tides de récompenses de démarrage au total ;
- aucune dépendance entre quêtes et mode de progression `flexible`.

## Sécurité des récompenses

Chaque récompense est une `CommandReward` automatiquement réclamée avec :

```text
/tidebound progression reward-once {p} ftb:<reçu_unique> <montant>
```

Le niveau de permission est `2`. Le reçu persistant Tidebound empêche un second versement si FTB
Quests rejoue la commande.

## Choix provisoires

Trois tâches d'objet sont détectées automatiquement : table de craft, morue et Compas de sillage.
Les six autres utilisent une case de validation manuelle. Elles seront remplacées par des événements
ou critères automatiques lorsque Core exposera les signaux port, navire, contrat et amélioration.

Les contrats répétables restent exclus du livre. Ils doivent être acceptés et livrés physiquement
auprès d'un intendant pour conserver la validation serveur, la consommation des denrées et le cooldown.

## Fichiers

```text
modpack/overrides/config/ftbquests/quests/
├── data.snbt
├── chapter_groups.snbt
├── chapters/naufrage.snbt
├── chapters/premier_port.snbt
└── lang/fr_fr.snbt, en_us.snbt
```

Le lore et la structure complète des sept chapitres sont définis dans
`docs/design/Tidebound_Lore.md`.

## Vérification

Depuis `core/` :

```bash
python tools/validate_content.py
./gradlew check
```

Le test manuel est décrit dans `docs/TESTING.md`. Ne pas déclarer l'affichage en jeu validé tant que
le Devpack n'a pas été ouvert avec FTB Quests sur un client NeoForge 1.21.1.
