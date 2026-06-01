## Système de Personnages et de Capacités

### 1. Architecture contractuelle via `SmurfCharacter`

L'interface `SmurfCharacter` constitue le pivot architectural du système de personnages. Elle définit un contrat strict que chaque type de Schtroumpf doit respecter, garantissant ainsi une polymorphie totale au sein du moteur de jeu.

**Objectif** : Permettre au moteur de jeu (`SmurfVillage`, phases de jeu) d'interagir avec n'importe quel personnage sans connaître son type concret, conformément au principe de substitution de Liskov.

**Structure contractuelle** :
- Gestion de l'énergie : `getEnergy()`, `setEnergy()`, `updateEnergy()`, `getBaseMaxEnergy()`
- Gestion des attributs : `getAttribute()`, `updateAttribute()`
- Exposition des capacités : `getAbilities()`
- Méthodes de commodité : `hasEnoughEnergy()`, `hasRequiredResources()`, `canExecute()`
- Factory statique : `fromType(SmurfType type)`

**Justification du choix de l'interface** :
L'interface a été préférée à une classe abstraite pour plusieurs raisons :
1. **Principe Ouvert/Fermé** : L'ajout d'un nouveau Schtroumpf (ex: `PapaSmurf`) se limite à l'implémentation de l'interface sans modifier le moteur de jeu. La factory `fromType()` est le seul point d'extension à mettre à jour.
2. **Absence d'état partagé** : Chaque personnage gère son propre état (énergie, attributs) via des `HashMap` internes, rendant inutile un héritage d'état.
3. **Flexibilité maximale** : L'interface permet à chaque implémentation de définir sa propre stratégie de comportement sans contrainte d'héritage.

### 2. Encapsulation des capacités via `CharacterAbility`

`CharacterAbility` est implémentée comme un *record* Java, ce qui lui confère immutabilité et transparence structurelle. Elle encapsule l'ensemble des métadonnées d'une capacité.

**Structure d'une capacité** :
- `name` : nom d'affichage
- `description` : description textuelle
- `energyCost` : coût en énergie
- `requiredResources` : ressources nécessaires (`List<ResourceSnapshot>`)
- `primaryEffects` : effets principaux (pour l'UI uniquement)
- `actionLogic` : **fonction lambda** de type `Function<SmurfVillage, AbilityResult>`

**Utilisation des lambdas comme stratégie d'exécution** :
Le champ `actionLogic` est une `Function<SmurfVillage, AbilityResult>`, ce qui constitue l'élément clé du design. Cette approche présente plusieurs avantages :

1. **Séparation définition/implémentation** : La définition de la capacité (nom, coût, prérequis) est déclarative dans le record, tandis que la logique d'exécution est encapsulée dans une méthode privée de la classe personnage.
2. **Pattern Stratégie** : Chaque capacité est une stratégie autonome, injectée dans le record via une référence de méthode (`this::executeGatherBerries`).
3. **Testabilité** : La logique d'exécution peut être testée indépendamment de la structure de la capacité.

**Résultat d'exécution** :
Le type `AbilityResult` (record interne) encapsule :
- `type` : `SUCCESS`, `FAILURE` ou `NEUTRAL` (pour l'UI)
- `message` : description textuelle du résultat
- `effectsToApply` : liste d'effets à appliquer au village

### 3. Gestion des attributs et état

Chaque personnage gère son état interne via deux mécanismes distincts :

**Gestion de l'énergie** :
- Valeur initiale commune : 10
- Méthode `updateEnergy()` utilisant `Math.clamp()` pour borner entre 0 et `getDynamicMaxEnergy()` (calculé dynamiquement via les modificateurs du village)
- `getBaseMaxEnergy()` : valeur par défaut de 10, surchargeable

**Gestion des attributs personnalisés** :
- Stockage via `Map<CharacterAttribute, Integer>` (implémentation `HashMap`)
- Initialisation spécifique dans chaque constructeur

Cette approche permet une personnalisation totale du comportement : les attributs influencent directement les probabilités de succès des capacités (ex: `GrandSmurf.executeCheckSpellBook` utilise `WISDOM` pour moduler la chance de réussite).

### 4. Différenciation concrète des personnages

L'interface `SmurfCharacter` permet d'implémenter des stratégies radicalement différentes tout en respectant le même contrat.

**BrainySmurf (orientation érudition)** :
- Attribut clé : `WISDOM = 2` (le plus élevé)
- Capacités orientées savoir : traduction de formules, étude de parchemins, écriture d'histoire
- Interaction avec les modificateurs : `executeStudyScroll()` applique un `ModifierEffect` de type `PRODUCTION_DELTA`
- Utilisation de `WeightedOutcomeSelector` pour les issues aléatoires (traduction de formule)

**GrouchySmurf (orientation survie/défense)** :
- Capacités défensives : surveillance des alentours (+1 Défense), dénonciation, prévention d'attaque
- Capacité unique : `executePreventAttack()` applique un modificateur persistant `CRISIS_SHIELD_COUNT`, démontrant une interaction avancée avec le système de crise
- Pas d'utilisation de `WeightedOutcomeSelector` : résultats déterministes

**GrandSmurf (orientation village/leader)** :
- Attribut clé : `WISDOM = 0` (évolutif via succès)
- Capacités sociales : consultation du grimoire, réunion, négociation avec les animaux
- Utilisation intensive de `WeightedOutcomeSelector` avec probabilités dynamiques basées sur `WISDOM`
- Pattern d'apprentissage : `executeCheckSpellBook()` incrémente `WISDOM` en cas de succès, créant une boucle de progression

### 5. Place des utilitaires dans l'architecture

Le `WeightedOutcomeSelector` est un composant utilitaire optionnel, non intégré au cœur de l'architecture des capacités. Il intervient uniquement lorsque la logique métier nécessite un tirage aléatoire pondéré entre plusieurs issues possibles.

**Fonctionnement** :
- Agrège des `OutcomeChoice` (record contenant poids, type de résultat, message, effets, callback optionnel)
- Calcule les probabilités normalisées par catégorie (succès/échec/neutre)
- Applique les modificateurs de chance du village (`SUCCESS_CHANCE_BONUS`)
- Sélectionne une issue via tirage aléatoire

**Caractère optionnel** : Ce composant n'est pas un prérequis de l'architecture. Les capacités déterministes (ex: `GrouchySmurf.executeWatchSurroundings`) l'ignorent complètement, prouvant la flexibilité du design.

### Conclusion architecturale

Le système de personnages implémente un **Pattern Stratégie** à deux niveaux :
1. **Niveau macro** : Chaque `SmurfCharacter` est une stratégie globale de comportement
2. **Niveau micro** : Chaque `CharacterAbility` est une stratégie d'action individuelle, paramétrée par son `actionLogic` lambda

Cette architecture respecte le principe **Ouvert/Fermé** : le système est ouvert à l'extension (nouveaux personnages, nouvelles capacités) mais fermé à la modification (le moteur de jeu n'a pas besoin d'être modifié). L'utilisation de records immutables et de lambdas garantit une séparation claire entre la définition déclarative et l'implémentation procédurale, facilitant la maintenance et l'évolution du projet.