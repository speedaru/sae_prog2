package fr.uge.but.schtroumpf.controller;

import javafx.scene.Scene;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

import fr.uge.but.schtroumpf.controller.Navigation.*;
import fr.uge.but.schtroumpf.model.WindowType;
import fr.uge.but.schtroumpf.view.FxmlUtils;
import fr.uge.but.schtroumpf.view.FxmlUtils.FxWindow;

public class AppController {
    private final Deque<AppWindow> windowStack = new ArrayDeque<>();
    private final Scene scene;
    
    // cache of all windows pre-loaded at launch
    private final Map<WindowType, FxWindow<WindowSubController>> preloadedWindows = new EnumMap<>(WindowType.class);

    public AppController(Scene scene) {
        this.scene = scene;
        preloadWindows();
    }
    
    public void loadStartWindow() {
    	// pushes first scene
        navigate(NavigationAction.PUSH, WindowType.START_WINDOW);
    }
    
    public void navigate(NavigationAction action, WindowType target) {
        switch (action) {
            case PUSH -> {
            	// if window is preloaded, load it, otherwise reload from fxml
            	var preloadedWindow = preloadedWindows.getOrDefault(target, null);
            	if (preloadedWindow != null) {
            		windowStack.push(new AppWindow(target, preloadedWindow));
            	}
            	else {
					windowStack.push(new AppWindow(target, compileLayout(target)));
            	}
                updateWindow();
            }
            case POP -> {
                if (!windowStack.isEmpty()) windowStack.pop();
                if (!windowStack.isEmpty()) updateWindow();
                else javafx.application.Platform.exit();
            }
            case REPLACE -> {
                if (!windowStack.isEmpty()) windowStack.pop();
                navigate(NavigationAction.PUSH, target);
            }
            case EXIT -> javafx.application.Platform.exit();
            case STAY -> {}
        }
    }
    
    public WindowSubController getWindowController(WindowType type) {
    	for (AppWindow window : windowStack) {
    		if (window.type() == type) {
    			return window.fxWindow().controller();
    		}
    	}
    	return null;
    }

    private void updateWindow() {
		AppWindow window = windowStack.peek();
		scene.setRoot(window.fxWindow().root());
    }

	private void preloadWindows() {
		preloadWindow(WindowType.SETTINGS_WINDOW);
    }
    
//    /** parses all FXML files and loads the layouts in memory */
//    @SuppressWarnings("unused")
//	private void preloadAllWindows() {
//		for (WindowType type : WindowType.values()) {
//            FxWindow<WindowSubController> parsedWindow = compileLayout(type);
//            if (parsedWindow != null && parsedWindow.root() != null) {
//                preloadedWindows.put(type, parsedWindow.root());
//            }
//        }
//    }

	private void preloadWindow(WindowType type) {
		FxWindow<WindowSubController> parsedWindow = compileLayout(type);
		if (parsedWindow != null && parsedWindow.root() != null) {
			preloadedWindows.put(type, parsedWindow);
		}
    }

    /** loads a FXML file into memory */
    private FxWindow<WindowSubController> compileLayout(WindowType type) {
    	return FxmlUtils.loadFxmlAndPassController(type.getFxmlFile(), this, (loader, masterController) -> {
			WindowSubController controller = (WindowSubController)loader.getController();
			controller.setRouter(masterController);
    	});
    	
    }
    
    private record AppWindow(WindowType type, FxWindow<WindowSubController> fxWindow) {}
}
