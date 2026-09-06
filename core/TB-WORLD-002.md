# TB-WORLD-002 — Monde et ports (en cours)

## Contexte

Retour direct de `TB-QA-001` : le joueur n'a trouvé ni port ni PNJ en jeu, ce qui bloque le test des
cinq PNJ portuaires et de leurs interfaces. Ticket large (variété de biomes, teintes/profondeurs
d'eau, zones bornées par le niveau du navire, gros navires échoués rares, placement automatique des
ports) ; ce document suit son avancement tranche par tranche plutôt qu'en un seul commit.

Décision prise avec l'utilisateur : pas de structures `.nbt` externes pour l'instant (aucune source
fiable identifiée sans risque de licence) — le système procédural actuel (`HarborPlacementService`)
est conservé, à réévaluer plus tard si besoin.

## Tranche 1 — Placement automatique du port initial (fait)

Le port n'était jusqu'ici jamais matérialisé automatiquement : `PortPlan.starter(seed)` et
`StarterPortPlan.shouldGenerate` existaient déjà (tirage ~1/3 par seed) mais seule la commande
administrateur `/tidebound world port-place` déclenchait une construction réelle, quelle que soit la
position du joueur au moment de l'exécution.

`dev.tidebound.core.event.StarterPortEvents` écoute `ServerStartedEvent` et appelle directement
`HarborPlacementService.placeNear(overworld, overworld.getSharedSpawnPos(), plan)` avec le plan
`starter()` s'il existe. `placeNear` est déjà idempotent (il vérifie la présence d'un PNJ portant le
tag du site avant de construire), donc réexécuter ceci à chaque démarrage de serveur est sûr : la
construction n'a lieu qu'une fois par monde.

Limite connue : la recherche de rivage reste bornée à 48 blocs autour du spawn (même limite que la
commande manuelle). Si le spawn n'est pas assez proche de l'eau, le port ne se matérialise pas et
rien ne le signale (aucun joueur n'est là pour recevoir un message d'échec). Pas de réessai avec un
rayon plus large pour l'instant.

### Validation effectuée localement

- `./gradlew build` : compilation et build réussis, `DomainSelfTest: OK`.
- lancement d'un serveur réel sur un monde neuf : démarrage propre, sans exception.

### Validation requise

- confirmer en jeu qu'un port apparaît bien près du spawn quand le tirage est favorable (~1/3 des
  seeds) et que les PNJ du plan sont présents et interactifs ;
- tester plusieurs seeds pour couvrir le cas où le tirage échoue (aucun port ne doit apparaître) et le
  cas où le spawn est trop loin d'un rivage exploitable.

## Restant à faire

- variété de biomes maritimes (teintes et profondeurs d'eau, plusieurs types d'îles au-delà de la
  forêt/plage actuelle) ;
- zones bornées par le niveau du navire (aucun hook existant : `PlayerVessel.hullTier`/`motorTier`
  et `VesselDeploymentService` sont les points d'extension naturels) ;
- gros navires échoués rares (en plus des épaves déjà existantes) ;
- peupler le tag `#tidebound:dangerous` une fois que des biomes réellement dangereux existent.
