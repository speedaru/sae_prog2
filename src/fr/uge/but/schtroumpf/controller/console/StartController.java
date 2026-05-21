package fr.uge.but.schtroumpf.controller.console;

import fr.uge.but.schtroumpf.controller.SubController;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationResult;
import fr.uge.but.schtroumpf.controller.Navigation.WindowType;
import fr.uge.but.schtroumpf.view.windows.console.StartWindow;
import fr.uge.but.schtroumpf.view.windows.console.StartWindow.Choice;

public class StartController implements SubController {
	private final StartWindow window = new StartWindow();

	@Override
	public NavigationResult handle() {
		window.load();
		Choice choice = Choice.fromCode(window.getUserChoice());

		return handleChoice(choice);
	}
	
	private NavigationResult handleChoice(Choice choice) {
		return switch (choice) {
			case START_GAME -> new NavigationResult(NavigationAction.PUSH, WindowType.GAME_WINDOW);
			case CONTINUE_GAME -> new NavigationResult(NavigationAction.STAY, null);
			case EXIT -> new NavigationResult(NavigationAction.EXIT, null);
		};
	}
}
