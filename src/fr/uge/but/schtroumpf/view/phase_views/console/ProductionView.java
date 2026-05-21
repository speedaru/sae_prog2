package fr.uge.but.schtroumpf.view.phase_views.console;

import fr.uge.but.schtroumpf.model.phases.GamePhaseContext;

public class ProductionView implements PhaseView {
	@Override
	public void display(GamePhaseContext ctx) {
		IO.readln("enter stuff to continue");
	}
}
