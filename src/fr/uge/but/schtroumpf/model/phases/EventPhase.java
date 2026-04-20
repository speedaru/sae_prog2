package fr.uge.but.schtroumpf.model.phases;

import module java.base;

import fr.uge.but.schtroumpf.model.events.*;
import fr.uge.but.schtroumpf.model.characters.*;
import fr.uge.but.schtroumpf.view.Logger;
import fr.uge.but.schtroumpf.view.phase_views.EventView;

public class EventPhase implements GamePhase {
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("started random event phase");

		GameEvent event = RandomEventGenerator.nextEvent();
		
		// get and apply event effects
		List<Effect> effectsToApply = event.trigger(ctx.village());
		for (Effect effect : effectsToApply) {
			ctx.village().applyEffect(effect);
		}

		// log event in village history
		ctx.village().recordEvent(new EventHistory(event.getEventType(), effectsToApply, ctx.currentRound()));
		
		// display view and effects that were applied
		EventView view = new EventView();
		view.display(ctx);
		view.displayEffectsApplied(ctx.window(), effectsToApply);
		
		Logger.LogTrace("finished random event phase");
		return new CouncilPhase();
	}
}
