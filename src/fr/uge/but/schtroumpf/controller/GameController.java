package fr.uge.but.schtroumpf.controller;

import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationResult;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.*;
import fr.uge.but.schtroumpf.view.windows.GameWindow;
import fr.uge.but.schtroumpf.view.windows.GameWindow.*;

public class GameController implements SubController {
	// constants
	private static final int MAX_ROUNDS = 12;

	private final GameWindow window = new GameWindow();
	private final SmurfVillage village = new SmurfVillage();
	private int round = 1; // represents months
	
	@Override
	public NavigationResult handle() {
		if (round > MAX_ROUNDS) {
			return new NavigationResult(NavigationAction.POP, null);
		}
		
		window.load();
		
		// initial phase is production des ressources
		GamePhase currentPhase = new ProductionPhase();
		
		// execute each phase of the month
		while (currentPhase != null) {
			window.displayHud(new HudContext(round, village));

			var ctx = new GamePhaseContext(window, village, round);
			currentPhase = currentPhase.execute(ctx);
			window.newLine();
		}
		
		postRoundLogic();
		
		return new NavigationResult(NavigationAction.STAY, null);
	}
	
	private void postRoundLogic() {
		village.saveRoundResources();
		village.applyResourceConsumption();
		
		// finally go to next round
		round++;
	}
}
