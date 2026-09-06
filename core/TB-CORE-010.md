# TB-CORE-010 — Catalogue de prises en datapack

## Contexte

`CatchProfiles` portait depuis `TB-FISH-001` un commentaire explicite : « Initial vanilla profiles.
A datapack-backed catalogue can replace this facade later. » C'était un des points listés dans
`HANDOFF.md` sous « Ce qui n'est pas encore implémenté » (« Catalogue par datapack »), indépendant de
tout art ou décision en attente — donc réalisable sans nouvel input.

## Changement

Les quatre profils vanilla (morue, saumon, poisson tropical, poisson-globe) sont maintenant définis
dans `data/tidebound/tidebound/catch_profiles/*.json`, chargés par `TideboundContentManager` au
rechargement des ressources — exactement le même mécanisme déjà utilisé pour les paliers
(`tidebound/milestones`) et les contrats (`tidebound/contracts`). `CatchProfiles` devient une simple
façade de lecture (`find`/`all`) déléguant à `TideboundContentManager`, sans changer son API externe :
aucun appelant (`CatchService`, `VesselModuleEvents`) n'a eu besoin d'être modifié.

Schéma JSON par fichier :

```json
{
  "species": "minecraft:cod",
  "min_weight_grams": 500,
  "max_weight_grams": 5000,
  "reference_weight_grams": 1800,
  "base_value_tides": 12
}
```

Intégrer un poisson d'un autre mod ne nécessite désormais plus de modification de code : un nouveau
fichier JSON suffit.

## Contrainte de test découverte

`DomainSelfTest` s'exécute en JVM pure, sans `ResourceManager` Minecraft réel — comme les paliers et
contrats déjà chargés par `TideboundContentManager`, le catalogue de prises n'y est donc plus
peuplé. Le test `vanillaFishProfilesAreComplete` (qui vérifiait le contenu du catalogue) a été retiré
plutôt que cassé silencieusement ; les deux autres tests qui empruntaient un `CatchProfile` de morue
en construisent désormais un directement en mémoire (même comportement, sans dépendre du chargement
datapack). La couverture du contenu réel du catalogue passe par `validate_content.py` (nouvelle
fonction `validate_catch_profile`, structure et unicité des espèces) et par le démarrage serveur réel,
au même niveau de garantie que paliers et contrats.

## Vérification

- `./gradlew build` : compilation et `DomainSelfTest` réussis ;
- `python3 tools/validate_content.py` : `OK (3 milestones, 3 contracts, 4 catch profiles, ...)` ;
- démarrage serveur réel sur un monde neuf : `Loaded 3 Tidebound milestones, 3 repeatable contracts and
  4 catch profiles`, aucune erreur — confirme que les quatre profils vanilla sont chargés avec
  exactement les mêmes valeurs qu'avant la migration.

Aucun changement de comportement en jeu : les nombres (poids, valeur) sont identiques à `TB-FISH-001`.
