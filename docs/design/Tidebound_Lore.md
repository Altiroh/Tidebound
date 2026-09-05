# Tidebound — Lore, progression et logique du monde

Statut : **référence canonique de conception**  
Dernière mise à jour : **5 septembre 2026**

Les systèmes réellement implémentés restent décrits dans `HANDOFF.md`. Ce document fixe la vision
cible et remplace les versions antérieures du lore.

## 1. Vision générale

Tidebound est un modpack maritime cosy, orienté exploration, pêche, navigation, découverte et
progression lente vers un imaginaire lovecraftien. Le monde semble d'abord accueillant, vivant et
presque banal. Le joueur peut survivre, construire, pêcher, commercer, explorer et améliorer son
quotidien sans suivre activement le lore.

> **Plus le joueur comprend la mer, moins elle paraît normale.**

Le surnaturel apparaît progressivement par des anomalies rares, des découvertes profondes, des
reliques, des espèces impossibles et des lieux qui ne devraient pas exister. Cthulhu, ou son
équivalent mythologique, n'est jamais un simple boss : c'est une présence, une vérité enfouie et un
horizon narratif.

## 2. Principe vanilla-friendly

Chaque système Tidebound important possède une utilité sandbox indépendante du lore :

- le navire transporte le joueur, des ressources, du stockage et des cargaisons ;
- les poissons servent à l'alimentation, au commerce et à la collection ;
- les ports sont des hubs pratiques et les îles spécialisées fournissent des ressources vanilla ;
- fumoir, table de découpe, vivier et aquarium prolongent les usages de Minecraft ;
- sonar et dragage servent d'abord l'exploration et le loot ordinaires ;
- les reliques donnent aussi de vrais bénéfices permanents.

> **Comment Minecraft vanilla ferait-il cela ?**

Cette question précède toujours l'ajout de la couche propre à Tidebound. Les ressources vanilla
gardent leur importance ; un objet Tidebound n'existe que lorsqu'il apporte une fonction nouvelle.

## 3. Un archipel vivant

Le monde normal de Tidebound est un archipel composé d'une infinité apparente de petites terres.
Le bateau et la navigation doivent conserver leur importance.

- **Îlot** : décor, halte ou ressource très ciblée.
- **Île** : biome, ressource ou petite structure principale.
- **Grande île** : plus rare ; plusieurs zones, village ou port possible.

Beaucoup d'îles restent simples. La rareté des lieux remarquables fait leur valeur.

## 4. Biomes réinventés en îles

Le joueur navigue jusqu'à reconnaître l'île correspondant à ce qu'il cherche : cuivrée,
ferrugineuse, carbonifère, aurifère, calciteuse, argileuse, sable rouge, granite, diorite, andésite,
forestière, bambou, fleurie, marécageuse, gelée, rocheuse, volcanique, champignon ou sauvage.

La géologie et la végétation vanilla portent l'essentiel de cette identité. Couleur de roche,
falaises, plages, relief et flore sont des indices lisibles depuis la mer.

## 5. Familles d'îles

### Îles de ressources

Cuivre, fer, charbon, or, argile, sable, gravier, bois, bambou, glace, pierre spécifique ou calcite.
Elles servent d'abord le sandbox.

### Îles habitées

Village de pêcheurs, cabane, ferme côtière, petit quai, chantier naval, naturaliste isolé, phare,
port marchand, refuge ou hameau. Une île habitée ne possède pas forcément un Intendant.

### Îles hostiles

Pillards, morts-vivants, grottes ouvertes, ruines infestées, nids de monstres, relief dangereux ou
créatures marines proches.

### Îles étranges

Arbres morts, plages noires, eau sombre, statues, épaves inhabituelles, sanctuaires, ruines
impossibles, falaises marquées et accès aux fosses. Certains phénomènes n'apparaissent qu'avec la
progression du joueur.

## 6. Create dans le monde

Create reste léger, maritime et lisible. Il anime les quais et prolonge Minecraft sans transformer
le pack en usine lourde : grues, treuils, convoyeurs courts, ateliers, moulins côtiers, pompes,
monte-charges, carrières légères, ascenseurs de falaise, mécanismes de phare, dragage, petites
exploitations minières et chantiers navals.

## 7. Le premier Intendant

