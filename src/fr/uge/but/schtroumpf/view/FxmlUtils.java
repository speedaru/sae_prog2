package fr.uge.but.schtroumpf.view;

import module java.base;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class FxmlUtils {
	public static FXMLLoader loadFxml(Path file) {
		URL fxml = AppWindow.class.getResource(file.toString());
		if (fxml == null) {
			Logger.LogError("can't find: " + fxml);
			return null;
		}

		return new FXMLLoader(fxml);
	}
	
	public static <C> Parent loadFxmlAndPassController(
			Path file,
			C masterController,
			BiConsumer<FXMLLoader, C> passControllerCallback
	) {
		try {
			FXMLLoader loader = FxmlUtils.loadFxml(file);
			if (loader == null) {
				return null;
			}

			Parent root = loader.load();

			try {
				passControllerCallback.accept(loader, masterController);
			} catch (ClassCastException e) {
				Logger.LogError("%s controller is invalid type", file);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Logger.LogDebug("loaded %s", file);
			return root;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
