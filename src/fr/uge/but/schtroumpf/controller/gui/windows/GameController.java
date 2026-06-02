package fr.uge.but.schtroumpf.controller.gui.windows;

import module java.base;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import fr.uge.but.schtroumpf.controller.WindowSubController;
import fr.uge.but.schtroumpf.controller.Navigation.*;
import fr.uge.but.schtroumpf.controller.AppController;
import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.model.Game.GameState;
import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.crises.*;
import fr.uge.but.schtroumpf.model.phases.*;
import fr.uge.but.schtroumpf.model.save.GameSaveManager;
import fr.uge.but.schtroumpf.model.types.EventHistory;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.types.VillageCallbackType;
import fr.uge.but.schtroumpf.model.types.WindowType;
import fr.uge.but.schtroumpf.model.utils.FxmlUtils;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.model.utils.FxmlUtils.FxWindow;
import fr.uge.but.schtroumpf.view.components.CrisisWidget;
import fr.uge.but.schtroumpf.view.components.GameModifierRow;
import fr.uge.but.schtroumpf.view.components.ResourceSidebarWidget;

public class GameController implements WindowSubController {
    private AppController router;
    private Game game = new Game();

    @FXML private StackPane root;
    @FXML private ImageView backgroundImage;
    @FXML private Label monthLabel, phaseLabel, eventLabel;
    @FXML private ImageView settingsButton, quitButton;
    @FXML private VBox resourcesContainer, crisisContainer, totalModifiersContainer;
    @FXML private StackPane centerContainer;
    @FXML private Label crisisTitleLabel, crisisCauseLabel, crisisEffectsLabel, crisisPageLabel;
    @FXML private Button prevCrisisBtn, nextCrisisBtn;
    
    private final Map<ResourceType, ResourceSidebarWidget> resourceSidebarWidgets = new EnumMap<>(ResourceType.class);
    private int currentCrisisPage = 1;
    
    @SuppressWarnings("unused")
	private PhaseSubController currentPhaseSubController = null;

    @Override public void setRouter(AppController router) { this.router = router; }

    @FXML
    public void initialize() {
		registerVillageCallbacks();
		game.startFirstMonth();
		
		// load first phase
		loadAndExecuteCurrentPhase();

        loadUI();
        Logger.LogDebug("GameController gui initialized");
    }

    @FXML void handlePrevCrisis(ActionEvent event) {
        if (currentCrisisPage > 1) {
            currentCrisisPage--;
            updateHudCrisisPage();
        }
    }

    @FXML void handleNextCrisis(ActionEvent event) {
    	int totalPages = game.getVillage().getActiveCrises().size();
        if (currentCrisisPage < totalPages) {
            currentCrisisPage++;
            updateHudCrisisPage();
        }
    }

    @FXML void handleOpenSettings(MouseEvent event) {
    	router.navigate(NavigationAction.PUSH, WindowType.SETTINGS_WINDOW);
    }

    @FXML void handleQuitButton(MouseEvent event) {
        router.navigate(NavigationAction.POP, null);
    }

