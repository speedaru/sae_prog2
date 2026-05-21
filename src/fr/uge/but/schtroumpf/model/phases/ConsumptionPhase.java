package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.view.Logger;

public class ConsumptionPhase implements GamePhase {
	@Override public PhaseType getType() { return PhaseType.CONSUMPTION_PHASE; }
	
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("started consumption phase");

		ctx.village().decreaseResource(ResourceType.BERRIES, 2);

		Logger.LogTrace("finished consumption phase");
		return new CrisisPhase();
	}
}
