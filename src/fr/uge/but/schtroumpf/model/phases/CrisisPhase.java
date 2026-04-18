package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.view.Logger;

public class CrisisPhase implements GamePhase {
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("started crisis phase");

		Logger.LogTrace("finished crisis phase");
		return null;
	}
}
