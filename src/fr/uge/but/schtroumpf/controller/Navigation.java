package fr.uge.but.schtroumpf.controller;

public class Navigation {
	// enum for windows to push/pop 
	public enum WindowType {
		START_WINDOW,
		GAME_WINDOW,
		SETTINGS_WINDOW,
		EXIT,
	}

	// navigation action : add, remove, change, stay windows 
	public enum NavigationAction {
		PUSH,
		POP,
		REPLACE,
		STAY,
		EXIT,
	}

	// A simple record to hold the action and the next target controller
	public record NavigationResult(NavigationAction action, WindowType target) {}
}