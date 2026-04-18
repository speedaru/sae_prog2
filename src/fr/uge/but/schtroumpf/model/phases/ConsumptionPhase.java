package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.view.Logger;

public class ConsumptionPhase implements GamePhase {
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("started consumption phase");

		Logger.LogTrace("finished consumption phase");
		return new CrisisPhase();
	}

}
