# TB-CORE-005B — Économie et entretien du navire

## Résultat

La capitainerie vend maintenant les niveaux de coque, moteur, cale et emplacements de module. Chaque
offre combine un coût en Tides, un matériau vanilla et un niveau de métier. Le navire doit être
physiquement ramené à moins de douze blocs du joueur et près d'un intendant.

## Garanties de transaction

- toutes les conditions sont vérifiées avant le premier débit ;
- une transaction par joueur peut être active à la fois ;
- Tides, matériaux et niveau du navire sont restaurés si l'application échoue ;
- les commandes d'achat joueur restent vérifiées côté serveur ;
- les commandes administrateur gratuites restent réservées au diagnostic.

## Cale

- niveau 1 : 9 emplacements utilisables ;
- niveau 2 : 18 emplacements ;
- niveau 3 et plus : 27 emplacements.

Le menu du bateau-coffre affiche encore les 27 cases vanilla. Chaque seconde, les objets présents dans
une case verrouillée sont transférés dans l'inventaire du propriétaire ; si celui-ci est plein, ils sont
déposés près du bateau. Une barque vanilla enregistrée est convertie en bateau-coffre lors du premier
achat de cale, sans changer l'identité persistante du navire.

## Réparation

Un navire endommagé et ramené au quai peut être réparé. Le prix augmente par tranches de dégâts et
consomme des planches de chêne. Le bouton apparaît dans la capitainerie uniquement si le navire proche
est endommagé.

## Outillage de test

- Gradle Wrapper repris du MDK NeoForge officiel ;
- workflow GitHub Actions sous Java 21 ;
- guide `docs/TESTING.md` ;
- manifeste `modpack/manifest.json` avec les dépendances FTB épinglées ;
- pont FTB Quests corrigé pour `{p}` et Permission Level 2.

## Validation effectuée localement

- `DomainSelfTest: OK`, incluant prix, plafonds de cale et réparations ;
- JSON Tidebound et manifeste vérifiés ;
- structure Java et différences Git contrôlées.

## Validation requise

- workflow GitHub Actions complet avec Java 21 ;
- lancement `runClient` et smoke test de `docs/TESTING.md` ;
- test du remboursement avec inventaire plein ;
- test multijoueur de deux achats simultanés.

## Suite

`TB-CORE-005C` : modules projecteur, sonar, treuil et filet.
