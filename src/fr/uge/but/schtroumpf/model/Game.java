package fr.uge.but.schtroumpf.model;

import java.util.Objects;

import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.phases.GamePhase;
import fr.uge.but.schtroumpf.model.phases.GamePhaseContext;
import fr.uge.but.schtroumpf.model.phases.ProductionPhase;
import fr.uge.but.schtroumpf.view.Logger;

public class Game {
    public enum GameState {
        VICTORY,
        DEFEAT,
        RUNNING,
    }

    private static final int MAX_ROUNDS = 12;

    private final SmurfVillage village = new SmurfVillage();
    private int currentRound;
    private GamePhase currentPhase;
    private GameState gameState;

    /** this method should be called once when a new game begins */
    public void startFirstMonth() {
        this.currentRound = 1;
        this.gameState = GameState.RUNNING;
        village.saveRoundResources();
        currentPhase = new ProductionPhase();
        Logger.LogDebug("game model initialized, starting month 1");
    }

    public void executePhaseLogic(GameController controller) {
        Objects.requireNonNull(controller, "controller cannot be null.");
    	GamePhaseContext ctx = new GamePhaseContext(controller, village, currentRound);

        if (gameState != GameState.RUNNING || currentPhase == null) {
            Logger.LogWarn("ExecutPhaseLogic called but game is not in a runnable state.");
            return;
        }

    	currentPhase.onEnter(ctx);
    }
    
    /**
     * advances the game state by executing the current phase exactly once.
     * this is the core non-blocking "tick" of the game engine. if the execution
     * completes a month, it automatically handles end of month checks and
     * prepares the state for the next month.
     *
     * @param context The context required for the phase execution.
     */
    public void advance(GameController controller) {
        Objects.requireNonNull(controller, "controller cannot be null.");
    	GamePhaseContext ctx = new GamePhaseContext(controller, village, currentRound);

        if (gameState != GameState.RUNNING || currentPhase == null) {
            Logger.LogWarn("Advance called but game is not in a runnable state.");
            return;
        }

        currentPhase.onExit(ctx);
        currentPhase = currentPhase.getNextPhase();

        // check if the month (round) has ended
        if (currentPhase == null) {
            handleMonthEnd();
        }
    }

    // public getters for controller

    public SmurfVillage getVillage() { return village; }
    public int getCurrentRound() { return currentRound; }
    public GamePhase getCurrentPhase() { return currentPhase; }
    public GameState getGameState() { return gameState; }

    private void handleMonthEnd() {
        Logger.LogTrace("end of month %d", currentRound);

        if (village.isDefeated()) {
            this.gameState = GameState.DEFEAT;
            Logger.LogError("Defeat condition met. Game Over.");
            return;
        }

        currentRound++;

        // check for victory condition
        if (currentRound > MAX_ROUNDS) {
            this.gameState = GameState.VICTORY;
            Logger.LogDebug("Victory condition met. All 12 months survived!");
            return;
        }

        Logger.LogDebug("preparing for month %d", currentRound);
        village.saveRoundResources();
        currentPhase = new ProductionPhase();
    }
}
