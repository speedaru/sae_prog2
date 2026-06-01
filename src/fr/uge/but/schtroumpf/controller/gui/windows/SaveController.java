package fr.uge.but.schtroumpf.controller.gui.windows;

import fr.uge.but.schtroumpf.controller.AppController;
import fr.uge.but.schtroumpf.controller.WindowSubController;
import fr.uge.but.schtroumpf.model.save.GameSaveManager;
import fr.uge.but.schtroumpf.model.types.WindowType;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SaveController implements WindowSubController {
	private AppController router;
	
    @FXML private TextField saveNameField;
    @FXML private VBox savesContainer;
    @FXML private Label statusLabel;

	@Override
	public void setRouter(AppController router) {
		this.router = router;
	}

    @FXML
    void handleBack(ActionEvent event) {
    	router.navigate(NavigationAction.POP, null);
    }

    @FXML
    void handleCreateSave(ActionEvent event) {
    	String saveName = saveNameField.getText().toLowerCase();

    	// check if save already exists
    	for (String existingSave : GameSaveManager.getSaveNames()) {
    		if (existingSave.toLowerCase().equals(saveName)) {
    			setStatus(String.format("la sauvegarde '%s' existe deja !", saveName),
    					ThemeManager.getFailColor());
    			return;
    		}
    	}

    	GameController gameController = router.getWindowController(WindowType.GAME_WINDOW);
    	if (gameController == null) {
    		Logger.LogError("failed to get game controller");
    		return;
    	}

		gameController.saveGame(saveName);

		setStatus(String.format("partie '%s' sauvegardée !", saveName),
				ThemeManager.getSuccessColor());
    }

    private void setStatus(String msg, Color color) {
		statusLabel.setText(msg);
		statusLabel.setTextFill(color);
    }
}
