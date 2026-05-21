package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.view.Logger;

public class ConsumptionPhase implements GamePhase {
	@Override public PhaseType getType() { return PhaseType.CONSUMPTION_PHASE; }
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Logger.LogTrace("started consumption phase");

		ctx.village().decreaseResource(ResourceType.BERRIES, 2);

	}

	@Override
	public void onExit(GamePhaseContext ctx) {
		Logger.LogTrace("finished consumption phase");
	}

	@Override
	public GamePhase getNextPhase() {
		return new CrisisPhase();
	}
}
