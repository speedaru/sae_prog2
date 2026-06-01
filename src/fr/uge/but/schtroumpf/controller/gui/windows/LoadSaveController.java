package fr.uge.but.schtroumpf.controller.gui.windows;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import fr.uge.but.schtroumpf.controller.AppController;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.WindowSubController;
import fr.uge.but.schtroumpf.model.save.GameSaveManager;
import fr.uge.but.schtroumpf.model.types.WindowType;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.view.components.LoadSummaryWidget;

public class LoadSaveController implements WindowSubController {
    private AppController router;
    private String selectedSaveName = null;
    private final List<LoadSummaryWidget> widgetsList = new ArrayList<>();

    @FXML private VBox savesContainer;
    @FXML private Button loadButton;

    @Override
    public void setRouter(AppController router) {
        this.router = router;
    }

    @FXML
    public void initialize() {
        refreshSavesList();
        updateLoadButtonState();
    }

    /** gets all save files and renders widget for each save */
    private void refreshSavesList() {
        savesContainer.getChildren().clear();
        widgetsList.clear();
        selectedSaveName = null;
        updateLoadButtonState();

        List<String> saveNames = GameSaveManager.getSaveNames();

        if (saveNames.isEmpty()) {
            Label emptyLabel = new Label("Aucune sauvegarde trouvée.");
            emptyLabel.setTextFill(javafx.scene.paint.Color.web("#64748b"));
            emptyLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontPosture.ITALIC, 13.0));
            savesContainer.getChildren().add(emptyLabel);
            return;
        }

        for (String saveName : saveNames) {
            LoadSummaryWidget widget = new LoadSummaryWidget(saveName);
            
            // set mouse click listener
            widget.setOnMouseClicked(_ -> handleSelectSave(widget));
            
            widgetsList.add(widget);
            savesContainer.getChildren().add(widget);
        }
    }

    private void handleSelectSave(LoadSummaryWidget selectedWidget) {
        this.selectedSaveName = selectedWidget.getSaveName();
        for (LoadSummaryWidget widget : widgetsList) {
            widget.setSelectedState(widget == selectedWidget);
        }
        updateLoadButtonState();
    }

    private void updateLoadButtonState() {
        if (loadButton != null) {
            loadButton.setDisable(selectedSaveName == null);
        }
    }

    @FXML
    void handleLoadSelected(ActionEvent event) {
        if (selectedSaveName == null) {
            return;
        }

        try {
        	// load game window
			router.navigate(NavigationAction.POP, null);
			router.navigate(NavigationAction.PUSH, WindowType.GAME_WINDOW);

			// then load saved game
			GameController gameController = router.getWindowController(WindowType.GAME_WINDOW);
			if (gameController != null) {
				gameController.loadGame(selectedSaveName);
			}
        } catch (Exception e) {
            Logger.LogError("Failed to deserialize selected save: %s", e.getMessage());
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        router.navigate(NavigationAction.POP, null);
    }
}
