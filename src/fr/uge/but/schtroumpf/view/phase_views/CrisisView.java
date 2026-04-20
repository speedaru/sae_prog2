package fr.uge.but.schtroumpf.view.phase_views;

import module java.base;

import fr.uge.but.schtroumpf.model.phases.GamePhaseContext;
import fr.uge.but.schtroumpf.view.windows.GameWindow;
import fr.uge.but.schtroumpf.model.crises.*;

public class CrisisView implements PhaseView {
	@Override
	public void display(GamePhaseContext ctx) { }
	
	public void displayActiveCrises(GameWindow window, List<Crisis> activeCrises) {
		window.displayActiveCrises(activeCrises);
	}
}
