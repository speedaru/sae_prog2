package fr.uge.but.schtroumpf.controller.gui.phases;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.phases.ConsumptionPhase;
import fr.uge.but.schtroumpf.model.phases.ConsumptionReport;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.view.components.CoolScrollPane;
import fr.uge.but.schtroumpf.view.components.ResourceSummaryRow;

public class ConsumptionPhaseController implements PhaseSubController {
	private GameController masterController;
	private Game game;

    @FXML private VBox consumptionEffectsContainer, detailsContainer;
    @FXML private ScrollPane detailsScrollpane, effectsScrollpane;
    @FXML private Button nextPhaseButton;
    @FXML private Label populationLabel;

	@Override
	public void setMasterController(GameController masterController, Game game) {
		this.masterController = masterController;
		this.game = game;

		// set scroll bar stlyes
		CoolScrollPane.setScrollBarStyle(effectsScrollpane);
		CoolScrollPane.setScrollBarStyle(detailsScrollpane);
		
		renderConsumptionReport();
		Logger.LogDebug("passed master controller to consuption phae");
	}

    @FXML
    void handleNextPhaseButton(ActionEvent event) {
    	masterController.advanceTurn();
    }

    private void renderConsumptionReport() {
        consumptionEffectsContainer.getChildren().clear();
        detailsContainer.getChildren().clear();

        if (game.getCurrentPhase() == null) {
        	return;
        }

        ConsumptionPhase consumptionPhase = (ConsumptionPhase)game.getCurrentPhase();
        ConsumptionReport report = consumptionPhase.getCurrentReport();
        if (report == null) {
        	return;
        }

        int populationCount = game.getVillage().getAvailableSmurfs().size();
        populationLabel.setText(String.format("%d Schtroumpfs", populationCount));

        for (ConsumptionRuleResult result : report.ruleResults()) {
        	// skip rules that didnt do any modifications
            if (result.effectsApplied().isEmpty()) {
                continue;
            }

            Label ruleHeaderLabel = new Label(result.ruleName().toUpperCase());
            ruleHeaderLabel.setTextFill(Color.web("#64748b")); // Clean muted steel-blue
            ruleHeaderLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
            VBox.setMargin(ruleHeaderLabel, new Insets(6, 0, 2, 0));
            consumptionEffectsContainer.getChildren().add(ruleHeaderLabel);

            for (ResourceEffect effect : result.effectsApplied()) {
                ResourceSummaryRow row = new ResourceSummaryRow(effect.resourceType());
                row.updateDelta(effect.delta());
                consumptionEffectsContainer.getChildren().add(row);
            }
        }

        if (report.hasAnyCrisis()) {
            for (ConsumptionRuleResult result : report.ruleResults()) {
                if (result.crisisTriggered()) {
                    for (ResourceEffect effect : result.effectsApplied()) {
                        if (effect.delta() < 0) {
                            ResourceSummaryRow penaltyRow = new ResourceSummaryRow(effect.resourceType());
                            penaltyRow.updateDelta(effect.delta());
                        }
                    }
                }
            }
        }

        masterController.updateHudResources();
    }
}
