package fr.uge.but.schtroumpf.controller;

import fr.uge.but.schtroumpf.controller.Navigation.NavigationResult;

public interface SubController {
	/**
	 * executes the logic for this specific fxWindow
	 * @return a NavigationResult designing an action to do and a target fxWindow type
	 */
	NavigationResult handle();
}
