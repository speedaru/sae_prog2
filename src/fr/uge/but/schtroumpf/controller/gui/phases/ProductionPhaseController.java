package fr.uge.but.schtroumpf.controller.gui.phases;

import java.util.Objects;

import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.view.components.ResourceSummaryRow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ProductionPhaseController implements PhaseSubController {
	private GameController masterController;
	private Game game;

	@Override
	public void setMasterController(GameController masterController, Game game) {
		this.masterController = masterController;
		this.game = game;

		// creates resource summary widgets
		loadResourceRows();
	}

    @FXML private VBox resourcesContainer;
    @FXML private Button nextPhaseButton;

    @FXML
    void handleNextPhaseButton(ActionEvent event) {
    	masterController.advanceTurn();
    }
    
    private void loadResourceRows() {
    	Objects.requireNonNull(game, "game was not initialized, please call setMasterController before");
    	resourcesContainer.getChildren().clear();

    	for (ResourceType type : ResourceType.values()) {
			ResourceSummaryRow row = new ResourceSummaryRow(type);
			row.updateDelta(game.getVillage().getResourceDelta(type));

    		resourcesContainer.getChildren().add(row);
        }
    }
}
