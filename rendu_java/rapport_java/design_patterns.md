# Rapport Technique : Architecture et Design Patterns du Projet Schtroumpf

---

## 1. Architecture Générale : Une Séparation en Trois Couches

Le projet repose sur une architecture **Modèle-Vue-Contrôleur (MVC)** classique, mais avec une particularité fondamentale : le modèle est totalement autonome et ignorant de la vue et du contrôleur.

**Les trois couches :**

- **Modèle** (`model/`) : Cœur du jeu. Contient les règles métier, les entités, le moteur de jeu, les personnages, les crises, les événements et les phases. Aucune dépendance vers JavaFX, System.out ou tout framework d'affichage. Expose uniquement des interfaces et des données brutes.

- **Vue** (`view/`) : Affichage via JavaFX et fichiers FXML. Passive, ne contient aucune logique métier. Affiche les données fournies par le modèle et transmet les actions utilisateur au contrôleur.

- **Contrôleur** (`controller/`) : Pont entre le modèle et la vue. Orchestre la navigation entre les écrans, initialise les parties, traduit les actions utilisateur en appels au modèle.

**Principe fondamental :** Le modèle ne connaît ni la vue ni le contrôleur. Cette séparation stricte a permis la migration d'une version console vers une version GUI sans toucher au noyau du jeu.

---

## 2. Le Pattern "Enum-Factory" : Pilier de la Robustesse

### 2.1 Structure Récurrente

Le projet utilise une structure qui se répète systématiquement pour chaque famille d'entités, composée de trois éléments :

1. **Un Enum** qui définit les constantes de la famille. Par exemple, `SmurfType` définit `GRAND_SMURF`, `HANDY_SMURF`, `SMURFETTE`, etc. Chaque constante porte ses métadonnées : code numérique, nom, description, chemin vers un sprite. L'enum est le point d'entrée unique pour la configuration.

2. **Une Interface** qui définit le contrat que chaque implémentation doit respecter. Par exemple, `SmurfCharacter` définit `getType()`, `getEnergy()`, `getAbilities()`. Cette interface contient une méthode statique `fromType()` qui agit comme une factory.

3. **Des Implémentations concrètes** qui contiennent la logique spécifique à chaque variante. Par exemple, `GrandSmurf`, `HandySmurf`, `Smurfette` implémentent `SmurfCharacter` avec leurs propres capacités et attributs.

### 2.2 Pourquoi ce Pattern est le Pilier du Projet

**A. Type-Safety absolue**

Le compilateur Java garantit qu'on ne peut pas :
- Créer un Schtroumpf à partir d'un type de crise
- Passer un type de phase là où on attend un type d'événement
- Utiliser un code numérique invalide

Chaque famille d'entités a son propre enum, et le compilateur vérifie les correspondances à la compilation.

**B. Centralisation de la configuration**

L'enum est le point d'entrée unique. Pour ajouter un nouveau Schtroumpf :
1. On ajoute une constante dans `SmurfType`
2. On crée la classe correspondante
3. On ajoute un `case` dans la factory

Aucune autre partie du code n'a besoin d'être modifiée.

**C. Extensibilité sans rupture**

Le pattern respecte le principe **Open/Closed** : on peut ajouter de nouvelles variantes sans modifier le code existant. Les boucles `for (SmurfType type : SmurfType.values())` continuent de fonctionner automatiquement après l'ajout d'une nouvelle constante.

### 2.3 Exemples Concrets du Pattern

| Famille | Enum | Interface | Implémentations |
|---------|------|-----------|-----------------|
| Personnages | `SmurfType` | `SmurfCharacter` | GrandSmurf, HandySmurf, Smurfette, GluttonSmurf, GrouchySmurf, BrainySmurf |
| Crises | `CrisisType` | `Crisis` | FamineCrisis, EpidemicCrisis, RevoltCrisis, MassiveAttackCrisis, DarkAgesCrisis, BankruptcyCrisis, StoneAgeCrisis |
| Événements | `GameEventType` | `GameEvent` | GargamelAttack, MagicBerries, FriendlyVillage, SarsaparillaStorm, SmurfParty, ForestCurse |
| Phases | `GamePhaseType` | `GamePhase` | ProductionPhase, EventPhase, CouncilPhase, ConsumptionPhase, CrisisPhase |
| Ressources | `ResourceType` | `CodeEnum` | BERRIES, SARSAPARILLA, GOLD, TOOLS, MORAL, DEFENSE, KNOWLEDGE |

---

## 3. Évolution Console vers GUI : Une Migration Sans Douleur

### 3.1 L'Architecture qui a Anticipé le Changement

Le projet a commencé avec une interface console, puis a migré vers JavaFX. Cette migration a été possible parce que l'architecture avait été pensée dès le départ avec une séparation stricte entre le modèle et la vue.

