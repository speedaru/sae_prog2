## Système de Sauvegarde (Game Saving)

### Architecture générale

Le système de sauvegarde repose sur deux classes principales : `GameSaveManager` et `GameSave`. La première agit comme un orchestrateur de la sérialisation/désérialisation, tandis que la seconde définit la structure des données persistées sous forme de *Data Transfer Objects* (DTO).

### Utilisation de Jackson pour la sérialisation JSON

La bibliothèque Jackson est utilisée pour convertir l'état du jeu en fichiers JSON. Le `ObjectMapper` est configuré avec l'option `INDENT_OUTPUT` pour produire des fichiers lisibles :

```java
private static ObjectMapper objectMapper = new ObjectMapper();

public static void init() {
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
}
```

La persistance s'effectue via deux méthodes principales :

- **`saveFile(String fileName, GameSave data)`** : écrit le DTO dans un fichier situé dans le répertoire `saves/`. Le chemin est construit selon le format `save_<nom>.json`. Jackson gère automatiquement la conversion des records Java en JSON grâce à ses annotations implicites sur les composants des records.
- **`loadFile(String fileName)`** : lit un fichier JSON et le désérialise en une instance de `GameSave`. Jackson utilise le constructeur canonique des records pour reconstruire les objets.

### Pattern DTO avec le record GameSave

Le record `GameSave` implémente le pattern *Data Transfer Object* : il sert de conteneur purement structurel, sans logique métier, pour transporter l'état du jeu entre la mémoire et le disque. Sa structure hiérarchique reflète la composition du modèle :

```
GameSave
├── EngineState (round, état, phase courante)
└── VillageState
    ├── ressources courantes et précédentes (ResourceMap)
    ├── membres du conseil (CouncilMemberState)
    ├── historique des événements (EventHistory)
    ├── crises actives (CrisisState)
    └── modificateurs (VillageModifierCtxState)
```

Chaque sous-record (`EngineState`, `VillageState`, `CouncilMemberState`, `CrisisState`, `VillageModifierCtxState`, `TemporaryModifierState`) encapsule un sous-ensemble cohérent de l'état. Cette décomposition permet une sérialisation granulaire et facilite l'évolution du format de sauvegarde.

### Hydratation du VillageModifierContext

Le `VillageModifierContext` est reconstruit à partir de l'état sauvegardé via un constructeur dédié qui accepte un `VillageModifierCtxState` :

```java
public VillageModifierContext(GameSave.VillageModifierCtxState state) {
    // chargement des modificateurs persistants
    for (var entry : state.persistentModifiers().entrySet()) {
        persistentModifiers.put(entry.getKey(), entry.getValue());
    }
    // chargement des modificateurs temporaires
    for (var tempState : state.temporaryModifiers()) {
        temporaryModifiers.add(new ModifierEffect(
            tempState.type(),
            tempState.value(),
            tempState.remainingRounds(),
            tempState.isCrisis()
        ));
    }
}
```

Ce constructeur itère sur les deux catégories de modificateurs :

1. **Modificateurs persistants** (`persistentModifiers`) : stockés dans une `Map<GameModifierType, Object>`, ils sont directement recopiés dans la map interne du contexte.
2. **Modificateurs temporaires** (`temporaryModifiers`) : chaque `TemporaryModifierState` est converti en une instance de `ModifierEffect` en conservant le type, la valeur, la durée restante et le drapeau `isCrisis`. Le champ `started` est réinitialisé à `false` par le constructeur de `ModifierEffect`, ce qui garantit que le compte à rebours des tours ne commence qu'à l'entrée du prochain round.

### Flux de sérialisation complet

La méthode `serializeGame(Game game)` orchestre la conversion :

1. **État du moteur** : `serialEngine()` extrait le round courant, l'état du jeu (`VICTORY`, `DEFEAT`, `RUNNING`) et le type de phase active.
2. **État du village** : `serializeVillage()` collecte les ressources (converties de `ResourceSnapshot` en `ResourceMap` via `resourcesSnapToMap`), les membres du conseil (type et énergie), l'historique des événements, les crises actives (uniquement leur type) et l'état des modificateurs via `getModifiersView().serialize()`.

La désérialisation suit le chemin inverse : `deserializeGame()` crée une nouvelle instance de `Game`, puis appelle `game.loadSave(save)` et `village.loadSave(save.villageState())` pour hydrater chaque composant à partir des DTO.

### Gestion des fichiers de sauvegarde

Le `GameSaveManager` expose `getSaveNames()` qui parcourt le répertoire `saves/` à la recherche de fichiers correspondant au motif `save_*.json`. Cette méthode extrait le nom de la sauvegarde en supprimant le préfixe `save_` et le suffixe `.json`, permettant ainsi une interface utilisateur pour charger une partie existante.