# Core Engine and System Architecture

## 1. Central Router: `AppController`

The `AppController` class (located in `src/fr/uge/but/schtroumpf/controller/AppController.java`) acts as the **central navigation router** for the entire application. It owns a stack of `AppWindow` records, each of which bundles a `WindowType` and a pre‑loaded or dynamically compiled `FxWindow<WindowSubController>`. The stack‑based design mirrors a typical **push‑down automaton** and allows the application to support both modal (push/pop) and non‑modal (replace) transitions.

### 1.1 Navigation Actions

The `Navigation` inner enum (`NavigationAction`) defines five possible actions:

- **`PUSH`** – Places a new window on top of the stack, making it the active view. The window is either retrieved from a pre‑loaded cache (`preloadedWindows`) or compiled on‑the‑fly via `compileLayout()`.
- **`POP`** – Removes the topmost window from the stack. If the stack becomes empty, the application exits.
- **`REPLACE`** – Pops the current window and then pushes the target window, effectively swapping the topmost view without increasing the stack depth.
- **`STAY`** – No navigation occurs; the current window remains unchanged.
- **`EXIT`** – Terminates the JavaFX application immediately.

The `navigate(NavigationAction, WindowType)` method implements the switch logic and calls `updateWindow()` to set the root of the shared `Scene` to the new window’s root node.

### 1.2 Window Pre‑loading

To improve responsiveness, `AppController` pre‑loads a subset of windows at construction time (currently only `SETTINGS_WINDOW`). The `preloadWindows()` method calls `compileLayout()` for each desired type and stores the resulting `FxWindow` in an `EnumMap<WindowType, FxWindow<WindowSubController>>`. When a `PUSH` action targets a pre‑loaded window, the cached instance is used directly, avoiding the overhead of FXML parsing at runtime.

### 1.3 Controller Retrieval

The generic method `getWindowController(WindowType type)` iterates over the current stack and returns the controller of the first window whose type matches the requested type. This allows any part of the system to obtain a reference to a specific window controller (e.g., the `GameController`) without tight coupling.

## 2. Navigation Result Model

The `Navigation` class also defines a record `NavigationResult(NavigationAction action, WindowType target)`. This record is returned by `SubController.handle()` methods (see `SubController.java`) and is consumed by the console‑based `AppController` to decide the next navigation step. The GUI‑based `AppController` does not use this record directly; instead, it relies on the `navigate()` method called from within window controllers.

## 3. Application State: `Game` Class

The `Game` class (located in `src/fr/uge/but/schtroumpf/model/Game.java`) is the **central state manager** for the game logic. It encapsulates:

- The `SmurfVillage` instance, which holds resources, council members, event history, and crisis counters.
- The current round number (`currentRound`), ranging from 1 to `MAX_ROUNDS` (12).
- The current game phase (`currentPhase`), an instance of a class implementing `GamePhase` (e.g., `ProductionPhase`).
- The overall game state (`gameState`), an enum with values `RUNNING`, `VICTORY`, or `DEFEAT`.

### 3.1 Initialisation

`startFirstMonth()` sets the round to 1, initialises the village with default resource quantities (3 units of each `ResourceType`), saves the resource snapshot, and sets the first phase to `ProductionPhase`. This method is called once when a new game begins.

### 3.2 Phase Execution and Advancement

The `executePhaseLogic()` method invokes the `onEnter` callback of the current phase, passing a `GamePhaseContext` that provides access to the `Game`, the `SmurfVillage`, and the current round number. After the phase logic has been executed, `advance()` calls `onExit` on the current phase and then obtains the next phase via `currentPhase.getNextPhase()`. If the next phase is `null`, the month has ended, and `handleMonthEnd()` is invoked.

### 3.3 Month‑End Logic

`handleMonthEnd()` performs the following steps:

1. Checks whether the village has been defeated (three or more crises). If so, the game state is set to `DEFEAT`.
2. Increments the round counter.
3. Checks for victory: if the round exceeds `MAX_ROUNDS`, the game state is set to `VICTORY`.
4. Otherwise, calls `village.prepareNextRound()` to reset per‑round counters and sets the next phase to a new `ProductionPhase`.

### 3.4 Save/Load Support

The `loadSave(GameSave save)` method restores the engine state from a `GameSave.EngineState` record, which contains the current round, game state, and current phase type. This enables the game to be resumed from a previously saved state.

### 3.5 Public API

The `Game` class exposes several getters for the controller layer:

- `getVillage()` – returns the `SmurfVillage` instance.
- `getCurrentRound()` – returns the current month number.
- `getCurrentPhase()` – returns the current `GamePhase` object.
- `getGameState()` – returns the `GameState` enum value.

These methods allow the GUI controllers (e.g., `GameController`) to query the game’s state and update the user interface accordingly.

## 4. Architectural Summary

The architecture follows a **Model‑View‑Controller (MVC)** pattern, where:

- **Model** – `Game`, `SmurfVillage`, `ResourceManager`, and the phase classes represent the game logic and data.
- **View** – JavaFX FXML files and their associated controller classes (e.g., `GameController`, `ProductionPhaseController`) handle user interaction and display.
- **Controller** – `AppController` serves as the top‑level router, while `WindowSubController` and `PhaseSubController` interfaces define the contract for window‑level and phase‑level controllers respectively.

The navigation system, based on a stack of windows and a small set of actions, provides a flexible and maintainable way to manage screen transitions without coupling the view layer to the navigation logic. The `Game` class centralises all mutable game state, ensuring that the model remains consistent and that the view layer can observe changes through well‑defined getters.
