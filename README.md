# Tidebound

Tidebound est un modpack sandbox maritime pour Minecraft Java. Le joueur explore un monde procédural d'îles, pêche, commerce en **Tides**, développe ses métiers et transforme progressivement une embarcation rudimentaire en navire personnel.

Le projet n'est pas un mode histoire. Le livre **Le Voyage** fournit un fil directeur facultatif, des repères et des récompenses, tandis que les contrats répétables et l'exploration restent libres.

## État actuel

- version du projet : `0.17.0-alpha` ;
- dernier ticket terminé : `TB-NAV-001` ;
- cible : Minecraft `1.21.1`, NeoForge `21.1.249`, Java `21` ;
- tests de domaine et validation du contenu automatisés par Gradle et la CI GitHub.

Le code actuel contient la monnaie, les métiers, les paliers, les contrats répétables, l'intendant de port,
l'enregistrement d'une barque vanilla, un Compas de sillage, ainsi que les achats et réparations du navire.
Le Devpack fournit également les chapitres FTB Quests **Naufragé** et **Premier port**.
Il inclut désormais une première sélection cohérente pour Create, JEI, Tom's Storage, JourneyMap,
Waystones sans coût d'XP, Ultimine, l'ambiance sonore, les objets physiques et les shaders.
Les morues, saumons, poissons tropicaux et poissons-globes pêchés portent désormais un poids, une
qualité, une fraîcheur, une origine, une valeur et parfois une anomalie rare.
Toute nouvelle sauvegarde utilise désormais un archipel avec un spawn ciblé sur une île boisée ; le
Nether et l'End restent inchangés.
Une commande de prototypage peut désormais matérialiser un petit quai et les seuls PNJ prévus par le
plan régional, sans modifier automatiquement les anciennes sauvegardes.
Les havres ainsi créés sont enregistrés dans la sauvegarde ; le Compas des Havres permet de retrouver
le plus proche qui possède un Intendant, même lorsque ses chunks ne sont pas chargés.

## Reprendre le projet

Lire dans cet ordre :

1. [`HANDOFF.md`](HANDOFF.md) — état exact, décisions et prochaine tâche ;
2. [`docs/design/Tidebound_Lore.md`](docs/design/Tidebound_Lore.md) — lore et tonalité canoniques ;
3. [`ROADMAP.md`](ROADMAP.md) — ordre de développement ;
4. [`core/README.md`](core/README.md) — commandes et architecture du mod ;
5. [`docs/DECISIONS.md`](docs/DECISIONS.md) — règles de conception verrouillées.

Claude peut aussi commencer par [`CLAUDE.md`](CLAUDE.md). Tout assistant ou développeur doit mettre `HANDOFF.md` et `CHANGELOG.md` à jour après une évolution matérielle.

## Arborescence

```text
Tidebound/
├── core/                 Mod NeoForge et tests
├── docs/design/          GDD et lore canonique
├── docs/technical/       Matrice technique
├── docs/TESTING.md       Compilation, profil et smoke tests
├── modpack/              Devpack CurseForge et livre FTB Quests
├── docs/DECISIONS.md     Décisions de conception
├── HANDOFF.md            Transmission toujours à jour
├── ROADMAP.md            Prochaines étapes
└── CHANGELOG.md          Historique synthétique
```

## Vérifications locales

Depuis `core/` :

```bash
python tools/validate_content.py
./gradlew check
./gradlew build
```

Le JAR attendu est `core/build/libs/tidebound-0.17.0-alpha.jar`. Le workflow GitHub Actions publie
également ce JAR comme artifact à chaque push et pull request.
