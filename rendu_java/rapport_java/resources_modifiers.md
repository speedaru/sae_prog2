# Moteur de Ressources et de Modificateurs

## 1. Architecture du `VillageModifierContext`

Le `VillageModifierContext` constitue le cœur du système de règles dynamiques du village. Son architecture repose sur un modèle hybride combinant deux structures de données aux cycles de vie distincts :

- **Une `Map<GameModifierType, Object>` pour les effets persistants** : ces modificateurs s'accumulent par addition via la méthode `accumulatePersistentEffect()`. Ils représentent des altérations durables du jeu, comme les boucliers anti-crises (`CRISIS_SHIELD_COUNT`) ou les bonus d'équipement. Leur valeur est calculée par combinaison additive : chaque appel à `accumulatePersistentEffect` invoque la fonction `combine()` propre à chaque `GameModifierType`, garantissant la cohérence typée des opérations.

- **Une `List<ModifierEffect>` pour les effets temporaires** : chaque effet est encapsulé dans un objet `ModifierEffect` qui porte sa durée résiduelle en tours (`remainingRounds`). Cette liste permet de gérer des effets multiples de même type sans écrasement — par exemple, deux crises actives appliquant simultanément un malus de production.

La méthode générique `get(GameModifierType type)` centralise le calcul de la valeur finale :

```java
public <T> T get(GameModifierType type) {
    Object total = type.getDefaultValue();
    if (persistentModifiers.containsKey(type)) {
        total = type.combine(total, persistentModifiers.get(type));
    }
    for (ModifierEffect effect : temporaryModifiers) {
        if (effect.getType() == type) {
            total = type.combine(total, effect.getValue());
        }
    }
    return (T)total;
}
```

Ce mécanisme exploite le polymorphisme des `GameModifierType` : chaque énumération définit sa propre fonction de combinaison (`addInt`, `addDouble`, `addBool`) et sa valeur par défaut. Le système est ainsi extensible sans modification du contexte — l'ajout d'un nouveau type de modificateur ne nécessite que la déclaration d'une nouvelle constante dans l'énumération.

## 2. Système de Hooks : le Moteur de Règles

`SmurfVillage` expose une série de méthodes qui agissent comme des points d'interception (*hooks*) entre les mécaniques de base du jeu et le contexte de modificateurs. Ces hooks constituent le véritable "moteur de règles" du jeu, permettant aux crises, aux capacités des Schtroumpfs et aux événements d'altérer dynamiquement le comportement du village.

### 2.1 Interception des jets de chance

La méthode `rollChance(double baseChance)` remplace l'appel direct à `GameRandomness.rollChance()` :

```java
public boolean rollChance(double baseChance) {
    double successChanceModifier = modifiers.getDouble(GameModifierType.SUCCESS_CHANCE_BONUS);
    double finalChance = baseChance + successChanceModifier;
    finalChance = Math.clamp(finalChance, 0, 1);
    return GameRandomness.rollChance(finalChance);
}
```

Ce hook permet à une crise comme `RevoltCrisis` d'appliquer un malus de -25% à toutes les chances de succès des capacités, sans que les capacités elles-mêmes aient à connaître l'existence des crises.

### 2.2 Interception des valeurs limites

Plusieurs hooks ajustent les capacités maximales des entités du jeu :

- **`getDynamicMaxEnergy(SmurfCharacter)`** : calcule l'énergie maximale d'un Schtroumpf en fonction du modificateur `MAX_ENERGY_DELTA`. Une `FamineCrisis` réduit cette valeur de 2, limitant le nombre d'actions possibles.

- **`getDynamicMaxAbilitiesPerTurn()`** : détermine le nombre d'actions autorisées par tour. La `BankruptcyCrisis` applique un `ABILITIES_PER_TURN_DELTA` de -1, réduisant la capacité d'action du conseil.

- **`getProductionRate()`** : ajuste le nombre de ressources produites pendant la phase de production. La `StoneAgeCrisis` applique un `PRODUCTION_DELTA` de -1.

### 2.3 Interception des effets de ressources