**Version Console (obsolète) :**
- L'utilisateur interagit avec un contrôleur console
- Affichage texte via une vue console
- Navigation via une boucle `while` qui empile et dépile des sous-contrôleurs
- `AppController` console avec une `Deque<SubController>`

**Version GUI (actuelle) :**
- L'utilisateur interagit avec des composants JavaFX
- Navigation via un système de pile avec actions standardisées (PUSH, POP, REPLACE, EXIT)
- Fenêtres préchargées en mémoire pour des transitions fluides
- `AppController` GUI avec une `Deque<AppWindow>` et un cache de fenêtres

**Ce qui a changé :** le contrôleur et la vue.
**Ce qui est resté inchangé :** le modèle.

### 3.2 Pourquoi le Modèle est Resté Intact

Le modèle expose uniquement :
- Des **interfaces** (`SmurfCharacter`, `GamePhase`, `Crisis`)
- Des **records** (`GameSave`, `ResourceEffect`, `AbilityResult`)
- Des **méthodes métier** (`executePhaseLogic`, `applyEffects`, `rollChance`)

Il ne contient :
- **Aucune référence** à `System.out`, `Scanner` ou `Console`
- **Aucune dépendance** vers JavaFX ou tout framework graphique
- **Aucune logique d'affichage**

Cette pureté du modèle est ce qui a permis la migration sans réécriture.

### 3.3 Leçons Architecturales

La présence des dossiers `controller/console/` et `view/console/` à côté de leurs équivalents GUI n'est pas un défaut. C'est la **preuve que l'architecture a fonctionné** : le modèle a survécu à un changement complet de paradigme d'interface utilisateur. Les dossiers console sont conservés comme témoins de cette évolution, mais ils sont marqués comme obsolètes.

---

## 4. Programmation Fonctionnelle : Les Lambdas comme Moteur Comportemental

### 4.1 Injection de Logique sans Sous-Classes

Le projet utilise des lambdas Java pour injecter du comportement, évitant la multiplication de sous-classes inutiles.

Le record `CharacterAbility` contient :
- Des métadonnées : nom, description, coût en énergie
- Une **lambda** de type `Function<SmurfVillage, AbilityResult>` qui représente la logique d'exécution

Chaque Schtroumpf crée ses capacités dans sa méthode `getAbilities()` en passant une référence de méthode, comme `this::executeCheckSpellBook`.

**Avantages :**
- Pas besoin de créer des classes comme `CheckSpellBookAbility` ou `PlanMeetingAbility`
- La logique reste dans la classe du Schtroumpf qui la comprend
- Les capacités sont des citoyens de première classe : on peut les passer, les stocker, les combiner

### 4.2 Le WeightedOutcomeSelector : Un Mini-Moteur Probabiliste

Le `WeightedOutcomeSelector` permet de chaîner des ajouts de choix pondérés, chacun avec :
- Un poids (probabilité)
- Un type de résultat (SUCCESS, FAILURE, NEUTRAL)
- Un message
- Des effets à appliquer
- Un callback optionnel (lambda)

La méthode `selectAndExecute` choisit un résultat en fonction des poids, exécute le callback correspondant, et retourne le résultat. Ce pattern permet de modéliser des actions à résultats multiples sans structures conditionnelles complexes.

**Exemple conceptuel d'utilisation :**
- 50% de chance de succès : gagner des ressources et augmenter un attribut
- 30% de chance neutre : gagner des ressources sans effet secondaire
- 20% de chance d'échec : perdre des ressources

### 4.3 Les Phases comme Fonctions de Transition

Les phases de jeu suivent un modèle où chaque phase est conceptuellement une **fonction de transition** :
- `onEnter(GamePhaseContext)` : reçoit un contexte et exécute sa logique
- `onExit(GamePhaseContext)` : nettoie avant de passer à la suivante
- `getNextPhase()` : retourne la phase suivante, ou `null` pour signaler la fin du mois

Ceci est conceptuellement proche d'une **machine à états fonctionnelle**, où chaque état est une fonction qui produit l'état suivant.

---

## 5. Gestion des Données : Records et DTOs

### 5.1 Les Records comme Conteneurs de Données

Le projet utilise massivement les records Java pour plusieurs catégories de données :

- **DTOs de sauvegarde** : `GameSave`, `EngineState`, `VillageState`, `CouncilMemberState`, `CrisisState`, `VillageModifierCtxState`, `TemporaryModifierState`
- **Résultats d'actions** : `AbilityResult`, `ResourceEffect`
- **Données historiques** : `EventHistory`
- **Contextes** : `GamePhaseContext`

**Avantages des records :**

