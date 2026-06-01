# Architecture du Moteur Central et du Système

## 1. Routeur Central : `AppController`

La classe `AppController` (située dans `src/fr/uge/but/schtroumpf/controller/AppController.java`) agit comme le **routeur de navigation central**
de l'application. Elle possède une pile d'enregistrements `AppWindow`, chacun regroupant un `WindowType` et un `FxWindow<WindowSubController>`
préchargé ou compilé dynamiquement. La conception basée sur une pile reflète un **automate à pile** typique et permet à l'application de prendre
en charge à la fois les transitions modales (push/pop) et non modales (replace).

### 1.1 Actions de Navigation

L'énumération interne `NavigationAction` définit cinq actions possibles :

- **`PUSH`** – Place une nouvelle fenêtre au sommet de la pile, la rendant active. La fenêtre est soit récupérée depuis un cache préchargé
(`preloadedWindows`), soit compilée à la volée via `compileLayout()`.
- **`POP`** – Retire la fenêtre la plus haute de la pile. Si la pile devient vide, l'application se ferme.
- **`REPLACE`** – Dépile la fenêtre courante puis pousse la fenêtre cible, remplaçant ainsi la vue la plus haute sans augmenter la profondeur de
la pile.
- **`STAY`** – Aucune navigation n'a lieu ; la fenêtre courante reste inchangée.
- **`EXIT`** – Termine immédiatement l'application JavaFX.

La méthode `navigate(NavigationAction, WindowType)` implémente la logique de commutation et appelle `updateWindow()` pour définir la racine de la
`Scene` partagée sur le nouveau nœud racine de la fenêtre.

### 1.2 Préchargement des Fenêtres

Pour améliorer la réactivité, `AppController` précharge un sous-ensemble de fenêtres au moment de la construction (actuellement uniquement
`SETTINGS_WINDOW`). La méthode `preloadWindows()` appelle `compileLayout()` pour chaque type souhaité et stocke le `FxWindow` résultant dans un
`EnumMap<WindowType, FxWindow<WindowSubController>>`. Lorsqu'une action `PUSH` cible une fenêtre préchargée, l'instance mise en cache est utilisée
directement, évitant ainsi le surcoût de l'analyse FXML à l'exécution.

### 1.3 Récupération du Contrôleur

La méthode générique `getWindowController(WindowType type)` itère sur la pile courante et retourne le contrôleur de la première fenêtre dont le
type correspond au type demandé. Cela permet à n'importe quelle partie du système d'obtenir une référence à un contrôleur de fenêtre spécifique
(par exemple, le `GameController`) sans couplage fort.

## 2. Modèle de Résultat de Navigation

La classe `Navigation` définit également un enregistrement `NavigationResult(NavigationAction action, WindowType target)`. Cet enregistrement est
retourné par les méthodes `SubController.handle()` (voir `SubController.java`) et est consommé par le `AppController` basé sur console pour
décider de la prochaine étape de navigation. Le `AppController` basé sur l'interface graphique n'utilise pas cet enregistrement directement ; il
s'appuie plutôt sur la méthode `navigate()` appelée depuis les contrôleurs de fenêtre.

## 3. État de l'Application : Classe `Game`

La classe `Game` (située dans `src/fr/uge/but/schtroumpf/model/Game.java`) est le **gestionnaire d'état central** pour la logique du jeu. Elle
encapsule :

- L'instance `SmurfVillage`, qui contient les ressources, les membres du conseil, l'historique des événements et les compteurs de crises.
- Le numéro du tour courant (`currentRound`), allant de 1 à `MAX_ROUNDS` (12).
- La phase de jeu courante (`currentPhase`), une instance d'une classe implémentant `GamePhase` (par exemple, `ProductionPhase`).
- L'état global du jeu (`gameState`), une énumération avec les valeurs `RUNNING`, `VICTORY` ou `DEFEAT`.

### 3.1 Initialisation

`startFirstMonth()` définit le tour à 1, initialise le village avec des quantités de ressources par défaut (3 unités de chaque `ResourceType`),
sauvegarde l'instantané des ressources et définit la première phase à `ProductionPhase`. Cette méthode est appelée une fois lorsqu'une nouvelle
partie commence.

### 3.2 Exécution et Avancement des Phases

La méthode `executePhaseLogic()` invoque le rappel `onEnter` de la phase courante, en passant un `GamePhaseContext` qui fournit l'accès au `Game`,
au `SmurfVillage` et au numéro du tour courant. Après l'exécution de la logique de phase, `advance()` appelle `onExit` sur la phase courante puis
obtient la phase suivante via `currentPhase.getNextPhase()`. Si la phase suivante est `null`, le mois est terminé et `handleMonthEnd()` est
invoquée.

