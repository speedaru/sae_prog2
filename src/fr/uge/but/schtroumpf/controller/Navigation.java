package fr.uge.but.schtroumpf.controller;

public class Navigation {
	// enum for windows to push/pop 
	public enum WindowType {
		START_WINDOW(1, "windows/StartWindow.fxml"),
		GAME_WINDOW(2, "windows/GameWindow.fxml"),
		SETTINGS_WINDOW(2, null),
		EXIT(2, null);
		
		private final int code;
		private final String fxmlFile;
		
		WindowType(int code, String fxmlFile) {
			this.code = code;
			this.fxmlFile = fxmlFile;
		}
		
		@Override
		public String toString() {
			return String.format("WindowType: %s (%s)", this.name(), fxmlFile);
		}
		
		public int getCode() { return code; }
		public String getFxmlFile() { return fxmlFile; }
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