Le joueur ne trouve pas nécessairement un port principal au début. Il peut d'abord rencontrer un
phare, une cabane, un chantier, une île sauvage ou hostile, un quai abandonné, un poissonnier ou un
naturaliste. **L'Intendant est quelque chose que l'on cherche.**

Le **Compas des Havres** empêche un mauvais tirage de worldgen de bloquer la progression. Sa recette
utilise bois, cuivre, fer, redstone, or et boussole vanilla : il devient accessible après les toutes
premières minutes, mais pas immédiatement. Il pointe vers un Intendant pertinent encore non
découvert.

Le Compas des Havres est distinct du **Compas de sillage**, qui retrouve le navire personnel.

## 8. Le navire Tidebound

Le navire Tidebound est un meilleur outil d'exploration qu'une barque vanilla, même sans lore :
transport, stockage, ports, îles de ressources, pêche au large, dragage, sonar et accès aux zones
profondes.

Ses améliorations sont visibles : coque, moteur, cale, éclairage, sonar et treuil.

La barque fabriquée par le joueur reste une **Barque de fortune** : elle peut être enregistrée et
retrouvée, mais n'est jamais un navire Tidebound améliorable. Le chantier naval construit ou remet
explicitement le véritable navire Tidebound.

## 9. Ports et lieux habités

Les ports sont des hubs différents les uns des autres. Un petit port peut n'avoir qu'un quai, des
maisons, un poissonnier et un atelier. Un autre peut proposer Intendant, chantier naval, contrats,
naturaliste, phare, stockage, mécanismes Create et services rares. Le joueur ne connaît pas tous
les services avant d'accoster.

## 10. PNJ principaux

Les PNJ conservent une silhouette et un langage visuel proches des villageois Minecraft.

| Rôle | Services |
| --- | --- |
| **Intendant du port** | Enregistrement et nom du navire, renseignements sur les ports, services administratifs et progression maritime. |
| **Charpentier naval / mécanicien** | Construction, réparation et améliorations de coque, moteur, cale, lumière, sonar, treuil et équipements. |
| **Poissonnier** | Achat physique des prises, demandes particulières et réactions aux espèces rares ou aberrantes. |
| **Naturaliste / chercheur** | Identification, Journal du marin, aberrations, reliques, découvertes abyssales et bénéfices permanents. |
| **Gardien de phare** | Météo, état de la mer, dangers, événements, rumeurs et indices narratifs. |

La logique métier reste côté Tidebound Core et ne dépend jamais du modèle visuel d'un PNJ.

## 11. Pêche et économie

> **Pêcher → revenir au port → vendre → améliorer → repartir plus loin**

Les prises se répartissent entre côtières, large, nocturnes, profondes, rares, crustacés,
mollusques et autres prises marines. Une espèce possède habitat, heure, profondeur, rareté, poids,
valeur, méthode de capture et éventuellement une version aberrante.

Elles servent aussi à cuisiner, nourrir, fumer, découper, conserver, exposer, préparer des appâts,
remplir des contrats et enrichir le Journal.

## 12. Contrats

Les contrats sont secondaires, répétables et trouvés dans les ports, villages de pêcheurs ou lieux
isolés : pêche, livraison, recherche, exploration, récupération, cargaison, prime maritime,
capture nocturne, spécimen rare et aberration. Ils donnent de bonnes raisons de naviguer, sans
devenir le scénario principal.

## 13. Journal du marin

Le Journal est le grand système de collection et de connaissance : poissons, aberrations,
créatures, reliques et lieux. Une entrée commence parfois par une silhouette inconnue puis révèle
nom, habitat, profondeur, période, poids record, captures, notes et comportement.

## 14. Aberrations

Les aberrations sont rares et mémorables. Leur apparition dépend de la profondeur, l'heure, la zone,
les anomalies, certains lieux et la Connaissance interdite. Elles ont une valeur économique, une
utilité scientifique et narrative, et peuvent être requises par des contrats spécifiques.

## 15. Exploration profonde et dragage

Le dragage récupère cargaisons, loot vanilla, outils abîmés, minerais, pièces mécaniques, journaux,
reliques, objets étranges et restes d'épaves.

Le sonar détecte d'abord bancs, profondeur, épaves et structures, puis anomalies, grandes formes et
zones inhabituelles. Son utilité normale précède sa fonction surnaturelle.

## 16. Les abysses

Les abysses sont rares, difficiles d'accès et riches en contenu unique : espèces profondes,
aberrations, reliques, épaves très anciennes, sanctuaires, structures enfouies, fragments du passé
et matériaux inconnus. La descente marque le basculement principal du cosy vers l'inquiétant.

