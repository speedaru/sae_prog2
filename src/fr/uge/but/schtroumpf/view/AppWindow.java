package fr.uge.but.schtroumpf.view;

import fr.uge.but.schtroumpf.controller.AppController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AppWindow extends Application {
	static final int WINDOW_WIDTH = 1280;
	static final int WINDOW_HEIGHT = 720;
	static final String WINDOW_TITLE = "Village des Schtroumpfs";
	
	Scene scene;
	Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		Pane placeholder = new Pane();
		scene = new Scene(placeholder, WINDOW_WIDTH, WINDOW_HEIGHT);

		primaryStage.setScene(scene);
		primaryStage.setTitle(WINDOW_TITLE);
		primaryStage.show();

		// transition control to the global app controller that handles windows
		AppController appController = new AppController(scene);
		appController.loadStartWindow();
	}

	public void setTitle(String title) {
		primaryStage.setTitle(title);
	}
	
	public void launch() {
        Application.launch(AppWindow.class);
	}
}
