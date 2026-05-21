package fr.uge.but.schtroumpf.model.phases;

import module java.base;

import fr.uge.but.schtroumpf.model.crises.*;
import fr.uge.but.schtroumpf.view.Logger;

public class CrisisPhase implements GamePhase {
	@Override public PhaseType getType() { return PhaseType.CRISIS_PHASE; }
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Logger.LogTrace("started crisis phase");

		// check if too many crises active
		if (ctx.village().isDefeated()) {
			return;
		}
		
		// get active crisis in village
		List<Crisis> activeCrises = new ArrayList<Crisis>();
		for (CrisisType crisisType : CrisisType.values()) {
			if (crisisType.isActive(ctx.village())) {
				activeCrises.add(CrisisType.getCrisis(crisisType));
			}
		}
		
		// display crisis information
	}

	@Override
	public void onExit(GamePhaseContext ctx) {
		Logger.LogTrace("finished crisis phase");
	}

	@Override
	public GamePhase getNextPhase() {
		return null; // end of month
	}
}
