# TB-PORT-001B — Quai portuaire matérialisé

Version : `0.16.0-alpha`  
Statut : prototype runtime implémenté ; génération automatique et grandes structures encore à produire.

## Résultat jouable

La commande administrateur suivante cherche un rivage à moins de 48 blocs, construit un quai compact
et invoque uniquement les PNJ prévus par le `PortPlan` de la région :

```mcfunction
/tidebound world port-place
```

Le matériau du tablier différencie les cinq archétypes. Les services secondaires sont également lisibles :
barils pour le stockage, pupitre pour les contrats, cloche pour l'Intendant et cuivre pour l'emplacement
d'un mécanisme Create. Le marqueur cuivre n'est pas présenté comme une contraption Create fonctionnelle.

La pose est volontairement une commande de prototypage. Elle ne s'exécute pas silencieusement dans une
sauvegarde existante et refuse de dupliquer le même site à proximité.

## Suite nécessaire

- remplacer les quais compacts par des structures `.nbt` construites et validées en jeu ;
- enregistrer leurs positions dans les données persistantes du monde ;
- raccorder la génération automatique aux îles habitées ;
- utiliser ces positions pour le Compas des Havres.
