package fr.uge.but.schtroumpf.view;

import java.net.URL;
import java.nio.file.Path;

import javafx.fxml.FXMLLoader;

public class FxUtils {
	public static FXMLLoader LoadFxml(Path file) {
		URL fxml = FxUtils.class.getResource(file.toString());
		if (fxml == null) {
			Logger.LogError("can't find: " + fxml);
			return null;
		}

		return new FXMLLoader(fxml);
	}
}
