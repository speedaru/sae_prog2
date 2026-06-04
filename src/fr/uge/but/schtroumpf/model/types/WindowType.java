package fr.uge.but.schtroumpf.model.types;

public enum WindowType {
	START_WINDOW(1, "/view/windows/StartWindow.fxml"),
	GAME_WINDOW(2, "/view/windows/GameWindow.fxml"),
	SETTINGS_WINDOW(3, "/view/windows/SettingsWindow.fxml"),
	SAVE_WINDOW(5, "/view/windows/SaveWindow.fxml"),
	LOAD_SAVE_WINDOW(6, "/view/windows/LoadSaveWindow.fxml");

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
