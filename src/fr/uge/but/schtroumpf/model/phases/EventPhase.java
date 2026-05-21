package fr.uge.but.schtroumpf.model.phases;

import module java.base;

import fr.uge.but.schtroumpf.model.events.*;
import fr.uge.but.schtroumpf.model.characters.*;
import fr.uge.but.schtroumpf.view.Logger;

public class EventPhase implements GamePhase {
	@Override public PhaseType getType() { return PhaseType.EVENT_PHASE; }
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Logger.LogTrace("started random event phase");

		GameEvent event = RandomEventGenerator.nextEvent();
		
		// get and apply event effects
		List<Effect> effectsToApply = event.trigger(ctx.village());
		for (Effect effect : effectsToApply) {
			ctx.village().applyEffect(effect);
		}

		// log event in village history
		ctx.village().recordEvent(new EventHistory(event.getEventType(), effectsToApply, ctx.currentRound()));
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
