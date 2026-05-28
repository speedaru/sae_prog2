package fr.uge.but.schtroumpf.model.phases;

import module java.base;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.crises.*;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
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
				Logger.LogDebug("triggered %s crisis", crisisType.name());
			}
		}
		
		village.setActiveCrises(activeCrises);
		
		if (ctx.currentRound() >= 2) {
			village.getModifiers().addDouble(GameModifierType.EFFICIENCY_MULTIPLIER, 1);
			Logger.LogDebug("efficiency multiplier: %f", village.getModifiers().getDouble(GameModifierType.EFFICIENCY_MULTIPLIER));
		}
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
