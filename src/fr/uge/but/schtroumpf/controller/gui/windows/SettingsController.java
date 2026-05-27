package fr.uge.but.schtroumpf.controller.gui.windows;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.nio.file.Path;

import fr.uge.but.schtroumpf.controller.AppController;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.WindowSubController;
import fr.uge.but.schtroumpf.model.types.WindowType;
import fr.uge.but.schtroumpf.view.components.SettingToggleWidget;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;
import fr.uge.but.schtroumpf.view.themes.ThemeManager.ResourceTheme;

public class SettingsController implements WindowSubController {
    private AppController router;

    @FXML private VBox settingsListContainer;

    private Button saveButton;

	@Override
	public void setRouter(AppController router) {
		this.router = router;
	}

    /** Called automatically by JavaFX after the FXML is loaded */
    @FXML
    public void initialize() {
        populateSettings();
    }

    private void populateSettings() {
        settingsListContainer.getChildren().clear();

        // --- WIDGET 1 : Mode Daltonien (Colorblind Mode) ---
        SettingToggleWidget colorblindSetting = new SettingToggleWidget(
            "Mode Daltonien",
            "Ajuste les couleurs des interfaces (vert/rouge) pour améliorer la lisibilité pour les joueurs atteints de daltonisme.",
            "colorblind_icon.png", // Just place a 64x64 icon in your resources/icons/ folder
            false,                 // Initial state (false by default)
            (isActivated) -> {
            	if (isActivated) {
					ThemeManager.setCurrentTheme(ResourceTheme.COLOR_BLIND);
            	}
            	else {
					ThemeManager.setCurrentTheme(ResourceTheme.STANDARD);
            	}
            }
        );
        
        saveButton = new Button("sauvegarder");
        saveButton.setOnAction(ev -> handleSaveButton(ev));

        // inject the widgets into the fxml layout
        settingsListContainer.getChildren().addAll(colorblindSetting, saveButton);
    }

    private void handleSaveButton(ActionEvent ev) {
    	GameController gameController = router.getWindowController(WindowType.GAME_WINDOW);
    	if (gameController != null) {
    		gameController.saveGame(Path.of("save1.json"));
    	}
    }
    
    @FXML
    void handleBack(ActionEvent event) {
    	router.navigate(NavigationAction.POP, null);
    }
}
