package fr.uge.but.schtroumpf.controller.gui.phases;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Objects;

import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.crises.Crisis;
import fr.uge.but.schtroumpf.view.components.*;

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
        masterController.advanceTurn(); 
    }

    @FXML
    void handleQuit(ActionEvent event) {
    	masterController.getRouter().navigate(NavigationAction.EXIT, null);
    }

    private void renderCrises() {
        crisisCardsContainer.getChildren().clear();

        // get crises
        List<Crisis> activeCrises = game.getVillage().getActiveCrises();

        // update crisis count indicator
        crisisCountBadge.setText(String.format("%d/%d", activeCrises.size(), SmurfVillage.MAX_CRISES));

        // create crisis widgets
        int counter = 0;
        for (Crisis crisis : activeCrises) {
        	if (++counter > 3) {
        		break;
        	}
            CrisisSummaryRow crisisRow = new CrisisSummaryRow(crisis.getType());
            crisisCardsContainer.getChildren().add(crisisRow);
        }
    }

}
