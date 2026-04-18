package fr.uge.but.schtroumpf.controller;

import fr.uge.but.schtroumpf.controller.Navigation.NavigationResult;

public interface SubController {
	/**
	 * Executes the logic for this specific window
	 * @return a NavigationResult designing an action to do and a target window type
	 */
	NavigationResult handle();
}
