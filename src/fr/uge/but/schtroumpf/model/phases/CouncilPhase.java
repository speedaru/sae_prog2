package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.view.Logger;

public class CouncilPhase implements GamePhase {
	@Override public PhaseType getType() { return PhaseType.COUNCIL_PHASE; }
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Logger.LogTrace("started council phase");
	}

	@Override
	public void onExit(GamePhaseContext ctx) {
		Logger.LogTrace("finished council phase");
	}

	@Override
	public GamePhase getNextPhase() {
		return new ConsumptionPhase();
	}
}
