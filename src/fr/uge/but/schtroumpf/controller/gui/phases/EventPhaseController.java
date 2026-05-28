package fr.uge.but.schtroumpf.controller.gui.phases;

import module java.base;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.types.EventHistory;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.view.components.ResourceSummaryRow;

public class EventPhaseController implements PhaseSubController {
	private GameController masterController;
	private Game game;
	
    @FXML private Label eventNameLabel;
    @FXML private VBox negativeEffectsContainer, positiveEffectsContainer;
    @FXML private Button nextPhaseButton;
	
	@Override
	public void setMasterController(GameController masterController, Game game) {
		this.masterController = masterController;
		this.game = game;

		// creates positive and negative resource effects widgets
		loadResourceRows();
	}

    @FXML
    void handleNextPhaseButton(ActionEvent event) {
    	masterController.advanceTurn();
    }

    private void loadResourceRows() {
    	Objects.requireNonNull(game, "game was not initialized, please call setMasterController before");
    	
    	final EventHistory lastEvent = game.getVillage().getLastEvent();
    	if (lastEvent.round() != game.getCurrentRound()) {
    		throw new IllegalStateException("last event doesn't match current round");
    	}
    	
    	eventNameLabel.setText(lastEvent.eventType().getTitle());

		List<ResourceEffect> effectsApplied = lastEvent.effectsApplied();

    	positiveEffectsContainer.getChildren().clear();
    	negativeEffectsContainer.getChildren().clear();

    	// load effects
    	for (var effect : effectsApplied) {
    		int delta = effect.delta();
    		ResourceSummaryRow resourceRow = new ResourceSummaryRow(effect.resourceType());
			resourceRow.updateDelta(delta);
    		
    		if (delta > 0) { // positive effect
    			positiveEffectsContainer.getChildren().add(resourceRow);
    		}
    		else if (delta < 0) { // negative effect
    			negativeEffectsContainer.getChildren().add(resourceRow);
    		}
    	}
    }

}
