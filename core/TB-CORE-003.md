# TB-CORE-003 — Boucle sandbox jouable

## Résultat

Cette version relie les systèmes de TB-CORE-002 au jeu sans créer de campagne linéaire :

- une prise à la canne rapporte 5 XP de pêche et déclenche le palier du premier poisson ;
- naviguer sur un biome océan rapporte 2 XP toutes les 20 secondes et déclenche le palier du large ;
- la première livraison de contrat déclenche automatiquement le premier échange ;
- les métiers ont dix niveaux dérivés de leur XP persistante ;
- des contrats peuvent demander un métier et un niveau minimal ;
- un villageois marqué comme intendant devient un tableau de contrats interactif ;
- une livraison cliquée n'est acceptée que dans un rayon de huit blocs autour d'un intendant.

## Courbe des métiers

| Niveau | XP totale |
|---:|---:|
| 1 | 0 |
| 2 | 100 |
| 3 | 250 |
| 4 | 500 |
| 5 | 900 |
| 6 | 1 400 |
| 7 | 2 000 |
| 8 | 2 750 |
| 9 | 3 650 |
| 10 | 4 700 |

Catégories de base : `fishing`, `navigation`, `trade`, `salvage`. Le registre accepte aussi des
catégories ajoutées par un datapack ou une intégration, dans la limite prévue par `PlayerProgress`.

## Installer un intendant de port

1. Placer ou invoquer un villageois dans le port.
2. Le viser avec un sélecteur de commande puis exécuter :

```text
/tidebound harbor register @e[type=minecraft:villager,sort=nearest,limit=1]
```

Pour le créer directement à l'emplacement courant :

```text
/function tidebound:create_harbor_intendant
```

Le villageois reçoit le nom **Intendant du port**, devient persistant et intercepte le clic droit.
Pour le rendre à son comportement normal :

```text
/tidebound harbor unregister @e[type=minecraft:villager,sort=nearest,limit=1]
```

## Contrats progressifs fournis

- livraison côtière : accessible dès le début ;
- caisse de saumons : pêche niveau 2 ;
- spécimens du large : pêche niveau 3.

## Ponts de modpack

- `examples/kubejs/server_scripts/tidebound_bridge.js` expose `global.Tidebound` ;
- `examples/ftb-quests/README.md` donne les récompenses Command à configurer ;
- `TideboundApi` reste la façade pour une intégration Java.

## Limites de cette itération

- le tableau utilise le chat interactif, pas encore un écran graphique propriétaire ;
- le PNJ est installé par commande ou fonction datapack, pas encore injecté automatiquement dans le worldgen ;
- la navigation détecte les bateaux vanilla ; le futur bateau Tidebound devra appeler la même API ;
- les textes techniques restent en dur jusqu'à la passe de localisation complète.