### 3.3 Logique de Fin de Mois

`handleMonthEnd()` effectue les étapes suivantes :

1. Vérifie si le village a été vaincu (trois crises ou plus). Si c'est le cas, l'état du jeu est défini à `DEFEAT`.
2. Incrémente le compteur de tours.
3. Vérifie la condition de victoire : si le tour dépasse `MAX_ROUNDS`, l'état du jeu est défini à `VICTORY`.
4. Sinon, appelle `village.prepareNextRound()` pour réinitialiser les compteurs par tour et définit la phase suivante à une nouvelle
`ProductionPhase`.

### 3.4 Support de Sauvegarde/Chargement

La méthode `loadSave(GameSave save)` restaure l'état du moteur à partir d'un enregistrement `GameSave.EngineState`, qui contient le tour courant,
l'état du jeu et le type de phase courante. Cela permet de reprendre la partie à partir d'un état précédemment sauvegardé.

### 3.5 API Publique

La classe `Game` expose plusieurs accesseurs pour la couche de contrôle :

- `getVillage()` – retourne l'instance `SmurfVillage`.
- `getCurrentRound()` – retourne le numéro du mois courant.
- `getCurrentPhase()` – retourne l'objet `GamePhase` courant.
- `getGameState()` – retourne la valeur de l'énumération `GameState`.

Ces méthodes permettent aux contrôleurs de l'interface graphique (par exemple, `GameController`) d'interroger l'état du jeu et de mettre à jour
l'interface utilisateur en conséquence.

## 4. Orchestration du Moteur de Jeu

L'architecture du moteur repose sur une **triade collaborative** où chaque composant possède une responsabilité unique et bien délimitée. Cette séparation garantit un **couplage faible** entre la logique métier, l'état global et la couche de présentation, tout en maintenant une **source de vérité unique** pour chaque domaine de données.

### 4.1 `Game` — L'État Global et le Coordinateur

La classe `Game` (située dans `src/fr/uge/but/schtroumpf/model/Game.java`) agit comme le **point d'entrée unique** pour l'état global de la partie. Elle ne contient **aucune logique métier complexe** ; son rôle est exclusivement celui d'un **orchestrateur** qui :

- Maintient le **compteur de rounds** (`currentRound`) et l'**état de la partie** (`gameState` : `RUNNING`, `VICTORY`, `DEFEAT`).
- Possède une référence unique vers l'instance de `SmurfVillage`, qui constitue le modèle de données du village.
- Gère la **machine à états des phases** : `executePhaseLogic()` invoque le callback `onEnter` de la phase courante, puis `advance()` appelle `onExit` et détermine la phase suivante via `getNextPhase()`.
- Détecte les **conditions de fin de partie** dans `handleMonthEnd()` (défaite si trois crises ou plus, victoire après 12 rounds).

**Principe de délégation** : `Game` ne calcule jamais directement l'impact d'une action. Il transmet un `GamePhaseContext` (contenant `Game`, `SmurfVillage` et le round courant) à chaque phase, qui délègue à son tour à `SmurfVillage` toute manipulation de données.

### 4.2 `SmurfVillage` — Le Cœur de la Logique Métier

La classe `SmurfVillage` (située dans `src/fr/uge/but/schtroumpf/model/SmurfVillage.java`) constitue le **noyau décisionnel** du jeu. Elle centralise l'ensemble des règles métier et expose une API publique que `Game` et les phases consultent sans jamais exposer ses structures internes.

**Responsabilités clés :**

- **Gestion des ressources** : via `ResourceManager`, elle contrôle les quantités, les plafonds (`MAX_QUANTITY = 10`), et les différences entre rounds (`getResourcesDiff()`).
- **Moteur de modificateurs** : le `VillageModifierContext` (`modifiers`) accumule les effets temporaires et persistants. Les méthodes comme `getDynamicEffectDelta()`, `getDynamicMaxEnergy()` ou `getProductionRate()` appliquent ces modificateurs en temps réel, garantissant que toute requête reflète l'état actuel du jeu.
- **Gestion des crises** : `applyCrises()` filtre les crises entrantes via le bouclier (`CRISIS_SHIELD_COUNT`), puis `applyActiveCrisesModifiers()` applique leurs effets au début de chaque round.
- **Système de callbacks** : via `registerCallback()` et `runCallback()`, le village notifie la couche vue des changements de modificateurs sans connaître son existence — un exemple concret de **couplage faible**.

