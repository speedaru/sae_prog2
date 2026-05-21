package fr.uge.but.schtroumpf.controller.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import fr.uge.but.schtroumpf.controller.FxmlSubController;
import fr.uge.but.schtroumpf.controller.Navigation.*;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.Effect;
import fr.uge.but.schtroumpf.model.crises.*;
import fr.uge.but.schtroumpf.model.events.EventHistory;
import fr.uge.but.schtroumpf.model.phases.*;
import fr.uge.but.schtroumpf.view.Logger;
import fr.uge.but.schtroumpf.view.windows.console.GameWindow;
import fr.uge.but.schtroumpf.view.components.ResourceWidget;

public class GameController implements FxmlSubController {
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
        
        initResourceWidgets();
        updateResourceHud();

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
        updateResourceHud();
    }

    @FXML void handleQuitButton1(ActionEvent event) {
        Logger.LogDebug("Click quit button, going back to start window");
        router.navigate(NavigationAction.POP, null);
    }

    // unused handlers
    @FXML void handleOpenEncyclopedia(ActionEvent event) { }
    @FXML void handleToggleUI(ActionEvent event) { }
    @FXML void handleOpenSettings(ActionEvent event) { }
    
    public void updateResourceHud() {
        SmurfVillage village = game.getVillage();
        List<ResourceSnapshot> snapshots = village.getResources();
        List<ResourceSnapshot> deltas = village.getResourcesDiff();
        
        for (var snap : snapshots) {
        	ResourceType type = snap.resource();
            int delta = getDeltaForType(deltas, type);
            
            ResourceWidget widget = resourceWidgets.get(type);
            if (widget != null) {
                widget.updateState(snap.quantity(), delta);
            }
        }
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
    
    private int getDeltaForType(List<ResourceSnapshot> deltas, ResourceType type) {
    	for (var snap : deltas) {
    		if (snap.resource() == type) {
    			return snap.quantity();
    		}
    	}

    	throw new IllegalStateException(String.format("ressource %s not found", type));
    }
}
