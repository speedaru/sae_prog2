package fr.uge.but.schtroumpf.controller.gui.phases;

import module java.base;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.model.characters.SmurfType;
import fr.uge.but.schtroumpf.view.components.AbilityAccordionWidget;
import fr.uge.but.schtroumpf.view.components.CoolScrollPane;
import fr.uge.but.schtroumpf.view.components.SmurfDetailCard;
import fr.uge.but.schtroumpf.view.components.SmurfListRow;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;

public class CouncilPhaseController implements PhaseSubController {
	private GameController masterController;
	private Game game;

    @FXML private VBox detailAbilitiesContainer, detailPanelContent, emptyPlaceholderCard, smurfsListContainer;
    @FXML private HBox detailCardContainer;
    @FXML private Button finishButton;
    @FXML private Label statusFeedbackLabel, actionsCounterLabel;
    @FXML private ScrollPane councilScrollPane, abilityScrollPane;
    
    // ui stuff
    private final SmurfDetailCard detailHeaderCard = new SmurfDetailCard();
    private final List<SmurfListRow> renderedRows = new ArrayList<>();
    private SmurfListRow currentlySelectedRow = null;
    private HashMap<String, AbilityAccordionWidget> abilityRows = new HashMap<>(); // ability name / ability widget
    
    // ability result feedback
    private int abilityResultSameMsgCounter = 0;
    private String abilityResultLastMsg;

	@Override
	public void setMasterController(GameController masterController, Game game) {
		this.masterController = masterController;
		this.game = game;
		
        detailCardContainer.getChildren().clear();
        detailCardContainer.getChildren().add(detailHeaderCard);
		
        // load council members list
        initUI();
		updateRemainingAbilitiesCounter();
	}

    @FXML
    void handleFinish(ActionEvent event) {
    	masterController.advanceTurn();
    }
    
    private void initUI() {
		loadCouncilMembers();
		CoolScrollPane.setScrollBarStyle(councilScrollPane);
		CoolScrollPane.setScrollBarStyle(abilityScrollPane);
    }

    private void loadCouncilMembers() {
        smurfsListContainer.getChildren().clear();
        renderedRows.clear();
        
        emptyPlaceholderCard.setVisible(true);
        detailPanelContent.setVisible(false);

        List<SmurfCharacter> councilMembers = game.getVillage().getAvailableSmurfs();
        Logger.LogDebug("council members: %d", councilMembers.size());

        SmurfVillage village = game.getVillage();
        for (SmurfCharacter member : councilMembers) {
            SmurfListRow rowWidget = new SmurfListRow(village, member);
            
            // register on click event to render selected smurf 
            rowWidget.setOnMouseClicked(_ -> selectCouncilMember(rowWidget));

            renderedRows.add(rowWidget);
            smurfsListContainer.getChildren().add(rowWidget);
        }
    }

    /** handle new smurf selected */
    private void selectCouncilMember(SmurfListRow selectedRow) {
        if (currentlySelectedRow != null) {
            currentlySelectedRow.setSelectedState(false);
        }

        currentlySelectedRow = selectedRow;
        currentlySelectedRow.setSelectedState(true);

        emptyPlaceholderCard.setVisible(false);
        detailPanelContent.setVisible(true);

        SmurfCharacter smurf = selectedRow.getSmurf();
        SmurfType smurfType = smurf.getType();
        int finalMaxEnergy = game.getVillage().getDynamicMaxEnergy(smurf);
        detailHeaderCard.updateData(smurfType.getName(), smurfType.getRoleDescription(), smurf.getEnergy(), finalMaxEnergy);
        detailHeaderCard.setPortrait(smurf.getType().getSpritePath());

        // load abilities
        loadAbilitiesForSelectedMember(smurf);
    }

