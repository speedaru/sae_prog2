package fr.uge.but.schtroumpf.controller.gui.windows;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.nio.file.Path;

import fr.uge.but.schtroumpf.controller.AppController;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.WindowSubController;
import fr.uge.but.schtroumpf.model.types.WindowType;
import fr.uge.but.schtroumpf.view.components.SettingNavigationWidget;
import fr.uge.but.schtroumpf.view.components.SettingToggleWidget;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;
import fr.uge.but.schtroumpf.view.themes.ThemeManager.ResourceTheme;

public class SettingsController implements WindowSubController {
	private static final Path BASE_ICONS_PATH = Path.of("src/main/resources/settings/");

    private AppController router;

    @FXML private VBox settingsListContainer;

	@Override
	public void setRouter(AppController router) {
		this.router = router;
	}

    @FXML
    public void initialize() {
        populateSettings();
    }

	private void populateSettings() {
		settingsListContainer.getChildren().clear();

		SettingToggleWidget colorblindSetting = new SettingToggleWidget(
			"Mode Daltonien",
			"Ajuste les couleurs des interfaces (vert/rouge) pour améliorer la lisibilité pour les joueurs atteints de daltonisme.",
			BASE_ICONS_PATH.resolve("colorblind_icon.png"),
			false,
			(isActivated) -> {
				if (isActivated) {
					ThemeManager.setCurrentTheme(ResourceTheme.COLOR_BLIND);
				}
				else {
					ThemeManager.setCurrentTheme(ResourceTheme.STANDARD);
				}
			}
		);
		
		SettingNavigationWidget saveLoadSetting = new SettingNavigationWidget(
			"Gestion des Sauvegardes",
			"Sauvegardez votre partie actuelle ou chargez une ancienne session de jeu pour reprendre votre village.",
			BASE_ICONS_PATH.resolve("save_icon.png"),
			"Gérer",
			() -> {
				router.navigate(NavigationAction.PUSH, WindowType.SAVE_WINDOW);
			}
		);
		
		settingsListContainer.getChildren().addAll(colorblindSetting, saveLoadSetting);
	}

    @SuppressWarnings("unused")
	private void handleSaveButton(ActionEvent ev) {
    	GameController gameController = router.getWindowController(WindowType.GAME_WINDOW);
    	if (gameController != null) {
    		gameController.saveGame("save1");
    	}
    }
    
    @FXML
    void handleBack(ActionEvent event) {
    	router.navigate(NavigationAction.POP, null);
    }
}
