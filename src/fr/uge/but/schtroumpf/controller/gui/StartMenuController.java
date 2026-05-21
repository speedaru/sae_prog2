package fr.uge.but.schtroumpf.controller.gui;

import fr.uge.but.schtroumpf.controller.FxmlSubController;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.Navigation.WindowType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartMenuController implements FxmlSubController {
    @FXML private Button newGameButton;
    @FXML private Button loadGameButton;
    
    private AppController router;

    @FXML
    public void initialize() {
        System.out.println("Start Menu Loaded Successfully.");
    }

    @FXML
    void handleNewGame(ActionEvent event) {
    	router.navigate(NavigationAction.PUSH, WindowType.GAME_WINDOW);
    }

    @FXML
    void handleLoadGame(ActionEvent event) {
        System.out.println("Loading previous save state...");
        // Add your save parsing initialization here
    }

	@Override
	public void setRouter(AppController router) {
		this.router = router;
	}
}