**Single Source of Truth** : Toute donnée dynamique (ressources, énergie des schtroumpfs, modificateurs actifs) est stockée et calculée exclusivement dans `SmurfVillage`. Ni `Game` ni `GameController` ne dupliquent ces informations.

### 4.3 `GameController` — Le Pont entre le Modèle et la Vue

La classe `GameController` (située dans `src/fr/uge/but/schtroumpf/controller/gui/windows/GameController.java`) agit comme un **médiateur** entre le moteur de jeu et l'interface utilisateur JavaFX. Elle ne contient **aucune logique métier** ; son rôle est de :

- **Observer l'état** : elle possède une instance de `Game` et interroge `SmurfVillage` via des getters pour obtenir les ressources, les crises, les modificateurs et les membres du conseil.
- **Déclencher les transitions** : la méthode `advanceTurn()` appelle `game.advance()`, puis vérifie l'état résultant (`VICTORY`, `DEFEAT`, `RUNNING`) pour charger la vue appropriée.
- **Mettre à jour l'interface** : `updateHudResources()`, `updateHudCrisis()` et `updateHudTotalModifiers()` lisent les données depuis `SmurfVillage` et rafraîchissent les widgets JavaFX correspondants.
- **Réagir aux callbacks** : via `registerVillageCallbacks()`, elle s'abonne aux notifications de `SmurfVillage` (notamment `MODIFIERS_UPDATED`) pour mettre à jour la vue sans polling actif.

**Injection de dépendance** : `GameController` reçoit le `AppController` via `setRouter()` et transmet ce dernier aux sous-contrôleurs de phase via `FxmlUtils.loadFxmlAndPassController()`, permettant une navigation modulaire sans couplage direct.

### 4.4 Le Système de Phases — Machine à États du Jeu

Le déroulement d'un tour de jeu est structuré autour d'une **machine à états séquentielle** implémentée par l'interface `GamePhase` et ses concrétisations. Chaque phase représente une étape logique du mois en cours, avec une entrée (`onEnter`), une sortie (`onExit`) et une transition vers la phase suivante (`getNextPhase`).

#### 4.4.1 Contrat de l'Interface `GamePhase`

L'interface `GamePhase` (située dans `src/fr/uge/but/schtroumpf/model/phases/GamePhase.java`) définit trois méthodes fondamentales :

- **`onEnter(GamePhaseContext ctx)`** : Invoquée automatiquement par `Game.executePhaseLogic()` lorsque la phase devient active. C'est ici que la logique métier de la phase est exécutée (production de ressources, déclenchement d'événements, etc.).
- **`onExit(GamePhaseContext ctx)`** : Invoquée par `Game.advance()` avant de passer à la phase suivante. Permet le nettoyage ou la journalisation.
- **`getNextPhase()`** : Retourne l'instance de la phase suivante, ou `null` pour signaler la fin du mois.

La méthode statique `fromType(GamePhaseType type)` agit comme une **fabrique** qui convertit un type énuméré en instance concrète, permettant la désérialisation depuis une sauvegarde.

#### 4.4.2 Séquence des Phases d'un Mois

Chaque mois (round) suit un ordre immuable de cinq phases, défini par les transitions codées dans chaque implémentation :

```
ProductionPhase → EventPhase → CouncilPhase → ConsumptionPhase → CrisisPhase → (fin de mois)
```

1. **`ProductionPhase`** (`src/fr/uge/but/schtroumpf/model/phases/ProductionPhase.java`)
   - **Objectif** : Générer des ressources passives et recharger l'énergie des schtroumpfs.
   - **Logique** : Le village dispose d'un taux de production (`getProductionRate()`) qui détermine le nombre d'unités de ressources à distribuer. Pour chaque unité, une ressource éligible est sélectionnée aléatoirement parmi celles qui ne sont pas saturées (`MAX_QUANTITY = 10`). Les ressources bloquées par des modificateurs (ex. `PASSIVE_FOOD_PRODUCTION_BLOCKED`) sont exclues. En fin de phase, chaque membre du conseil récupère de l'énergie via `village.rechargeSmurfEnergy()`.
   - **Transition** : `getNextPhase()` retourne une nouvelle instance de `EventPhase`.

