package fr.uge.but.schtroumpf.model.phases;

public interface GamePhase {
	PhaseType getType();
	
	/** triggered automatically when this phase becomes active */
    void onEnter(GamePhaseContext ctx);
    
    /** triggered when the player clicks "valider" to finalize choices before leaving */
    void onExit(GamePhaseContext ctx);
    
    /** factory method that returns the next phase */
    GamePhase getNextPhase();
}
