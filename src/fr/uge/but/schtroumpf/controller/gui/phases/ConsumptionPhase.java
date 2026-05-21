package fr.uge.but.schtroumpf.controller.gui.phases;

import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.view.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ConsumptionPhase implements PhaseSubController {
	@SuppressWarnings("unused")
	private GameController masterController;
	@SuppressWarnings("unused")
	private Game game;

	@Override
	public void setMasterController(GameController masterController, Game game) {
		this.masterController = masterController;
		this.game = game;
	}

    @FXML private Label placeholderLabel;

    @FXML private VBox resourcesContainer;

    @FXML private Button validerButton;

    @FXML
    void handleValider(ActionEvent event) {
    	Logger.LogDebug("pressed valider ! going to next phase");
    }

}
