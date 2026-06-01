package fr.uge.but.schtroumpf.controller.gui.windows;

import fr.uge.but.schtroumpf.controller.AppController;
import fr.uge.but.schtroumpf.controller.WindowSubController;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.model.types.WindowType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartMenuController implements WindowSubController {
    @FXML private Button newGameButton;
    @FXML private Button loadGameButton;
    
    private AppController router;

    @Override public void setRouter(AppController router) { this.router = router; }

    @FXML
    public void initialize() {}

    @FXML
    void handleNewGame(ActionEvent event) {
    	router.navigate(NavigationAction.PUSH, WindowType.GAME_WINDOW);
    }

    @FXML
    void handleLoadGame(ActionEvent event) {
    	router.navigate(NavigationAction.PUSH, WindowType.LOAD_SAVE_WINDOW);
//    	router.navigate(NavigationAction.PUSH, WindowType.GAME_WINDOW);
//
//    	GameController gameController = router.getWindowController(WindowType.GAME_WINDOW);
//    	if (gameController != null) {
//    		gameController.loadGame("save1");
//    	}
    }
}
