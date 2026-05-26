package fr.uge.but.schtroumpf.model;

import java.nio.file.Path;

public enum WindowType {
	START_WINDOW(1, Path.of("windows/gui/StartWindow.fxml")),
	GAME_WINDOW(2, Path.of("windows/gui/GameWindow.fxml")),
	SETTINGS_WINDOW(3, Path.of("windows/gui/SettingsWindow.fxml")),
	EXIT(4, null);

	private final int code;
	private final Path fxmlFile;

	WindowType(int code, Path fxmlFile) {
		this.code = code;
		this.fxmlFile = fxmlFile;
	}

	@Override
	public String toString() {
		return String.format("WindowType: %s (%s)", this.name(), fxmlFile);
	}

	public int getCode() { return code; }
	public Path getFxmlFile() { return fxmlFile; }
}
