package fr.uge.but.schtroumpf.controller.gui.windows;

import javafx.scene.Scene;
import javafx.scene.Parent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

import fr.uge.but.schtroumpf.controller.*;
import fr.uge.but.schtroumpf.controller.Navigation.*;
import fr.uge.but.schtroumpf.model.WindowType;
import fr.uge.but.schtroumpf.view.FxmlUtils;

public class AppController {
    private final Deque<Parent> windowStack = new ArrayDeque<>();
    private final Scene scene;
    
    // cache of all windows pre-loaded at launch
    private final Map<WindowType, Parent> preloadedWindows = new EnumMap<>(WindowType.class);

    public AppController(Scene scene) {
        this.scene = scene;
//        preloadAllWindows();
    }
    
    public void loadStartWindow() {
    	// pushes first scene
        navigate(NavigationAction.PUSH, WindowType.START_WINDOW);
    }
    
    public void navigate(NavigationAction action, WindowType target) {
        switch (action) {
            case PUSH -> {
//            	long start = System.nanoTime();
//                windowStack.push(preloadedWindows.get(target));
                windowStack.push(compileLayout(target));
//            	long end = System.nanoTime();
//            	Logger.LogDebug("time to push window (%s): %.3fms", target.name(), (end - start) / 1_000_000.f);
                updateWindow();
            }
            case POP -> {
                if (!windowStack.isEmpty()) windowStack.pop();
                if (!windowStack.isEmpty()) updateWindow();
                else javafx.application.Platform.exit();
            }
            case REPLACE -> {
                if (!windowStack.isEmpty()) windowStack.pop();
//                windowStack.push(preloadedWindows.get(target));
                windowStack.push(compileLayout(target));
                updateWindow();
            }
            case EXIT -> javafx.application.Platform.exit();
            case STAY -> {}
        }
    }

    private void updateWindow() {
		Parent window = windowStack.peek();
		scene.setRoot(window);
    }
    
    /** parses all FXML files and loads the layouts in memory */
    @SuppressWarnings("unused")
	private void preloadAllWindows() {
		for (WindowType type : WindowType.values()) {
            Parent parsedRoot = compileLayout(type);
            if (parsedRoot != null) {
                preloadedWindows.put(type, parsedRoot);
            }
        }
    }

    /** loads a FXML file into memory */
    private Parent compileLayout(WindowType type) {
    	return FxmlUtils.loadFxmlAndPassController(type.getFxmlFile(), this, (loader, masterController) -> {
			WindowSubController controller = (WindowSubController)loader.getController();
			controller.setRouter(masterController);
    	});
    }
}
