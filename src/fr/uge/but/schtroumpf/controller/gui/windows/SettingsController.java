package fr.uge.but.schtroumpf.controller.gui.windows;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import fr.uge.but.schtroumpf.controller.AppController;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.WindowSubController;
import fr.uge.but.schtroumpf.view.components.SettingToggleWidget;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;
import fr.uge.but.schtroumpf.view.themes.ThemeManager.ResourceTheme;

public class SettingsController implements WindowSubController {
    private AppController router;

    @FXML private VBox settingsListContainer;

	@Override
	public void setRouter(AppController router) {
		this.router = router;
	}

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     */
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

//        // --- WIDGET 2 : Animations ---
//        SettingToggleWidget animationSetting = new SettingToggleWidget(
//            "Animations Fluides",
//            "Active ou désactive les effets de transition et d'animation dans le menu.",
//            "animation_icon.png", 
//            true,                 
//            (isActivated) -> {
//                System.out.println("Animations are now: " + isActivated);
//            }
//        );

        // Inject the widgets into the FXML layout
        settingsListContainer.getChildren().addAll(colorblindSetting);
    }

    @FXML
    void handleBack(ActionEvent event) {
    	router.navigate(NavigationAction.POP, null);
    }
}
