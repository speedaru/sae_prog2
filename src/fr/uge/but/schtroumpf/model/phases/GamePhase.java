package fr.uge.but.schtroumpf.model.phases;

public interface GamePhase {
	GamePhaseType getType();
	
	/** triggered automatically when this phase becomes active */
    void onEnter(GamePhaseContext ctx);
    
    /** triggered when the player clicks "valider" to finalize choices before leaving */
    void onExit(GamePhaseContext ctx);
    
    /** factory method that returns the next phase */
    GamePhase getNextPhase();
    
	public static GamePhase fromType(GamePhaseType type) {
		return switch (type) {
			case PRODUCTION_PHASE -> new ProductionPhase();
			case CONSUMPTION_PHASE -> new ConsumptionPhase();
			case COUNCIL_PHASE -> new CouncilPhase();
			case CRISIS_PHASE -> new CrisisPhase();
			case EVENT_PHASE -> new EventPhase();
			default -> throw new IllegalArgumentException("unsported type");
		};
	}
}
