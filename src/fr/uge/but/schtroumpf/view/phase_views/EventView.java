package fr.uge.but.schtroumpf.view.phase_views;

import module java.base;

import fr.uge.but.schtroumpf.model.characters.Effect;
import fr.uge.but.schtroumpf.model.phases.GamePhaseContext;
import fr.uge.but.schtroumpf.view.windows.GameWindow;

public class EventView implements PhaseView {
	@Override
	public void display(GamePhaseContext ctx) {
		ctx.window().displayCurrentEvent(ctx.currentRound(), ctx.village());
	}
	
	public void displayEffectsApplied(GameWindow window, List<Effect> effectsApplied) {
		window.displayEffectsApplied(effectsApplied);
	}
}
