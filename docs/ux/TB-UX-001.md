# TB-UX-001 — Première passe visuelle

Version : `0.9.0-alpha`

## Intention

Faire du texte un soutien et non l'interface principale. Cette tranche installe un socle réutilisable
pour les services du port sans déplacer la logique économique côté client.

## Implémenté

- icône d'ancre dans le coin de l'inventaire ;
- carnet de bord visuel avec la boucle bois, barque, port, pêche, vente, anomalie ;
- `HarborMenu` synchronisant les informations compactes du serveur ;
- `HarborScreen` habillé par l'interface créée pour l'intendant ;
- jauges de coque, moteur, cale et emplacements de modules ;
- actions envoyées par boutons de menu Minecraft puis revérifiées côté serveur ;
- nouveau sprite du Compas de sillage ;
- inventaire initial du pack artistique dans `docs/assets/ASSET_CATALOG.md`.

## Limites explicites

- le PNJ reste un villageois marqué par tag ; la planche artistique doit encore être convertie pour une
  entité ou un renderer original ;
- l'onglet Contrats revient encore à la liste textuelle existante ;
- la maquette contient certains textes français figés : une future découpe en éléments traduisibles est
  nécessaire avant finition ;
- les boutons ne remplacent pas encore toutes les commandes de diagnostic ;
- aucune animation d'interface n'est encore active ;
- le bateau physique reste vanilla jusqu'à `TB-VESSEL-001`.

## Test attendu

1. ouvrir l'inventaire à plusieurs échelles GUI et utiliser l'icône d'ancre ;
2. ouvrir l'intendant puis vérifier le solde et les quatre jauges ;
3. enregistrer une barque et vérifier la mise à jour d'état ;
4. acheter une amélioration et vérifier le débit serveur ;
5. tenter un double clic, un achat sans ressources et une interaction à distance ;
6. vérifier que le menu se ferme lorsque le joueur s'éloigne du port.
