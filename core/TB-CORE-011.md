# TB-CORE-011 — Boutons morts de la poissonnerie rendus honnêtes

## Contexte

Le retour `TB-QA-001`/deuxième playtest signalait que « les boutons des interfaces ne font rien ».
Pour l'Intendant et le Charpentier, l'hypothèse retenue (et vraisemblablement confirmée par
`TB-CORE-008`) était un mauvais positionnement des PNJ cassant la vérification de proximité. Mais en
auditant `HarborScreen.initFishmonger`, un vrai cas séparé est apparu : l'art `harbor_fishmonger.png`
dessine trois onglets — **Vendre**, **Demandes**, **Produits** — mais seul **Vendre** avait jamais été
raccordé à une zone cliquable. Cliquer sur « Demandes » ou « Produits » ne faisait strictement rien,
sans aucun indice pour le joueur que c'est normal (fonctionnalité non livrée) plutôt qu'un bug.

## Pourquoi ne pas juste « rendre dynamique » le tableau des prix

Le board « Prix du jour » peint dans l'art liste six espèces fictives (Maquereau, Morue, Saumon, Thon,
Calamar, Crabe) qui ne correspondent pas aux quatre espèces réellement gérées par
`CatchProfiles`/`TB-CORE-010` (morue, saumon, poisson tropical, poisson-globe). Faire pointer
« Produits » vers une vraie liste dynamique afficherait un contenu contredisant le tableau peint juste
à côté — plus confus, pas moins. Corriger le fond lui-même demande soit un nouvel art (les prix ne sont
pas un calque séparable, même limite que `TB-CORE-009`), soit une décision de contenu (quelles espèces
sont réellement vendues). Aucune des deux ne peut être tranchée sans l'utilisateur.

## Correctif appliqué

Les deux boutons reçoivent une zone cliquable réelle, mais **désactivée** (`active = false`) avec une
info-bulle « Bientôt disponible » au survol — même mécanisme Vanilla que n'importe quel bouton
grisé. Le joueur comprend maintenant que ces onglets sont prévus mais pas encore livrés, au lieu de
soupçonner un bug de câblage. Aucune donnée inventée, aucun art modifié.

Géométrie des deux nouvelles zones déduite par calcul à partir de celle, déjà vérifiée, du bouton
« Vendre » (`artX+13, topPos+128, 59×29`) : trois colonnes égales dans la même rangée, `artX+75` et
`artX+137`. **Non vérifié visuellement** — si l'alignement est décalé au prochain test, ce sont des
constantes numériques triviales à corriger dans `HarborScreen.initFishmonger`.

## Fichiers touchés

- `core/src/main/java/dev/tidebound/core/client/HarborScreen.java` ;
- `core/src/main/resources/assets/tidebound/lang/{en_us,fr_fr}.json`
  (`menu.tidebound.requests`, `menu.tidebound.catalog`, `menu.tidebound.coming_soon`).

## Vérification

- `./gradlew build`, `validate_content.py`, démarrage serveur réel : tous `OK` ;
- alignement visuel des deux nouvelles zones et lisibilité de l'info-bulle non vérifiables dans cette
  session, comme pour le reste de `TB-CORE-008`.
