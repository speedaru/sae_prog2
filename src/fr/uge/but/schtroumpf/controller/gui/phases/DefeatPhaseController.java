package fr.uge.but.schtroumpf.controller.gui.phases;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.crises.Crisis;
import fr.uge.but.schtroumpf.view.components.CrisisSummaryRow;

public class DefeatPhaseController implements PhaseSubController {
    private GameController masterController;
    private Game game;

    @FXML private VBox fatalCrisesContainer;

    @Override
    public void setMasterController(GameController masterController, Game game) {
        this.masterController = masterController;
        this.game = game;

        loadFatalCrises();
    }

    private void loadFatalCrises() {
        fatalCrisesContainer.getChildren().clear();

        // loop through the crises that triggered the game over and generate cards
        for (Crisis crisis : game.getVillage().getActiveCrises()) {
            CrisisSummaryRow row = new CrisisSummaryRow(crisis.getType());
            fatalCrisesContainer.getChildren().add(row);
        }
    }

    @FXML
    void handleMainMenu(ActionEvent event) {
    	masterController.getRouter().navigate(NavigationAction.POP, null);
    }

    @FXML
    void handleQuitApp(ActionEvent event) {
    	masterController.getRouter().navigate(NavigationAction.EXIT, null);
    }
}
