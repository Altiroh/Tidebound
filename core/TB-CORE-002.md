# TB-CORE-002 — Paliers, contrats et récompenses

## Résultat

Tidebound dispose désormais de deux boucles complémentaires sans imposer de scénario :

1. **Paliers uniques** — accomplissements libres, récompensés une seule fois.
2. **Contrats répétables** — livraison de denrées, consommation réelle des objets, récompense et délai.

## Contrat technique

| Élément | Règle |
|---|---|
| Reçu unique | un identifiant déjà enregistré ne peut plus payer |
| Palier | un identifiant ne peut être terminé qu'une fois par joueur |
| Contrat | compteur et prochaine date disponible propres au joueur |
| Temps | cooldown exprimé en ticks du monde principal |
| Denrées | contrôlées et consommées avant le versement |
| Tides | ajout au portefeuille persistant de TB-CORE-001 |
| XP | stockée par catégorie dans `PlayerProgress` |
| Objets | ajoutés à l'inventaire, déposés au sol si celui-ci est plein |
| Rechargement | `/reload` remplace les catalogues seulement après validation complète |

## Contenu prototype

### Paliers

- `tidebound:first_catch` — 25 Tides, 50 XP pêche ;
- `tidebound:first_sale` — 40 Tides, 60 XP commerce ;
- `tidebound:open_water` — 75 Tides, 100 XP navigation et une boussole.

### Contrats

- `tidebound:coastal_delivery` — 12 morues, rotation quotidienne ;
- `tidebound:salmon_crate` — 8 saumons, rotation quotidienne ;
- `tidebound:deepwater_specimen` — 3 poissons-globes, rotation sur deux jours.

## Intégration temporaire

FTB Quests, KubeJS, un advancement ou Easy NPC peuvent exécuter les commandes administrateur sous
`/tidebound progression`. Les intégrations Java utilisent directement `TideboundApi`.

## Limites assumées

- aucun écran de quête propriétaire ;
- aucun déclencheur automatique tant que la liste définitive des mods de pêche n'est pas figée ;
- un seul type d'objet demandé par contrat dans ce prototype ;
- pas encore de sélection rotative de contrats par port ou par biome ;
- pas de synchronisation client, car toutes les lectures actuelles passent par le serveur.

## Validation attendue sur poste de développement

```bash
./gradlew check
./gradlew runServer
```

Puis tester deux fois le même reçu et le même palier, effectuer un contrat avec puis sans les denrées,
redémarrer le serveur et vérifier que solde, XP, compteur et cooldown sont conservés.
