package fr.uge.but.schtroumpf.view;

import java.io.IOException;
import java.nio.file.Path;

import fr.uge.but.schtroumpf.controller.AppController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppWindow extends Application {
	Scene scene;
	Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			Parent root = FxUtils.LoadFxml(Path.of("windows/FuckassFXML.fxml")).load();

			scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			Logger.LogError("stack trace");
		}

//		// transition control to the global app controller
//		AppController app = new AppController();
//        app.launch();
	}

	public void setTitle(String title) {
		primaryStage.setTitle(title);
	}
	
	public void launch() {
        Application.launch(AppWindow.class);
	}
}
