# Système de Crise et d'Événements Aléatoires

## Architecture Générale

Le jeu intègre deux systèmes distincts mais complémentaires de perturbations : les **crises** et les **événements aléatoires**. Ces deux mécanismes partagent une philosophie commune : ils modifient l'état du village via des effets temporaires ou immédiats, mais diffèrent dans leur déclenchement et leur cycle de vie.

---

## 1. Système de Crise

### 1.1 Déclenchement d'une Crise

Une crise est déclenchée lorsque la ressource causale (*cause*) atteint zéro. Cette logique est implémentée dans la méthode `shouldTrigger()` de l'énumération `CrisisType` :

```java
public boolean shouldTrigger(SmurfVillage village) {
    return village.getResourceQuantity(this.cause) == 0;
}
```

Chaque type de crise est associé à une ressource spécifique :
- `FAMINE` → `BERRIES`
- `EPIDEMIC` → `SARSAPARILLA`
- `REVOLT` → `MORAL`
- `MASSIVE_ATTACK` → `DEFENSE`
- `DARK_AGES` → `KNOWLEDGE`
- `BANKRUPTCY` → `GOLD`
- `STONE_AGE` → `TOOLS`

### 1.2 Cycle de Vie d'une Crise

Le cycle de vie d'une crise suit un modèle en trois phases :

1. **Détection** : À chaque tour, le système vérifie si une ressource est épuisée. Si c'est le cas, la crise correspondante est activée.

2. **Application des effets immédiats** : Certaines crises appliquent des effets instantanés via la méthode `applyImmediateEffects()`. Par exemple, `MassiveAttackCrisis` réduit immédiatement les outils et l'or du village :
   ```java
   public void applyImmediateEffects(SmurfVillage village) {
       village.updateResource(ResourceType.TOOLS, -1);
       village.updateResource(ResourceType.GOLD, -3);
   }
   ```

3. **Application des modificateurs temporaires** : Chaque crise retourne une liste de `ModifierEffect` via `getModifierEffects()`. Ces effets sont créés avec la méthode statique `crisisModifierEffect()` qui les marque comme étant de type crise et leur attribue une durée d'un tour complet.

### 1.3 Types d'Effets de Crise

Les crises appliquent des modificateurs variés sur les mécaniques du jeu :

| Crise | Modificateur | Valeur | Impact |
|-------|-------------|--------|--------|
| Famine | `MAX_ENERGY_DELTA` | -2 | Réduit l'énergie maximale des Schtroumpfs |
| Épidémie | `EFFICIENCY_DELTA` | -1 | Réduit l'efficacité des actions |
| Révolte | `SUCCESS_CHANCE_BONUS` | -0.25 | Réduit les chances de succès de 25% |
| Attaque Massive | `MAX_ENERGY_DELTA` | -3 | Fatigue extrême des Schtroumpfs |
| Âges Sombres | `PASSIVE_FOOD_PRODUCTION_BLOCKED` | true | Bloque la production passive de nourriture |
| Faillite | `ABILITIES_PER_TURN_DELTA` | -1 | Réduit le nombre d'actions disponibles |
| Âge de Pierre | `PRODUCTION_DELTA` | -1 | Réduit la production globale |

### 1.4 Gestion des Modificateurs

Les modificateurs de crise sont stockés dans `VillageModifierContext` sous forme d'effets temporaires. La classe `ModifierEffect` gère leur cycle de vie avec un compteur de tours restants (`remainingRounds`) et un drapeau `started` qui retarde le début du décompte jusqu'au prochain tour complet.

---

## 2. Système d'Événements Aléatoires

### 2.1 Génération des Événements

La classe `RandomEventGenerator` implémente un système de sélection pondérée basé sur la fréquence de chaque événement :

```java
public static GameEvent nextEvent(int currentRound) {
    int totalFrequency = 0;
    for (var type : availableTypes) {
        totalFrequency += type.calcFrequency(currentRound);
    }
    
    int roll = GameRandomness.randomChoice(0, totalFrequency + 1);
    
    for (var type : availableTypes) {
        roll -= type.calcFrequency(currentRound);
        if (roll <= 0) {
            return GameEvent.fromType(type);
        }
    }
    throw new IllegalStateException(...);
}
```

