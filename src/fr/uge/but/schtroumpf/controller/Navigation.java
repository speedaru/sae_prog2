package fr.uge.but.schtroumpf.controller;

import fr.uge.but.schtroumpf.model.types.WindowType;

public class Navigation {
	public enum NavigationAction {
		PUSH,
		POP,
		REPLACE,
		STAY,
		EXIT,
	}

	// record to hold the action and the next target controller
	public record NavigationResult(NavigationAction action, WindowType target) { }
}