    public void saveGame(String saveName) {
		try {
			GameSaveManager.saveGame(game, saveName);
			Logger.LogDebug("saved game %s", saveName);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void loadGame(String saveName) {
    	game = GameSaveManager.loadGame(saveName);
		registerVillageCallbacks();
    	Logger.LogDebug("loaded saved game %s", saveName);
    	
    	loadCurrentPhase();
    	loadUI();
    }

    public void updateHudResources() {
        SmurfVillage village = game.getVillage();
        List<ResourceSnapshot> snapshots = village.getResources();
        List<ResourceSnapshot> deltas = village.getResourcesDiff();
        
        for (var snap : snapshots) {
        	ResourceType type = snap.type();
            int delta = getDeltaForType(deltas, type);
            
            ResourceSidebarWidget widget = resourceSidebarWidgets.get(type);
            if (widget != null) {
                widget.updateState(snap.quantity(), delta);
            }
        }
    }

    /** exposed publicly for phase controllers to call when finished */
    public void advanceTurn() {
    	if (game.getGameState() != GameState.RUNNING) {
    		Logger.LogWarn("can't advance turn because game is not running");
    		return;
    	}
    	
    	// tell game to go to next phase
    	game.advance();

        if (game.getGameState() == GameState.VICTORY) {
        	loadCenterView(GamePhaseType.VICTORY.getFxmlFile());
        }
        else if (game.getGameState() == GameState.DEFEAT) {
        	loadCenterView(GamePhaseType.DEFEAT.getFxmlFile());
        }
        else if (game.getGameState() == GameState.RUNNING) {
    		// execute and load new phase
        	loadAndExecuteCurrentPhase();
    	}
    }
    
    public AppController getRouter() {
    	return router;
    }

    // ------------------------- private helpers
    
    // ------------------------- UI helpers
    
    private void loadUI() {
    	loadBackground();
    	initNavButtons();
        initResourceWidgets();
        
        updateHudResources();
		updateHudCrisis();
    }
    
    private void loadBackground() {
    	Path path = Path.of("src/main/resources/sprites/les-schtroumpfs.png").toAbsolutePath();
    	backgroundImage.setImage(new Image(path.toUri().toString()));
    	
    	backgroundImage.fitWidthProperty().bind(root.widthProperty());
    	backgroundImage.fitHeightProperty().bind(root.heightProperty());
    	backgroundImage.setPreserveRatio(false);
    }
    
    private void initNavButtons() {
    	Path settingsIconPath = Path.of("src/main/resources/icons/settings.png");
    	Path quitIconPath = Path.of("src/main/resources/icons/quit.png");
    	
    	settingsButton.setImage(new Image(settingsIconPath.toUri().toString()));
    	settingsButton.setOnMouseClicked(this::handleOpenSettings);

    	quitButton.setImage(new Image(quitIconPath.toUri().toString()));
    	quitButton.setOnMouseClicked(this::handleQuitButton);
    	
    	Logger.LogDebug("seetings icon path: %s", settingsIconPath.toUri().toString());
    }
    
    private void initResourceWidgets() {
		resourcesContainer.getChildren().clear();
    	if (resourceSidebarWidgets.size() > 0) {
    		resourceSidebarWidgets.clear();
    	}

    	for (ResourceType type : ResourceType.values()) {
            ResourceSidebarWidget widget = new ResourceSidebarWidget(type);
            resourceSidebarWidgets.put(type, widget);
            resourcesContainer.getChildren().add(widget);
        }
    }
    
    private void registerVillageCallbacks() {
    	// callback for updating modifiers
		game.getVillage().registerCallback(VillageCallbackType.MODIFIERS_UPDATED, () -> {
			updateHudCrisis();
			updateHudTotalModifiers();
		});
		
		Logger.LogTrace("registered village callbacks");
    }

    private void updateHudRoundIndicator(int round) {
    	monthLabel.setText(String.format("%d (%s)", round, getMonthFromNumber(round)));
    }

    private void updateHudPhaseIndicator(GamePhase phase) {
    	phaseLabel.setText(phase.getType().getDisplayName());
    }
    
    private void updateHudEventIndicator() {
    	SmurfVillage village = game.getVillage();
    	int currentRound = game.getCurrentRound();
    	
    	EventHistory event = village.getEventFromRound(currentRound);
    	String eventStr = "Aucun";
    	if (event != null) {
    		eventStr = event.eventType().getTitle();
    	}
    	
    	eventLabel.setText(String.format("Evenement: %s", eventStr));
    }

    private void updateHudCrisis() {
    	updateHudCrisisPage();
        
        // update total modifiers list
        updateHudTotalModifiers();
    }
    
    /** also updates navigation buttons */
    private void updateHudCrisisPage() {
        crisisContainer.getChildren().clear();

        SmurfVillage village = game.getVillage();
        List<Crisis> activeCrises = village.getActiveCrises();

        // zero crises
        if (activeCrises.isEmpty()) {
        	loadCrisisEmptyView();
            return;
        }

        int totalPages = activeCrises.size();
        updateHudCrisisNavBar(totalPages);
        
        Crisis currentCrisis = activeCrises.get(currentCrisisPage - 1);

        // load crisis widget
        CrisisWidget crisisWidget = new CrisisWidget(currentCrisis);
        crisisContainer.getChildren().add(crisisWidget);
    }
    
    private void updateHudCrisisNavBar(int totalPages) {
        if (currentCrisisPage > totalPages) {
            currentCrisisPage = totalPages;
        }
        if (currentCrisisPage < 1) {
            currentCrisisPage = 1;
        }

        crisisPageLabel.setText(currentCrisisPage + " / " + totalPages);

        prevCrisisBtn.setDisable(currentCrisisPage == 1);
        nextCrisisBtn.setDisable(currentCrisisPage == totalPages);
    }
    
    public void updateHudTotalModifiers() {
        totalModifiersContainer.getChildren().clear();

        SmurfVillage village = game.getVillage();
        
        boolean hasModifiers = false;
        
        for (GameModifierType modType : GameModifierType.values()) {
            Object currentValue = village.getModifier(modType);
            Object defaultValue = modType.getDefaultValue();

            // ignore default values
            if (currentValue.equals(defaultValue)) {
                continue;
            }

            hasModifiers = true;
            
            GameModifierRow row = new GameModifierRow(modType, currentValue);
            totalModifiersContainer.getChildren().add(row);
        }

        // clean view if the village has no active penalties
        if (!hasModifiers) {
            Label happyLabel = new Label("Aucun modificateur");
            happyLabel.setTextFill(javafx.scene.paint.Color.web("#64748b"));
            happyLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontPosture.ITALIC, 12.0));
            happyLabel.setPadding(new javafx.geometry.Insets(10, 0, 0, 4));
            
            totalModifiersContainer.getChildren().add(happyLabel);
        }
    }

