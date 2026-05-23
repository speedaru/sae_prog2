package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.model.GameRandomness;
import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.view.Logger;

public class ProductionPhase implements GamePhase {
	@Override public PhaseType getType() { return PhaseType.PRODUCTION_PHASE; }
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Logger.LogTrace("entered production phase");
		
		for (ResourceType resourceType : ResourceType.values()) {
			int delta = GameRandomness.randomChoice(1, 4);
			ctx.village().updateResource(resourceType, delta);
		}
	}

	@Override
	public void onExit(GamePhaseContext ctx) {
		Logger.LogTrace("finished production phase");
	}

	@Override
	public GamePhase getNextPhase() {
		return new EventPhase();
	}
}
