package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.view.Logger;

public class EventPhase implements GamePhase {
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("started random event phase");

		Logger.LogTrace("finished random event phase");
		return new CouncilPhase();
	}
}