    private void loadCrisisEmptyView() {
    	currentCrisisPage = 1;
    	crisisPageLabel.setText("0 / 0");
    	prevCrisisBtn.setDisable(true);
    	nextCrisisBtn.setDisable(true);

    	Label calmLabel = new Label("le village est calme");
    	calmLabel.setTextFill(javafx.scene.paint.Color.web("#64748b"));
    	calmLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontPosture.ITALIC, 13));
    	calmLabel.setPadding(new javafx.geometry.Insets(30, 0, 0, 0));

    	crisisContainer.getChildren().add(calmLabel);
    }

    // ------------------------- phase related UI helpers

    private void loadCurrentPhase() {
        syncPhaseView();

        updateHudResources();
        
//        if (game.getCurrentPhase().getType() == GamePhaseType.CRISIS_PHASE) {
//        	updateHudCrisis();
//        }
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
        updateHudRoundIndicator(game.getCurrentRound());
        updateHudEventIndicator();

        Path phaseFxmlFile = currentPhase.getType().getFxmlFile();
        loadCenterView(phaseFxmlFile);
    }
    
    private void loadCenterView(Path fxmlFile) {
    	centerContainer.getChildren().clear();

    	FxWindow<PhaseSubController> window = FxmlUtils.loadFxmlAndPassController(fxmlFile, this, (loader, masterCtlr) -> {
    		PhaseSubController controller = (PhaseSubController)loader.getController();
			controller.setMasterController(masterCtlr, this.game);
			currentPhaseSubController = controller;
    	});

    	if (window.root() != null) {
			centerContainer.getChildren().add(window.root());
    	}
    }

    // ------------------------- logic helpers
    
    private void loadAndExecuteCurrentPhase() {
    	game.executePhaseLogic();
    	loadCurrentPhase();
    }
    
    private int getDeltaForType(List<ResourceSnapshot> deltas, ResourceType type) {
    	for (var snap : deltas) {
    		if (snap.type() == type) {
    			return snap.quantity();
    		}
    	}

    	throw new IllegalStateException(String.format("ressource %s not found", type));
    }
    
    /** january is 1 */
    private String getMonthFromNumber(int month) {
    	return switch (month) {
    	case 1 -> "Janvier";
    	case 2 -> "Fevrier";
    	case 3 -> "Mars";
    	case 4 -> "Avril";
    	case 5 -> "Mai";
    	case 6 -> "Juin";
    	case 7 -> "Juillet";
    	case 8 -> "Aout";
    	case 9 -> "Septembre";
    	case 10 -> "Octobre";
    	case 11 -> "Novembre";
    	case 12 -> "Decembre";
		default -> throw new IllegalArgumentException("Unexpected value: " + month);
    	};
    }
    
    private void handleDefeat() {
		Logger.LogError("Village defeated. Returning to main menu.");
    }

    private void handleVictory() {
		Logger.LogDebug("Victory achieved! Popping back to start fxWindow.");
    }
}
