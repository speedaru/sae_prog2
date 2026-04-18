package fr.uge.but.schtroumpf.model.phases;

public interface GamePhase {
	/**
     * executes the phase logic
     * @return the next phase to execute, or null if the month (round) is over
     */
    GamePhase execute(GamePhaseContext ctx);
}
