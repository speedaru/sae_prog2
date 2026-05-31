package fr.uge.but.schtroumpf.model.phases;

public interface GamePhase {
	GamePhaseType getType();
	
	/** triggered automatically when phase becomes active */
    void onEnter(GamePhaseContext ctx);
    
    /** triggered before going to next phase */
    void onExit(GamePhaseContext ctx);
    
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