## 17. Reliques abyssales et vitalité

Une découverte exceptionnelle peut donner un bénéfice permanent après analyse par le Naturaliste,
notamment **+1 cœur permanent**. Ces récompenses viennent de fosses, sanctuaires, épaves profondes,
créatures très rares ou anomalies sonar.

- cible : 3 à 5 gains de cœur maximum par partie complète ;
- chaque récompense est unique et non farmable ;
- la relique est consommée ou transformée pendant l'analyse ;
- le bénéfice reste utile hors du lore.

## 18. Connaissance interdite

La progression surnaturelle n'est pas une jauge de folie. Plus le joueur comprend la mer, plus il
perçoit symboles, silhouettes, anomalies sonar, portes, marques, lieux, réactions et passages
auparavant invisibles. Le joueur ne devient pas seulement plus fou : **il devient plus conscient**.

## 19. Créatures et présence sous-marine

- **Le Fouisseur** suit le navire sous l'eau et provoque des remous.
- **La Gueule-Lanterne** apparaît surtout la nuit et réagit aux sources lumineuses.
- **Le Banc Creux** imite un banc de poissons avant de révéler sa nature.
- **Le Dormeur** est une présence immense comprise très progressivement.

Ce sont parfois des événements, silhouettes ou phénomènes plutôt que des mobs à combattre.

## 20. Progression vers le mythe

### Début

Survie, construction, pêche, exploration, ressources, recherche d'un Intendant, obtention du navire
et découverte des ports.

### Milieu

Amélioration du navire, profondeur, sonar, dragage, Journal, aberrations, reliques et lieux étranges.

### Fin

Le joueur comprend que certaines traditions et certains phares ont une fonction ancienne, que les
mêmes symboles apparaissent sur des îles éloignées et que quelque chose existe sous l'archipel.
Cthulhu, ou son équivalent, reste une vérité extrêmement tardive.

## 21. Philosophie générale

Le lore n'est jamais obligatoire. Le joueur peut construire, aménager un port, utiliser Create,
pêcher, commercer, explorer, collectionner et transporter des ressources. Plus il voyage loin et
profondément, plus il risque de découvrir ce qui se cache sous la surface.

> **Minecraft maritime → exploration → maîtrise de l'archipel → profondeur → anomalies → connaissance → mythe**

## 22. Règles de design

1. Vanilla-friendly avant tout.
2. Create reste léger, fonctionnel et décoratif.
3. Les îles sont nombreuses, variées et généralement petites.
4. Toutes les îles n'ont ni port ni Intendant.
5. Toute exploration possède une utilité sandbox.
6. Les ressources vanilla gardent leur importance.
7. Un objet Tidebound doit apporter une fonction nouvelle.
8. Les ports diffèrent les uns des autres.
9. Le surnaturel reste rare au début.
10. Les aberrations restent mémorables.
11. Les abysses sont difficiles à atteindre et riches en récompenses uniques.
12. Les gains permanents de vitalité sont extrêmement rares.
13. La Connaissance interdite révèle le monde plutôt qu'elle ne punit le joueur.
14. Cthulhu n'est pas un boss classique.
15. Le navire est utile sans quête.
16. La mer devient moins rassurante à mesure que le joueur la comprend.

## Structure cible du livre

```text
TIDEBOUND
├── LE VOYAGE
│   ├── Naufragé
│   ├── Premier port
│   ├── Les eaux côtières
│   ├── Quelque chose sous l'eau
│   ├── Plus loin que les cartes
│   ├── Les profondeurs
│   └── Ce que la mer garde
├── JOURNAL DU MARIN
│   ├── Poissons
│   ├── Aberrations
│   ├── Créatures
│   ├── Lieux
│   └── Reliques
├── UN PORT À SOI
├── NAVIGATION
├── CONTRATS
└── LES PROFONDEURS
```

## Boussoles créatives

> **Plus le joueur comprend la mer, moins elle paraît normale.**

> **Le cosy doit donner envie de revenir au port.**

> **Les contrats occupent le joueur ; Le Voyage lui donne une direction.**

> **Cthulhu est un horizon mythologique, pas un boss de milieu de jeu.**

> **Tidebound doit ressembler à un jeu construit dans Minecraft, pas à une collection de mods.**
