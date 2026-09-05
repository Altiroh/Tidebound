# TB-ECON-001 — Vente physique des prises

Version : `0.14.0-alpha`  
Statut : implémenté, compilation NeoForge en attente de CI.

## Boucle

Le joueur revient physiquement auprès d'un `tidebound:fishmonger`. Son écran affiche en direct le
nombre de prises Tidebound vendables dans l'inventaire et leur valeur totale actuelle. **Tout vendre**
effectue une seule transaction côté serveur.

## Garanties

- seuls les `ItemStack` portant `tidebound:catch_data` et ayant une valeur positive sont vendus ;
- un poisson vanilla obtenu autrement reste dans l'inventaire ;
- le prix réutilise poids, qualité, anomalie et fraîcheur de `CatchService` ;
- le nouveau portefeuille et la progression Commerce sont calculés avant toute suppression ;
- chaque emplacement vendu est vidé une seule fois ;
- un second clic ne peut pas recréditer les prises déjà consommées ;
- les prises avariées sans valeur restent au joueur.

XP Commerce : le maximum entre le nombre de prises, un point et 10 % des Tides obtenues.

## Test manuel

1. invoquer `/function tidebound:create_test_harbor` ;
2. pêcher plusieurs poissons et conserver un poisson vanilla donné par commande ;
3. ouvrir le poissonnier et noter l'estimation ;
4. avancer le temps pour vérifier la baisse liée à la fraîcheur ;
5. cliquer **Tout vendre** deux fois ;
6. vérifier Tides, XP Commerce, disparition des prises estampillées et conservation du poisson témoin.
