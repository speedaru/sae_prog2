package fr.uge.but.schtroumpf.controller;

import module java.base;

import fr.uge.but.schtroumpf.controller.Navigation.NavigationResult;
import fr.uge.but.schtroumpf.controller.Navigation.WindowType;

public class AppController {
	private final Deque<SubController> stack = new ArrayDeque<SubController>();

	public AppController() {
		stack.push(new StartController());
	}

	public void launch() {
		while (!stack.isEmpty()) {
			SubController current = stack.peek();
			NavigationResult result = current.handle();

			switch (result.action()) {
			case PUSH -> stack.push(createController(result.target()));
			case POP -> stack.pop();
			case REPLACE -> {
				stack.pop();
				stack.push(createController(result.target()));
			}
			case STAY -> { /* do nothing loop again */ }
			case EXIT -> stack.clear();
			}
		}
	}

	private SubController createController(WindowType windowType) {
		return switch (windowType) {
		case START_WINDOW -> new StartController();
		case GAME_WINDOW -> new GameController();
		default -> throw new IllegalArgumentException("unknown window type : " + windowType);
		};
	}
}
