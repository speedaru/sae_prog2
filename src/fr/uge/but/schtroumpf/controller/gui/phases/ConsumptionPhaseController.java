package fr.uge.but.schtroumpf.controller.gui.phases;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.stream.Collectors;

import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.phases.ConsumptionPhase;
import fr.uge.but.schtroumpf.model.phases.ConsumptionReport;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.view.components.ResourceSummaryRow;

public class ConsumptionPhaseController implements PhaseSubController {
	private GameController masterController;
	private Game game;

    @FXML private VBox consumptionEffectsContainer, penaltyEffectsContainer, statusCardFrame;
    @FXML private HBox statusBadgeHeader;
    @FXML private Button nextPhaseButton;
    @FXML private Label populationLabel, statusMessageLabel, statusTitleLabel;

	@Override
	public void setMasterController(GameController masterController, Game game) {
		this.masterController = masterController;
		this.game = game;

		renderConsumptionReport();
		Logger.LogDebug("passed master controller to consuption phae");
	}

    @FXML
    void handleNextPhaseButton(ActionEvent event) {
    	masterController.advanceTurn();
    }

    private void renderConsumptionReport() {
        consumptionEffectsContainer.getChildren().clear();
        penaltyEffectsContainer.getChildren().clear();

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
            statusCardFrame.setStyle("-fx-background-color: #202225; -fx-border-color: #ef4444; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;");
            statusBadgeHeader.setStyle("-fx-background-color: #ef4444; -fx-background-radius: 6;");
            
            statusTitleLabel.setText(String.format("⚠️ ALERTES : %s", report.seasonName().toUpperCase()));

            String combinedCrisisMessages = report.ruleResults().stream()
                .filter(ConsumptionRuleResult::crisisTriggered)
                .map(ConsumptionRuleResult::crisisMessage)
                .collect(Collectors.joining("\n\n"));
            
            statusMessageLabel.setText(combinedCrisisMessages);

            for (ConsumptionRuleResult result : report.ruleResults()) {
                if (result.crisisTriggered()) {
                    for (ResourceEffect effect : result.effectsApplied()) {
                        if (effect.delta() < 0) {
                            ResourceSummaryRow penaltyRow = new ResourceSummaryRow(effect.resourceType());
                            penaltyRow.updateDelta(effect.delta());
                            penaltyEffectsContainer.getChildren().add(penaltyRow);
                        }
                    }
                }
            }
        } else {
            statusCardFrame.setStyle("-fx-background-color: #202225; -fx-border-color: #3f444c; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;");
            statusBadgeHeader.setStyle("-fx-background-color: #10b981; -fx-background-radius: 6;");
            
            statusTitleLabel.setText(String.format("✅ RAS : %s", report.seasonName().toUpperCase()));
            statusMessageLabel.setText("Toutes les contraintes écologiques et rationnements ont été honorés avec succès. Votre village est en parfaite santé et aucun incident n'est à déplorer ce mois-ci !");
        }

        masterController.updateHudResources();
    }
}
