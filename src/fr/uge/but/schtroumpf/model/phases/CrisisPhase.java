package fr.uge.but.schtroumpf.model.phases;

import module java.base;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.crises.*;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class CrisisPhase implements GamePhase {
	@Override public GamePhaseType getType() { return GamePhaseType.CRISIS_PHASE; }
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Logger.LogTrace("started crisis phase");
		SmurfVillage village = ctx.village();

		// get active crisis in village
		List<Crisis> activeCrises = new ArrayList<Crisis>();
		for (CrisisType crisisType : CrisisType.values()) {
			if (crisisType.shouldTrigger(village)) {
				activeCrises.add(Crisis.fromType(crisisType));
				Logger.LogDebug("triggered crisis: %s, cause: %s", crisisType.name(), crisisType.getCause().getDisplayName());
			}
		}
		
		village.applyCrises(activeCrises);
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
