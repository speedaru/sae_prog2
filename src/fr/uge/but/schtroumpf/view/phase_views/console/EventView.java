package fr.uge.but.schtroumpf.view.phase_views.console;

import module java.base;

import fr.uge.but.schtroumpf.model.phases.GamePhaseContext;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.view.windows.console.GameWindow;

public class EventView implements PhaseView {
	public void display(GamePhaseContext ctx) {
//		ctx.window().displayCurrentEvent(ctx.currentRound(), ctx.village());
	}
	
	public void displayEffectsApplied(GameWindow window, List<ResourceEffect> effectsApplied) {
		window.displayEffectsApplied(effectsApplied);
	}
}