2. **`EventPhase`** (`src/fr/uge/but/schtroumpf/model/phases/EventPhase.java`)
   - **Objectif** : Déclencher un événement aléatoire qui impacte le village.
   - **Logique** : `RandomEventGenerator.nextEvent(currentRound)` sélectionne un événement parmi les types définis (`GARGAMEL_ATTACK`, `MAGIC_BERRIES`, `FRIENDLY_VILLAGE`, etc.), avec une fréquence de base modulée par le round. L'événement est exécuté via `event.trigger(village)`, qui retourne une liste d'effets sur les ressources. Ces effets sont appliqués via `village.applyEffects()`, et l'événement est enregistré dans l'historique du village (`village.recordEvent()`).
   - **Transition** : `getNextPhase()` retourne une nouvelle instance de `CouncilPhase`.

3. **`CouncilPhase`** (`src/fr/uge/but/schtroumpf/model/phases/CouncilPhase.java`)
   - **Objectif** : Permettre au joueur d'utiliser les capacités spéciales des membres du conseil.
   - **Logique** : Cette phase est **interactive** : elle attend que le joueur sélectionne un schtroumpf et une capacité via l'interface graphique. Le contrôleur de phase (`PhaseSubController`) appelle `village.executeCouncilMemberAbility()` qui vérifie les préconditions (énergie suffisante, limite d'actions non atteinte), consomme l'énergie, applique les effets, et incrémente le compteur d'actions du tour. La limite d'actions est dynamique (`getDynamicMaxAbilitiesPerTurn()`), modulée par les modificateurs actifs.
   - **Transition** : `getNextPhase()` retourne une nouvelle instance de `ConsumptionPhase`.

4. **`ConsumptionPhase`** (`src/fr/uge/but/schtroumpf/model/phases/ConsumptionPhase.java`)
   - **Objectif** : Appliquer les coûts de maintenance et les règles de consommation du village.
   - **Logique** : Un ensemble de `ConsumptionRule` est évalué séquentiellement. Chaque règle (alimentation, surpopulation, chauffage hivernal, décay des infrastructures) examine l'état actuel du village et applique des pénalités si les ressources sont insuffisantes. Par exemple, `FoodRule` consomme des baies proportionnellement à la population, et déclenche une crise si le stock est épuisé. Les résultats sont agrégés dans un `ConsumptionReport` qui liste les effets appliqués et les messages de crise.
   - **Transition** : `getNextPhase()` retourne une nouvelle instance de `CrisisPhase`.

5. **`CrisisPhase`** (`src/fr/uge/but/schtroumpf/model/phases/CrisisPhase.java`)
   - **Objectif** : Évaluer et appliquer les crises déclenchées par les phases précédentes.
   - **Logique** : Pour chaque type de crise défini dans `CrisisType`, la méthode `shouldTrigger(village)` est appelée. Si une condition de déclenchement est remplie (ex. ressources en dessous d'un seuil), une instance de `Crisis` est créée et ajoutée à la liste. `village.applyCrises()` filtre ensuite ces crises via le bouclier (`CRISIS_SHIELD_COUNT`) et les active. Les modificateurs des crises sont appliqués au début du round suivant via `applyActiveCrisesModifiers()`.
   - **Transition** : `getNextPhase()` retourne `null`, signalant la fin du mois.

#### 4.4.3 Gestion de la Fin de Mois

Lorsque `getNextPhase()` retourne `null`, `Game.handleMonthEnd()` est invoqué. Cette méthode :

1. Vérifie la condition de défaite (`village.isDefeated()`, soit 3 crises ou plus).
2. Incrémente le compteur de rounds.
3. Vérifie la condition de victoire (`currentRound > MAX_ROUNDS`, soit 12 mois).
4. Appelle `village.prepareNextRound()` pour sauvegarder l'état des ressources, réinitialiser le compteur d'actions, et appliquer les modificateurs des crises actives.
5. Initialise une nouvelle `ProductionPhase` pour le mois suivant.

#### 4.4.4 Intégration avec la Vue

Chaque phase possède un fichier FXML associé, défini dans l'énumération `GamePhaseType` via `getFxmlFile()`. Lorsque `GameController.syncPhaseView()` détecte un changement de phase, elle charge dynamiquement la vue correspondante dans le panneau central (`centerContainer`) via `FxmlUtils.loadFxmlAndPassController()`. Le contrôleur de phase (`PhaseSubController`) reçoit une référence au `GameController` maître, lui permettant de déclencher `advanceTurn()` une fois l'interaction utilisateur terminée.

**Avantage architectural** : Cette séparation permet d'ajouter ou de modifier une phase sans impacter les autres. Chaque phase est une unité autonome avec sa propre logique, sa propre vue FXML, et son propre contrôleur. La machine à états garantit que l'ordre d'exécution reste cohérent et prévisible.