1. **Immutabilité** : Garantit que les données ne sont pas modifiées accidentellement après leur création. Évite toute une classe de bugs liés à la mutation d'état.

2. **Constructeur automatique** : Élimine le boilerplate. Pas de risque d'oublier d'initialiser un champ.

3. **Égalité structurelle** : Deux records avec les mêmes valeurs sont égaux. Facilite les tests et les comparaisons.

4. **Sérialisation native** : Compatible Jackson sans configuration. Les noms de champs sont conservés dans le JSON, rendant les fichiers de sauvegarde lisibles et déboguables.

### 5.2 Le Flux de Sauvegarde

Le flux de sauvegarde est un cycle vertueux :

- **Sauvegarde** : État du jeu → `GameSave` (arbres de records) → Fichier JSON
- **Chargement** : Fichier JSON → `GameSave` (arbres de records) → `Game.loadSave()` + `Village.loadSave()`

Ce flux est possible parce que les records sont sérialisables sans effort, immutables, et auto-descriptifs. La sérialisation et la désérialisation sont des opérations inverses parfaites.

### 5.3 ResourceMap comme Conteneur Typé

La classe `ResourceMap` étend `EnumMap<ResourceType, Integer>`, ce qui offre :
- Performance optimale pour les clés enum
- Garantie que seuls des `ResourceType` valides sont utilisés comme clés
- Méthodes héritées pour la manipulation

---

## 6. Extensibilité du Système

### 6.1 Ajout d'un Nouveau Schtroumpf

Pour ajouter un nouveau personnage :
1. Créer une classe implémentant `SmurfCharacter`
2. Ajouter une entrée dans `SmurfType` avec ses métadonnées
3. Ajouter le `case` dans `SmurfCharacter.fromType()`

### 6.2 Ajout d'une Nouvelle Phase

Pour ajouter une phase de jeu :
1. Créer une classe implémentant `GamePhase`
2. Ajouter une entrée dans `GamePhaseType`
3. Définir les transitions dans `getNextPhase()` des phases adjacentes

### 6.3 Ajout d'une Nouvelle Crise

Pour ajouter une crise :
1. Créer une classe implémentant `Crisis`
2. Ajouter une entrée dans `CrisisType` avec sa ressource déclencheuse
3. Ajouter le `case` dans `Crisis.fromType()`

---

## 7. Synthèse : Un Écosystème Modulaire, Testable et Maintenable

### 7.1 Comment les Techniques se Combinent

Les trois techniques principales du projet se combinent pour créer un écosystème cohérent :

- **Enums typés + Factory** → Type-Safety à la compilation, extensibilité sans modification du code existant, configuration centralisée
- **Records comme DTOs** → Sérialisation sans effort, données immutables et prévisibles, tests facilités par l'égalité structurelle
- **Lambdas pour le comportement** → Pas de sous-classes inutiles, logique injectée là où elle est comprise, composition de comportements

**Résultat :**
- Ajouter une entité = 1 enum + 1 classe + 1 case dans la factory
- Tester = créer des instances via la factory et vérifier les résultats
- Maintenir = modifier un seul endroit pour un changement donné
- Faire évoluer = changer la vue sans toucher au modèle

### 7.2 Testabilité

L'architecture rend le projet naturellement testable à plusieurs niveaux :

- **Tests unitaires du modèle** : Créer un `SmurfVillage`, ajouter des ressources, exécuter des capacités, vérifier les effets, sans jamais lancer JavaFX
- **Tests de phases** : Instancier une `ProductionPhase`, appeler `onEnter()` avec un contexte, vérifier les ressources générées
- **Tests de crises** : Vérifier qu'une `FamineCrisis` applique les bons modificateurs
- **Tests de sauvegarde** : Sérialiser un état, le désérialiser, vérifier l'égalité

### 7.3 Maintenabilité

- Un changement dans le modèle n'affecte que le modèle
- Un changement dans la vue n'affecte que la vue
- Un nouveau type s'ajoute en un point unique (l'enum + la factory)
- Une nouvelle règle s'ajoute comme une nouvelle implémentation d'interface

### 7.4 Conclusion

Le projet démontre une maturité architecturale remarquable pour un projet étudiant. La combinaison d'enums typés pour la sécurité, de records pour l'immuabilité, de lambdas pour la flexibilité comportementale, et de MVC strict pour la séparation des préoccupations crée un écosystème où l'ajout de fonctionnalités est prévisible, les tests sont simples, et l'évolution est naturelle.

La preuve ultime de cette robustesse est que le modèle a survécu à une migration complète d'interface, d'une version console vers une version GUI, sans aucune modification. Le modèle ne savait pas et ne voulait pas savoir s'il était affiché dans un terminal ou dans une fenêtre JavaFX. Il s'est contenté de faire son travail : exécuter des règles et produire des données.