package fr.uge.but.schtroumpf.model.phases;

import module java.base;

import fr.uge.but.schtroumpf.model.events.*;
import fr.uge.but.schtroumpf.model.types.EventHistory;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.model.characters.*;

public class EventPhase implements GamePhase {
	@Override public GamePhaseType getType() { return GamePhaseType.EVENT_PHASE; }
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Logger.LogTrace("started random event phase");

		GameEvent event = RandomEventGenerator.nextEvent(ctx.currentRound());
		
		// get and apply event effects
		List<ResourceEffect> effectsToApply = event.trigger(ctx.village());
		ctx.village().applyEffects(effectsToApply);

		// log event in village history
		ctx.village().recordEvent(
			new EventHistory(event.getEventType(), effectsToApply, ctx.currentRound())
		);
	}

	@Override
	public void onExit(GamePhaseContext ctx) {
		Logger.LogTrace("finished random event phase");
	}

	@Override
	public GamePhase getNextPhase() {
		return new CouncilPhase();
	}
}
