# Tidebound

Tidebound est un modpack sandbox maritime pour Minecraft Java. Le joueur explore un monde procédural d'îles, pêche, commerce en **Tides**, développe ses métiers et transforme progressivement une embarcation rudimentaire en navire personnel.

Le projet n'est pas un mode histoire : les paliers et contrats récompensent l'autonomie sans imposer une campagne linéaire.

## État actuel

- version du mod : `0.6.0-alpha` ;
- dernier ticket terminé : `TB-CORE-005B` ;
- cible : Minecraft `1.21.1`, NeoForge `21.1.249`, Java `21` ;
- tests de domaine autonomes validés ;
- compilation NeoForge complète encore à exécuter avec un JDK 21 et Gradle.

Le code actuel contient la monnaie, les métiers, les paliers, les contrats répétables, l'intendant de port,
l'enregistrement d'une barque vanilla, un Compas de sillage, ainsi que les achats et réparations du navire.

## Reprendre le projet

Lire dans cet ordre :

1. [`HANDOFF.md`](HANDOFF.md) — état exact, décisions et prochaine tâche ;
2. [`ROADMAP.md`](ROADMAP.md) — ordre de développement ;
3. [`core/README.md`](core/README.md) — commandes et architecture du mod ;
4. [`docs/DECISIONS.md`](docs/DECISIONS.md) — règles de conception verrouillées.

Claude peut aussi commencer par [`CLAUDE.md`](CLAUDE.md). Tout assistant ou développeur doit mettre `HANDOFF.md` et `CHANGELOG.md` à jour après une évolution matérielle.

## Arborescence

```text
Tidebound/
├── core/                 Mod NeoForge et tests
├── docs/design/          GDD courant
├── docs/technical/       Matrice technique
├── docs/TESTING.md       Compilation, profil et smoke tests
├── modpack/              Manifeste CurseForge minimal
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

Le JAR attendu est `core/build/libs/tidebound-0.6.0-alpha.jar`. Le workflow GitHub Actions publie
également ce JAR comme artifact à chaque push et pull request.
