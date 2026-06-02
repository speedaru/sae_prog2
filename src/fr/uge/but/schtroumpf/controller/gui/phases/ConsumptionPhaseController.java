package fr.uge.but.schtroumpf.controller.gui.phases;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
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
import fr.uge.but.schtroumpf.model.phases.Season;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.utils.ColorUtils;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.view.components.CoolScrollPane;
import fr.uge.but.schtroumpf.view.components.ResourceSummaryRow;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;

public class ConsumptionPhaseController implements PhaseSubController {
	private GameController masterController;
	private Game game;

    @FXML private VBox reportContainer;
    @FXML private HBox seasonContainer;
    @FXML private ScrollPane reportScrollpane;
    @FXML private Button nextPhaseButton;
    @FXML private Label populationLabel, seasonLabel;

    private ConsumptionReport report;
    Runnable themeUpdater = this::loadTheme;
    
	@Override
	public void setMasterController(GameController masterController, Game game) {
		this.masterController = masterController;
		this.game = game;
		
		renderConsumptionReport();

		// set scroll bar stlyes
		CoolScrollPane.setScrollBarStyle(reportScrollpane);

		// theme updater
        ThemeManager.addThemeChangeListener(themeUpdater);

		Logger.LogDebug("passed master controller to consuption phae");
	}

    @FXML
    void handleNextPhaseButton(ActionEvent event) {
    	masterController.advanceTurn();
    }

    private void renderConsumptionReport() {
        reportContainer.getChildren().clear();

        if (game.getCurrentPhase() == null) {
        	return;
        }

        ConsumptionPhase consumptionPhase = (ConsumptionPhase)game.getCurrentPhase();
        report = consumptionPhase.getCurrentReport();
        if (report == null) {
        	return;
        }

        int populationCount = game.getVillage().getAvailableSmurfs().size();
        populationLabel.setText(String.format("%d Schtroumpfs", populationCount));

        // update season
        updateSeason(report.season());
        
        for (ConsumptionRuleResult result : report.ruleResults()) {
        	updateRuleContainer(result);
        }

        masterController.updateHudResources();
    }
    
    private void loadTheme() {
    	updateSeason(report.season());
    }
    
    private void updateSeason(Season season) {
        seasonContainer.setBackground(new Background(new BackgroundFill(
			ThemeManager.getSeasonColor(season),
			new CornerRadii(8),
			Insets.EMPTY
        )));
        seasonContainer.setBorder(new Border(new BorderStroke(
			ColorUtils.darker(ThemeManager.getSeasonColor(season), 60),
			BorderStrokeStyle.SOLID,
			new CornerRadii(8),
			new BorderWidths(2)
		)));
        seasonLabel.setText(String.format("Saison : %s", season.getName()));
    }
    
    private Label getHeader(String text) {
		Label header = new Label(text.toUpperCase());
		header.setTextFill(Color.web("#91a6c4"));
		header.setFont(Font.font("System", FontWeight.BOLD, 12));
		VBox.setMargin(header, new Insets(6, 0, 0, 0));
		
		return header;
    }
    
    private void updateRuleContainer(ConsumptionRuleResult result) {
		// skip rules that didnt do any modifications
		if (result.effectsApplied().isEmpty()) {
			return;
		}

		// header
		Label header = getHeader(result.ruleName());
		reportContainer.getChildren().add(header);

    	// dont render empty feedback
    	String feedback = result.feedbackMessage();
    	if (!feedback.isEmpty()) {
			// feedback message
			Label content = new Label();
			content.setTextFill(Color.WHITE);
			content.setFont(Font.font("System", FontWeight.BOLD, 11));
			content.setText(feedback);
			content.setWrapText(true);
			content.maxWidthProperty().bind(reportContainer.widthProperty());
//			VBox.setMargin(content, new Insets(0, 0, 0, 12));
			reportContainer.getChildren().add(content);
    	}

		// effects
		for (ResourceEffect effect : result.effectsApplied()) {
			ResourceSummaryRow row = new ResourceSummaryRow(effect.resourceType());
			row.updateDelta(effect.delta());
			reportContainer.getChildren().add(row);
		}
    }
}
