package fr.uge.but.schtroumpf.controller.gui.phases;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.Objects;

import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.crises.Crisis;
import fr.uge.but.schtroumpf.view.components.CrisisSummaryRow;

public class CrisisPhaseController implements PhaseSubController {
    private GameController masterController;
    private Game game;

    @FXML private Label phaseTitleLabel, phaseSubtitleLabel, crisisCountBadge;
    @FXML private VBox safeStateContainer, crisisCardsContainer, gameOverContainer, crisisListWrapper;
    @FXML private HBox gameOverButtonsBox;
    @FXML private Button nextMonthButton;

    @Override
    public void setMasterController(GameController masterController, Game game) {
        this.masterController = Objects.requireNonNull(masterController);
        this.game = Objects.requireNonNull(game);

        renderCrises();
    }

    @FXML
    void handleNextMonth(ActionEvent event) {
        // Automatically resets energy and increments the turn counter in your Game loop
        masterController.advanceTurn(); 
    }

    @FXML
    void handleQuit(ActionEvent event) {
        System.exit(0); // Or route back to a Main Menu if you build one later
    }

    private void renderCrises() {
        crisisCardsContainer.getChildren().clear();

        // get crises and check lose condition
        List<Crisis> activeCrises = game.getVillage().getActiveCrises();
        if (activeCrises.size() > 3) {
        	displayGameOverState();
        	return;
        }

        // update crisis count indicator
        crisisCountBadge.setText(String.format("%d/%d", activeCrises.size(), SmurfVillage.MAX_CRISES));

        // create crisis widgets
        for (Crisis crisis : activeCrises) {
            CrisisSummaryRow crisisRow = new CrisisSummaryRow(crisis.getType());
            crisisCardsContainer.getChildren().add(crisisRow);
        }
    }

    private void displayGameOverState() {
        safeStateContainer.setVisible(false);
        crisisListWrapper.setVisible(false);
        
        gameOverContainer.setVisible(true);
        
        phaseTitleLabel.setText("FIN DE LA PARTIE");
        phaseTitleLabel.setTextFill(Color.web("#ef4444"));
        phaseSubtitleLabel.setText("Le village a succombé aux crises.");
        
        nextMonthButton.setVisible(false);
        nextMonthButton.setManaged(false);
        
        gameOverButtonsBox.setVisible(true);
        gameOverButtonsBox.setManaged(true);
    }
}
