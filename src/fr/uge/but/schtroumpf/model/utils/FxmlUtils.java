package fr.uge.but.schtroumpf.model.utils;

import module java.base;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class FxmlUtils {
	public static FXMLLoader loadFxml(String file) {
		URL fxml = FxmlUtils.class.getResource(file);
		if (fxml == null) {
			Logger.LogError("can't find fxml file: %s", file);
			throw new IllegalStateException("FXML not found: " + file);
		}

		return new FXMLLoader(fxml);
	}
	
	public static <PC, SC> FxWindow<SC> loadFxmlAndPassController(
			String file,
			PC masterController,
			BiConsumer<FXMLLoader, PC> passControllerCallback
	) {
		try {
			FXMLLoader loader = FxmlUtils.loadFxml(file);
			if (loader == null) {
				return null;
			}

			Parent root = loader.load();
			SC controller = loader.getController();
			
			try {
				passControllerCallback.accept(loader, masterController);
			} catch (ClassCastException e) {
				Logger.LogError("%s controller is invalid type", file);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Logger.LogDebug("loaded %s", file);
			return new FxWindow<SC>(root, controller);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

    public record FxWindow<SC>(Parent root, SC controller) {}
}
