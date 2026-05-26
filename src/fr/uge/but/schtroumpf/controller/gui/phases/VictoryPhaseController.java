package fr.uge.but.schtroumpf.controller.gui.phases;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;

public class VictoryPhaseController implements PhaseSubController {
    private GameController masterController;

    @Override
    public void setMasterController(GameController masterController, Game game) {
        this.masterController = masterController;
    }

    @FXML
    void handleMainMenu(ActionEvent event) {
    	masterController.getRouter().navigate(NavigationAction.POP, null);
    }
}
