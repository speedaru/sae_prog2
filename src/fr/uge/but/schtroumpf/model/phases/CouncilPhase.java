package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.view.Logger;

public class CouncilPhase implements GamePhase {
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("started council phase");

		Logger.LogTrace("finished council phase");
		return new ConsumptionPhase();
	}
}
