package fr.uge.but.schtroumpf.controller;

import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationResult;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.*;
import fr.uge.but.schtroumpf.view.windows.GameWindow;

public class GameController implements SubController {
	private final GameWindow window = new GameWindow();
	private final SmurfVillage village = new SmurfVillage();
	private int round = 1; // represents months
	
	// constants
	private static final int MAX_ROUNDS = 12;
	
	@Override
	public NavigationResult handle() {
		if (round > MAX_ROUNDS) {
			return new NavigationResult(NavigationAction.POP, null);
		}
		
		window.load();
		
		// initial phase is production des ressources
		GamePhase currentPhase = new ProductionPhase();
		
		while (currentPhase != null) {
			currentPhase = currentPhase.execute(new GamePhaseContext(window, village));
		}
		
		round++;
		return new NavigationResult(NavigationAction.STAY, null);
	}
}
