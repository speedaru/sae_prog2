package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.view.Logger;
import fr.uge.but.schtroumpf.view.phase_views.*;

public class ProductionPhase implements GamePhase {
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("entered production phase");

		ProductionView view = new ProductionView();
		view.display(ctx);

		Logger.LogTrace("finished production phase");
		return new EventPhase();
	}
}