**Principe de fonctionnement** : La somme des fréquences de tous les événements constitue l'espace de tirage. Un nombre aléatoire est généré dans cet intervalle, puis on soustrait itérativement la fréquence de chaque événement jusqu'à obtenir une valeur négative ou nulle. L'événement correspondant est alors sélectionné.

### 2.2 Fréquence Dynamique

Chaque type d'événement possède une fréquence de base et, optionnellement, un modificateur de fréquence qui évolue avec le numéro du tour. Par exemple, `GargamelAttack` devient plus fréquent au fil du jeu :

```java
public static int getFrequencyModifier(int currentRound) {
    final int START_ROUND = 5;
    final int DELTA_PER_ROUND = 10;
    
    if (currentRound >= START_ROUND) {
        int rounds = currentRound - START_ROUND + 1;
        return rounds * DELTA_PER_ROUND;
    }
    return 0;
}
```

### 2.3 Catalogue des Événements

| Événement | Fréquence de Base | Effets |
|-----------|------------------|--------|
| `GARGAMEL_ATTACK` | 35 (variable) | Perte de défense (-3) et moral (-2) |
| `MAGIC_BERRIES` | 15 | Gain de baies (+2) et salsepareille (+2) |
| `FRIENDLY_VILLAGE` | 15 | Gain d'or (+2), moral conditionnel (+2 si Smurfette a >3 énergie) |
| `SARSAPARILLA_STORM` | 25 | Perte d'outils (-3), gain de connaissance conditionnel (+1 si connaissance ≥3) |
| `SMURF_PARTY` | 20 | Perte de baies (-2), gain de moral conditionnel (+3 si baies ≥2) |
| `FOREST_CURSE` | 30 | Perte de connaissance (-3) |

### 2.4 Exécution des Événements

Tous les événements implémentent l'interface `GameEvent` qui définit la méthode `trigger()`. Cette méthode retourne une liste d'effets de ressources (`ResourceEffect`) qui sont ensuite appliqués au village via `SmurfVillage.applyEffects()`.

**Patron de conception** : Le système utilise le patron **Factory Method** via `GameEvent.fromType()` et `Crisis.fromType()` pour instancier les objets concrets à partir de leur type énuméré.

---

## 3. Interaction avec les Règles du Jeu

### 3.1 Règles de Consommation

L'interface `ConsumptionRule` (définie comme `@FunctionalInterface`) permet de définir des règles de consommation de ressources qui s'appliquent pendant la phase de consommation. Ces règles interagissent avec le système de crise de deux manières :

- **Vérification des ressources** : Les règles de consommation peuvent déclencher des crises si elles réduisent une ressource à zéro.
- **Application des modificateurs** : Les effets de crise modifient les paramètres de consommation (ex : `PRODUCTION_DELTA` réduit la production).

### 3.2 Impact sur les Statistiques du Village

Les crises et événements affectent les statistiques du village via le système de modificateurs de `VillageModifierContext`. Ce système utilise un mécanisme d'**accumulation** où chaque modificateur est combiné avec la valeur existante via la méthode `combine()` du `GameModifierType` :

```java
public void accumulatePersistentEffect(GameModifierType type, Object deltaVal) {
    Object current = persistentModifiers.getOrDefault(type, type.getDefaultValue());
    Object newValue = type.combine(current, deltaVal);
    persistentModifiers.put(type, newValue);
}
```

---

## 4. Considérations Architecturales

### 4.1 Séparation des Préoccupations

Le système sépare clairement :
- **La détection** (dans `CrisisType.shouldTrigger()`)
- **La définition des effets** (dans les classes internes de `Crises`)
- **L'application** (dans `SmurfVillage` et `VillageModifierContext`)

### 4.2 Extensibilité

L'ajout d'une nouvelle crise ou d'un nouvel événement nécessite :
1. Ajouter une constante dans l'énumération correspondante
2. Implémenter l'interface (`Crisis` ou `GameEvent`)
3. Ajouter le cas dans la méthode `fromType()` de l'interface

Cette architecture respecte le principe **Open/Closed** : le système est ouvert à l'extension mais fermé à la modification.