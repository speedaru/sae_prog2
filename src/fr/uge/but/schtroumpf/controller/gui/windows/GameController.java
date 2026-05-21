package fr.uge.but.schtroumpf.controller.gui.windows;

import module java.base;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import fr.uge.but.schtroumpf.controller.WindowSubController;
import fr.uge.but.schtroumpf.controller.Navigation.*;
import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.Effect;
import fr.uge.but.schtroumpf.model.crises.*;
import fr.uge.but.schtroumpf.model.phases.*;
import fr.uge.but.schtroumpf.view.FxmlUtils;
import fr.uge.but.schtroumpf.view.Logger;
import fr.uge.but.schtroumpf.view.components.ResourceWidget;

public class GameController implements WindowSubController {
    private AppController router;
    private final Game game = new Game();

    @FXML private Label monthLabel, phaseLabel, eventLabel;
    @FXML private Button mysteriousButton, encyclopediaButton, uiToggleButton, settingsButton, quitButton1;
    @FXML private FlowPane resourcesContainer;
    @FXML private StackPane centerContainer;
    @FXML private Label crisisTitleLabel, crisisCauseLabel, crisisEffectsLabel, crisisPageLabel;
    @FXML private Button prevCrisisBtn, nextCrisisBtn;
    
    private final Map<ResourceType, ResourceWidget> resourceWidgets = new EnumMap<>(ResourceType.class);

    private int currentCrisisPage = 1;

    @Override public void setRouter(AppController router) { this.router = router; }

    @FXML
    public void initialize() {
        game.startFirstMonth();
        
        // init UI
        initResourceWidgets();
        updateHudResource();

        // load first phase
        syncPhaseView();
        game.executePhaseLogic(this); // load initial phase in center
        
        Logger.LogDebug("GameController gui initialized");
    }

    @FXML void handlePrevCrisis(ActionEvent event) {
        if (currentCrisisPage > 1) {
            currentCrisisPage--;
        }
    }

    @FXML void handleNextCrisis(ActionEvent event) {
        // re-calculate total pages on each click to be safe
        long totalPages = Stream.of(CrisisType.values()).filter(t -> t.isActive(game.getVillage())).count();
        if (currentCrisisPage < totalPages) {
            currentCrisisPage++;
        }
    }

    @FXML void handleMysteriousButton(ActionEvent event) {
        Logger.LogDebug("Secret tunnel trigger! Berries added to reserves.");
        game.getVillage().applyEffect(new Effect(ResourceType.BERRIES, 1));
        updateHudResource();
    }

    @FXML void handleQuitButton1(ActionEvent event) {
        Logger.LogDebug("Click quit button, going back to start window");
        router.navigate(NavigationAction.POP, null);
    }

    // unused handlers
    @FXML void handleOpenEncyclopedia(ActionEvent event) { }
    @FXML void handleToggleUI(ActionEvent event) { }
    @FXML void handleOpenSettings(ActionEvent event) { }
    
    /** exposed publicly so phase views can update resources */
    public void updateHudResource() {
        SmurfVillage village = game.getVillage();
        List<ResourceSnapshot> snapshots = village.getResources();
        List<ResourceSnapshot> deltas = village.getResourcesDiff();
        
        for (var snap : snapshots) {
        	ResourceType type = snap.type();
            int delta = getDeltaForType(deltas, type);
            
            ResourceWidget widget = resourceWidgets.get(type);
            if (widget != null) {
                widget.updateState(snap.quantity(), delta);
            }
        }
    }
    
    /** exposed publicly for phase views to call when finished */
    public void advanceTurn() {
        // 2. Run the single phase step logic through our model state machine
        game.advance(this);

        // 3. Re-render resources to immediately reflect deltas on the spot
        updateHudResource();

        // 4. Update the center viewport layout panel to the next phase sequence
        syncPhaseView();
    }
    
    // ------------------------- private helpers
    
    private void initResourceWidgets() {
    	if (resourceWidgets.size() > 0) {
    		resourceWidgets.clear();
    	}

    	for (ResourceType type : ResourceType.values()) {
            ResourceWidget widget = new ResourceWidget(type);
            resourceWidgets.put(type, widget);
            resourcesContainer.getChildren().add(widget);
        }
    }

    private void updateHudPhaseIndicator(GamePhase phase) {
    	phaseLabel.setText(phase.getType().getDisplayName());
    }
    
    /** swaps center pane to load the current phase */
    private void syncPhaseView() {
    	if (game.getGameState() == Game.GameState.DEFEAT) {
    		handleDefeat();
            return;
        }
        if (game.getGameState() == Game.GameState.VICTORY) {
    		handleVictory();
            return;
        }
        
        GamePhase currentPhase = game.getCurrentPhase();
        updateHudPhaseIndicator(currentPhase);

        Path phaseFxmlFile = currentPhase.getType().getFxmlFile();
        Logger.LogDebug("phase fxml: %s", phaseFxmlFile);
        loadCenterView(phaseFxmlFile);
    }
    
    private void loadCenterView(Path fxmlFile) {
    	centerContainer.getChildren().clear();

    	Parent root = FxmlUtils.loadFxmlAndPassController(fxmlFile, this, (loader, masterCtlr) -> {
    		PhaseSubController controller = (PhaseSubController)loader.getController();
    		controller.setMasterController(masterCtlr, this.game);
    	});

    	if (root != null) {
			centerContainer.getChildren().add(root);
    	}
    }
    
    private int getDeltaForType(List<ResourceSnapshot> deltas, ResourceType type) {
    	for (var snap : deltas) {
    		if (snap.type() == type) {
    			return snap.quantity();
    		}
    	}

    	throw new IllegalStateException(String.format("ressource %s not found", type));
    }
    
    private void handleDefeat() {
		Logger.LogError("Village defeated. Returning to main menu.");
    }

    private void handleVictory() {
		Logger.LogDebug("Victory achieved! Popping back to start window.");
    }
}