    private void loadAbilitiesForSelectedMember(SmurfCharacter smurf) {
    	SmurfVillage village = game.getVillage();
        List<CharacterAbility> characterAbilities = village.getCouncilMember(smurf.getType()).getAbilities();

        // clear container and rows list
        detailAbilitiesContainer.getChildren().clear();
        abilityRows.clear();

        // create ability widgets
        for (CharacterAbility ability : characterAbilities) {
            AbilityAccordionWidget abilityWidget = new AbilityAccordionWidget(ability, village);

            abilityWidget.populateEffectsDisplay(ability);
            setAbilityButtonConstraints(smurf, ability, abilityWidget);

            // on click
            abilityWidget.setOnAbilityActivated(_ -> {
            	executeAbility(smurf, ability);
            	updateAbilityRows(smurf); // disable some rows if not enough energy now
            	updateRemainingAbilitiesCounter();
            });

            detailAbilitiesContainer.getChildren().add(abilityWidget);
            abilityRows.put(ability.name(), abilityWidget);
        }
    }
    
    /** rechecks if we can still click activate button */
    private void updateAbilityRows(SmurfCharacter smurf) {
    	SmurfVillage village = game.getVillage();
        List<CharacterAbility> characterAbilities = village.getCouncilMember(smurf.getType()).getAbilities();

        // re-evaluate ability based on new energy
        for (CharacterAbility ability : characterAbilities) {
            AbilityAccordionWidget abilityWidget = abilityRows.getOrDefault(ability.name(), null);
            if (abilityWidget == null) continue;
            
            // only update button state
            setAbilityButtonConstraints(smurf, ability, abilityWidget);
        }
    }
    
    private void updateRemainingAbilitiesCounter() {
    	SmurfVillage village = game.getVillage();
    	int max = village.getDynamicMaxAbilitiesPerTurn();
    	int used = village.getAbilitiesUsedThisTurn();
    	int remaining = max - used;
    	
    	actionsCounterLabel.setText(String.format("%d/%d", remaining, max));
    }

    private void setAbilityButtonConstraints(SmurfCharacter smurf, CharacterAbility ability, AbilityAccordionWidget widget) {
    	SmurfVillage village = game.getVillage();
    	
    	if (village.isActionLimitReached()) {
    		widget.setActivationAllowed(false, String.format("Limite de %d actions", village.getDynamicMaxAbilitiesPerTurn()));
    	} else if (!smurf.hasEnoughEnergy(ability)) {
    		widget.setActivationAllowed(false, "Énergie Insuffisante");
    	} else if (!smurf.hasRequiredResources(village, ability)) {
    		widget.setActivationAllowed(false, "Ressources Manquantes");
    	} else {
    		widget.setActivationAllowed(true, "Activer");
    	}
    }
    
    /** executes ability logic via village and updates UI stuff */
    private void executeAbility(SmurfCharacter smurf, CharacterAbility ability) {
    	SmurfVillage village = game.getVillage();
    	if (!smurf.canExecute(village, ability)) {
    		return;
    	}
    	
    	// actual logic
    	AbilityResult result = village.executeCouncilMemberAbility(smurf, ability);
    	
    	// update UI
    	
		// update ability status indicator
		if (result.message().equals(abilityResultLastMsg)) {
			abilityResultSameMsgCounter++;
			statusFeedbackLabel.setText(String.format("%s (x%d)", result.message(), abilityResultSameMsgCounter + 1));
		} else {
			statusFeedbackLabel.setText(result.message());
			abilityResultLastMsg = result.message();
			abilityResultSameMsgCounter = 0;
		}
		statusFeedbackLabel.setTextFill(ThemeManager.getAbilityResultTypeColor(result.type()));

        masterController.updateHudResources();

        int newEnergyAmount = village.getCouncilMember(smurf.getType()).getEnergy();

        // update energy indicators
        int maxEnergy = village.getDynamicMaxEnergy(smurf);
        currentlySelectedRow.updateEnergy(newEnergyAmount, maxEnergy);
        Logger.LogDebug("updated energy to %d", newEnergyAmount);
        detailHeaderCard.updateEnergy(newEnergyAmount, maxEnergy);
    }
}