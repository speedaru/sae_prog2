package fr.uge.but.schtroumpf.model.phases;

import module java.base;

import fr.uge.but.schtroumpf.model.crises.*;
import fr.uge.but.schtroumpf.view.Logger;
import fr.uge.but.schtroumpf.view.phase_views.*;

public class CrisisPhase implements GamePhase {
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("started crisis phase");

		// check if too many crises active
		if (ctx.village().isDefeated()) {
			return null;
		}
		
		// get active crisis in village
		List<Crisis> activeCrises = new ArrayList<Crisis>();
		for (CrisisType crisisType : CrisisType.values()) {
			if (crisisType.isActive(ctx.village())) {
				activeCrises.add(CrisisType.getCrisis(crisisType));
			}
		}
		
		// display crisis information
		CrisisView view = new CrisisView();
		view.display(ctx);
		view.displayActiveCrises(ctx.window(), activeCrises);

		Logger.LogTrace("finished crisis phase");
		return null;
	}
}
