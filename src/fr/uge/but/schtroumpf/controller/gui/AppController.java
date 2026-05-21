package fr.uge.but.schtroumpf.controller.gui;

import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import fr.uge.but.schtroumpf.controller.*;
import fr.uge.but.schtroumpf.controller.Navigation.*;
import fr.uge.but.schtroumpf.view.AppWindow;
import fr.uge.but.schtroumpf.view.Logger;

public class AppController {
    private final Deque<Parent> windowStack = new ArrayDeque<>();
    private final Scene scene;

    public AppController(Scene scene) {
        this.scene = scene;
    }
    
    public void loadStartWindow() {
    	// pushes first scene
        navigate(NavigationAction.PUSH, WindowType.START_WINDOW);
    }
    
    public void navigate(NavigationAction action, WindowType target) {
        switch (action) {
            case PUSH -> {
                windowStack.push(createWindow(target));
                updateView();
            }
            case POP -> {
                if (!windowStack.isEmpty()) windowStack.pop();
                if (!windowStack.isEmpty()) updateView();
                else javafx.application.Platform.exit();
            }
            case REPLACE -> {
                if (!windowStack.isEmpty()) windowStack.pop();
                windowStack.push(createWindow(target));
                updateView();
            }
            case EXIT -> javafx.application.Platform.exit();
            case STAY -> {}
        }
    }
    
    /** loads a fxml window from a window type */
    private Parent createWindow(WindowType type) {
		String fxmlPath = switch (type) {
			case START_WINDOW -> "windows/StartWindow.fxml";
			case GAME_WINDOW -> "windows/GameWindow.fxml";
			default -> throw new IllegalArgumentException("Unknown window: " + type);
		};

		try {
			FXMLLoader loader = new FXMLLoader(AppWindow.class.getResource(fxmlPath));
			Parent root = loader.load();

			// pass the router down to the newly loaded sub controller
			try {
				FxmlSubController controller = (FxmlSubController)loader.getController();
				controller.setRouter(this);
			} catch (ClassCastException e) {
				Logger.LogError("%s controller is not a FxmlSubController\n", fxmlPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return root;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
    }

    private void updateView() {
		Parent window = windowStack.peek();
		scene.setRoot(window);
    }
}
