package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.view.Logger;

public class ConsumptionPhase implements GamePhase {
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("started consumption phase");

		ctx.village().decreaseResource(ResourceType.BERRIES, 2);

		Logger.LogTrace("finished consumption phase");
		return new CrisisPhase();
	}

}
