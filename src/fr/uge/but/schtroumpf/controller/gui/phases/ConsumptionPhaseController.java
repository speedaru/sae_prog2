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
import fr.uge.but.schtroumpf.model.characters.Effect;
import fr.uge.but.schtroumpf.model.phases.ConsumptionPhase;
import fr.uge.but.schtroumpf.model.phases.ConsumptionReport;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;
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
	}

    @FXML
    void handleNextPhaseButton(ActionEvent event) {
    	masterController.advanceTurn();
    }

	/**
     * Reads the dynamic rule payload from the model phase and cleanly paints the screen graph layout.
     */
    private void renderConsumptionReport() {
        // 1. Defensively clear layout tracks to avoid duplication bugs on refresh
        consumptionEffectsContainer.getChildren().clear();
        penaltyEffectsContainer.getChildren().clear();

        // 2. Safely extract the active model phase reference context
        if (!(game.getCurrentPhase() instanceof ConsumptionPhase consumptionPhase)) {
            throw new IllegalStateException("Controller loaded outside of an active ConsumptionPhase context.");
        }
        ConsumptionReport report = consumptionPhase.getCurrentReport();

        // Update the baseline demographic metrics counter
        int populationCount = game.getVillage().getAvailableSmurfs().size();
        populationLabel.setText(String.format("%d Schtroumpfs", populationCount));

        // ==========================================
        // 3. LEFT PANEL: Dynamic Resource Ledger Generation
        // ==========================================
        for (ConsumptionRuleResult result : report.ruleResults()) {
            // Skip rules that didn't apply any modifications this turn (e.g. Winter rules during Summer)
            if (result.effectsApplied().isEmpty()) {
                continue;
            }

            // Generate a clean sub-header for this specific rule group (Elsa-approved scanning)
            Label ruleHeaderLabel = new Label(result.ruleName().toUpperCase());
            ruleHeaderLabel.setTextFill(Color.web("#64748b")); // Clean muted steel-blue
            ruleHeaderLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
            VBox.setMargin(ruleHeaderLabel, new Insets(6, 0, 2, 0));
            consumptionEffectsContainer.getChildren().add(ruleHeaderLabel);

            // Dynamically instantiate rows for every modified item inside the rule
            for (Effect effect : result.effectsApplied()) {
                ResourceSummaryRow row = new ResourceSummaryRow(effect.resourceType());
                row.updateDelta(effect.delta());
                consumptionEffectsContainer.getChildren().add(row);
            }
        }

        // ==========================================
        // 4. RIGHT PANEL: Global Crisis Context Rendering
        // ==========================================
        if (report.hasAnyCrisis()) {
            // CRITICAL CRISIS ALERTS STATE (High-contrast crimson border for Thierry)
            statusCardFrame.setStyle("-fx-background-color: #202225; -fx-border-color: #ef4444; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;");
            statusBadgeHeader.setStyle("-fx-background-color: #ef4444; -fx-background-radius: 6;");
            
            // Format title explicitly to include the current season token
            statusTitleLabel.setText(String.format("⚠️ ALERTES : %s", report.seasonName().toUpperCase()));

            // Aggregate all active rule failure descriptions cleanly into a single vertical paragraph block
            String combinedCrisisMessages = report.ruleResults().stream()
                .filter(ConsumptionRuleResult::crisisTriggered)
                .map(ConsumptionRuleResult::crisisMessage)
                .collect(Collectors.joining("\n\n"));
            
            statusMessageLabel.setText(combinedCrisisMessages);

            // Populate the separate penalty tracker below the text area for explicit visual feedback
            for (ConsumptionRuleResult result : report.ruleResults()) {
                if (result.crisisTriggered()) {
                    for (Effect effect : result.effectsApplied()) {
                        // Highlight only negative impacts in the crisis panel tracking list
                        if (effect.delta() < 0) {
                            ResourceSummaryRow penaltyRow = new ResourceSummaryRow(effect.resourceType());
                            penaltyRow.updateDelta(effect.delta());
                            penaltyEffectsContainer.getChildren().add(penaltyRow);
                        }
                    }
                }
            }
        } else {
            // NOMINAL PROSPEROUS OPERATION STATE (Cozy emerald green success theme for Elsa)
            statusCardFrame.setStyle("-fx-background-color: #202225; -fx-border-color: #3f444c; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;");
            statusBadgeHeader.setStyle("-fx-background-color: #10b981; -fx-background-radius: 6;");
            
            statusTitleLabel.setText(String.format("✅ RAS : %s", report.seasonName().toUpperCase()));
            statusMessageLabel.setText("Toutes les contraintes écologiques et rationnements ont été honorés avec succès. Votre village est en parfaite santé et aucun incident n'est à déplorer ce mois-ci !");
        }

        // 5. Force background tracking HUD updates to sync remaining storage metrics immediately on the spot
        masterController.updateHudResources();
    }
}