La méthode `getDynamicEffectDelta(ResourceEffect)` applique le modificateur d'efficacité (`EFFICIENCY_DELTA`) à chaque effet de ressource :

```java
public int getDynamicEffectDelta(ResourceEffect effect) {
    int baseDelta = effect.delta();
    if (baseDelta == 0) return baseDelta;
    int efficiencyModifier = modifiers.getInt(GameModifierType.EFFICIENCY_DELTA);
    if (baseDelta > 0) {
        return Math.max(1, baseDelta + efficiencyModifier);
    }
    return baseDelta + efficiencyModifier;
}
```

Cette logique garantit qu'un effet positif ne peut être réduit à zéro (plancher à 1), préservant ainsi une progression minimale même en période de crise.

## 3. Gestion du Cycle de Vie des Modificateurs

L'intégrité des données au fil des tours est assurée par deux mécanismes complémentaires :

### 3.1 `tickRounds()`

Invoquée à chaque début de tour via `applyActiveCrisesModifiers()`, cette méthode décrémente le compteur de tours de chaque `ModifierEffect` temporaire. Les effets arrivés à expiration sont automatiquement retirés de la liste active. Un mécanisme de *lazy start* (`started` flag) diffère le début du décompte au tour suivant l'application, évitant qu'un effet ne expire prématurément dans le tour où il a été appliqué.

### 3.2 `clearCrisisEffects()`

Cette méthode filtre les effets temporaires pour ne conserver que ceux qui ne proviennent pas d'une crise (`isCrisis() == false`). Elle est utilisée lors de la résolution des crises pour nettoyer les modificateurs des crises précédentes avant d'appliquer ceux des nouvelles crises. Cette isolation garantit qu'une crise ne laisse pas de traces persistantes après sa résolution, tandis que les buffs permanents (acquis via des capacités ou des événements) demeurent actifs.

## 4. Interaction avec le Système de Ressources

### 4.1 `ResourceManager`

Le `ResourceManager` encapsule une `ResourceMap` (extension de `EnumMap<ResourceType, Integer>`) et assure le respect de la contrainte de capacité maximale (`MAX_QUANTITY = 10`). Il expose trois opérations fondamentales :

- `set(ResourceType, int)` : affecte une quantité, avec clamps entre 0 et MAX_QUANTITY
- `add(ResourceType, int)` : incrémente ou décrémente, avec clamps automatique
- `get(ResourceType)` : retourne la quantité courante

### 4.2 Application dynamique des effets

La méthode `applyEffects(List<ResourceEffect>)` dans `SmurfVillage` orchestre l'interaction entre les effets bruts et le moteur de modificateurs :

```java
public void applyEffects(List<ResourceEffect> resourceEffects) {
    for (ResourceEffect effect : resourceEffects) {
        int finalDelta = getDynamicEffectDelta(effect);
        updateResource(effect.resourceType(), finalDelta);
    }
}
```

Chaque effet de capacité ou d'événement passe ainsi par le filtre du modificateur d'efficacité avant d'affecter les ressources. Une capacité produisant normalement +3 baies ne produira que +2 si une `EpidemicCrisis` est active (efficacité -1).

## 5. Collaboration et Modularité

L'architecture décrite permet une séparation nette des préoccupations :

- **Les capacités des Schtroumpfs** (`CharacterAbility`) définissent des effets bruts (ex: "+3 baies") sans connaître l'état des crises.
- **Les crises** (`Crisis`) définissent des modificateurs contextuels (ex: "-1 efficacité") sans connaître les capacités individuelles.
- **Le `VillageModifierContext`** sert de médiateur, combinant les contributions de toutes les sources pour produire les valeurs effectives.
- **Les hooks de `SmurfVillage`** interceptent chaque point d'entrée du jeu pour injecter les modifications.

Cette conception, inspirée du patron *Decorator* appliqué à l'échelle du système de règles, permet d'ajouter de nouvelles crises, capacités ou événements sans modifier les classes existantes — seule la déclaration de nouveaux `GameModifierType` et l'implémentation des hooks correspondants sont nécessaires.