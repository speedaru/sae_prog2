package fr.uge.but.schtroumpf.controller.gui;

import fr.uge.but.schtroumpf.controller.FxmlSubController;
import fr.uge.but.schtroumpf.view.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class GameController implements FxmlSubController {
	AppController router;

    // === TOP BAR INJECTS ===
    @FXML private Label monthLabel;
    @FXML private Label phaseLabel;
    @FXML private Label eventLabel;
    @FXML private Button mysteriousButton;
    @FXML private Button encyclopediaButton;
    @FXML private Button uiToggleButton;
    @FXML private Button settingsButton;

    // === RESOURCES BAR INJECTS ===
    @FXML private Rectangle berriesBar;
    @FXML private Label berriesText;
    @FXML private Label berriesDeltaLabel;

    @FXML private Rectangle sarsaparillaBar;
    @FXML private Label sarsaparillaText;
    @FXML private Label sarsaparillaDeltaLabel;

    @FXML private Rectangle goldBar;
    @FXML private Label goldText;
    @FXML private Label goldDeltaLabel;

    @FXML private Rectangle moralBar;
    @FXML private Label moralText;
    @FXML private Label moralDeltaLabel;

    // === CENTER INJECTS ===
    @FXML private StackPane centerContainer; // Swap sub-panels inside this layout

    // === CRISIS PANEL INJECTS ===
    @FXML private Label crisisTitleLabel;
    @FXML private Label crisisCauseLabel;
    @FXML private Label crisisEffectsLabel;
    @FXML private Label crisisPageLabel;
    @FXML private Button prevCrisisBtn;
    @FXML private Button nextCrisisBtn;

    // Track active crisis pagination index
    private int currentCrisisPage = 1;
    private final int totalCrisisPages = 3;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Set baseline resource states manually (can be loaded from data models later)
        setResourceProgress(berriesBar, 420.0, 1000.0);
        berriesText.setText("420 / 1000");
        berriesDeltaLabel.setText("+15");

        setResourceProgress(sarsaparillaBar, 525.0, 1000.0);
        sarsaparillaText.setText("525 / 1000");
        sarsaparillaDeltaLabel.setText("-5");

        setResourceProgress(goldBar, 150.0, 1000.0);
        goldText.setText("150 / 1000");
        goldDeltaLabel.setText("+2");

        setResourceProgress(moralBar, 60.0, 100.0);
        moralText.setText("60%");
        moralDeltaLabel.setText("+1%");

        updateCrisisDisplay();
    	Logger.LogDebug("GameController initialized");
    }

	@Override
	public void setRouter(AppController router) {
		this.router = router;
	}

    // === HANDLERS FOR ACTIONS & POPUPS ===

    @FXML
    void handleMysteriousButton(ActionEvent event) {
        System.out.println("Mysterious action trigger!");
        // Add secret mechanics here
    }

    @FXML
    void handleOpenEncyclopedia(ActionEvent event) {
        System.out.println("Opening Encyclopedia pane...");
        // Logic to load Encyclopedia subwindow FXML and replace children in centerContainer
    }

    @FXML
    void handleToggleUI(ActionEvent event) {
        System.out.println("Toggling UI display mode...");
    }

    @FXML
    void handleOpenSettings(ActionEvent event) {
        System.out.println("Opening settings dialog...");
    }

    // === CRISIS PAGINATION HANDLERS ===

    @FXML
    void handlePrevCrisis(ActionEvent event) {
        if (currentCrisisPage > 1) {
            currentCrisisPage--;
            updateCrisisDisplay();
        }
    }

    @FXML
    void handleNextCrisis(ActionEvent event) {
        if (currentCrisisPage < totalCrisisPages) {
            currentCrisisPage++;
            updateCrisisDisplay();
        }
    }

    /**
     * Public helper that your main class or sub-menus can access to clear out 
     * the center workspace and load new screens dynamically.
     */
    public StackPane getCenterContainer() {
        return centerContainer;
    }

    /**
     * Helper to modify the progress bar fills dynamically in JavaFX.
     * Maps progress to the maximum width of the indicator bar background (130px wide).
     */
    private void setResourceProgress(Rectangle bar, double currentValue, double maxValue) {
        double maxBgWidth = 130.0;
        double progressPercentage = currentValue / maxValue;
        if (progressPercentage > 1.0) progressPercentage = 1.0;
        if (progressPercentage < 0.0) progressPercentage = 0.0;

        bar.setWidth(maxBgWidth * progressPercentage);
    }

    private void updateCrisisDisplay() {
        crisisPageLabel.setText(currentCrisisPage + " / " + totalCrisisPages);
        
        switch (currentCrisisPage) {
            case 1:
                crisisTitleLabel.setText("CRISE : BAIES");
                crisisCauseLabel.setText("- Manque de baies criant dans les réserves du village.");
                crisisEffectsLabel.setText("- Malus de moral généralisé (-10%)\n- Risque de famine accru à la fin du cycle.");
                break;
            case 2:
                crisisTitleLabel.setText("CRISE : MAUDITE");
                crisisCauseLabel.setText("- Présence suspecte de pièges à proximité de la clairière.");
                crisisEffectsLabel.setText("- -15% de vitesse de récolte générale.\n- Impossibilité d'envoyer des explorateurs.");
                break;
            case 3:
                crisisTitleLabel.setText("CRISE : ORAGE");
                crisisCauseLabel.setText("- Intempéries dévastatrices sur les toits des habitations.");
                crisisEffectsLabel.setText("- Coût de réparation de l'or doublé.\n- 5% de chance de perdre de la Sarsaparille.");
                break;
        }
    }
}
