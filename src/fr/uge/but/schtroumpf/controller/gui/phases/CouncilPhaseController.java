package fr.uge.but.schtroumpf.controller.gui.phases;

import module java.base;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import fr.uge.but.schtroumpf.controller.PhaseSubController;
import fr.uge.but.schtroumpf.controller.gui.windows.GameController;
import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.view.components.AbilityAccordionWidget;
import fr.uge.but.schtroumpf.view.components.SmurfDetailCard;
import fr.uge.but.schtroumpf.view.components.SmurfListRow;

public class CouncilPhaseController implements PhaseSubController {
	private GameController masterController;
	private Game game;

    @FXML private VBox detailAbilitiesContainer, detailPanelContent, emptyPlaceholderCard, smurfsListContainer;
    @FXML private HBox detailCardContainer;
    @FXML private Button finishButton;
    
	 // View State Layers
    private final SmurfDetailCard detailHeaderCard = new SmurfDetailCard();
    private final List<SmurfListRow> renderedRows = new ArrayList<>();
    private SmurfListRow currentlySelectedRow = null;

	@Override
	public void setMasterController(GameController masterController, Game game) {
		this.masterController = masterController;
		this.game = game;
		
		// 1. Programmatically inject our beautiful header profile widget into the FXML HBox card holder
        detailCardContainer.getChildren().clear();
        detailCardContainer.getChildren().add(detailHeaderCard);
		
		 // 2. Initialize and load council members list layout
		loadCouncilMembers();
	}

    @FXML
    void handleFinish(ActionEvent event) {
    	masterController.advanceTurn();
    }

	/**
     * Queries the village model to render the left-side master list rows.
     */
    private void loadCouncilMembers() {
        smurfsListContainer.getChildren().clear();
        renderedRows.clear();
        
        // Ensure our views are in the correct initial default state (Dorian-friendly stability)
        emptyPlaceholderCard.setVisible(true);
        detailPanelContent.setVisible(false);

        // Fetch your array list of council characters from your backing model layer
        List<SmurfCharacter> councilMembers = game.getVillage().getAvailableSmurfs();

        for (SmurfCharacter member : councilMembers) {
            SmurfListRow rowWidget = new SmurfListRow(member);
            
            // Set up click actions to register selection changes cleanly
            rowWidget.setOnMouseClicked(_ -> selectCouncilMember(rowWidget));

            renderedRows.add(rowWidget);
            smurfsListContainer.getChildren().add(rowWidget);
        }
    }

    /**
     * Handles selection context mutations. Swaps active selections and targets cards.
     */
    private void selectCouncilMember(SmurfListRow selectedRow) {
        // 1. Clear highlight out of former active row node components
        if (currentlySelectedRow != null) {
            currentlySelectedRow.setSelectedState(false);
        }

        // 2. Assign and activate new selection visual state properties
        currentlySelectedRow = selectedRow;
        currentlySelectedRow.setSelectedState(true);

        // 3. Structural swap: Hide placeholder card, display work panel context track
        emptyPlaceholderCard.setVisible(false);
        detailPanelContent.setVisible(true);

        // 4. Update the detail card headers with the Smurf's active properties
        var smurf = selectedRow.getSmurf();
        detailHeaderCard.updateData(smurf.getName(), "smurf role", smurf.getEnergy(), smurf.getMaxEnergy());
        detailHeaderCard.setPortrait(smurf.getSpritePath());

        // 5. Populate actionable ability loops
        loadAbilitiesForSelectedMember(smurf);
    }

    /**
     * Generates accordion panels dynamically based on character configuration metrics.
     */
    private void loadAbilitiesForSelectedMember(SmurfCharacter smurf) {
        detailAbilitiesContainer.getChildren().clear();

        // Fetch abilities tied to this character type from your engine logic rules
        List<CharacterAbility> characterAbilities = game.getVillage().getAbilitiesFor(smurf.getType());

        for (CharacterAbility ability : characterAbilities) {
            AbilityAccordionWidget abilityWidget = new AbilityAccordionWidget(ability);

            // Fetch dynamic resource impacts list (internally reuses your ResourceSummaryRow!)
            abilityWidget.setAbilityDescription(ability.description());

            // --- Preconditions Check (Strict rule enforcement) ---
            boolean hasRequiredResources = game.getVillage().verifyResources(ability.requiredResources());

            if (!smurf.canExecute(ability)) {
                abilityWidget.setActivationAllowed(false, "Énergie Insuffisante");
            } else if (!hasRequiredResources) {
                abilityWidget.setActivationAllowed(false, "Ressources Manquantes");
            } else {
                abilityWidget.setActivationAllowed(true, "Activer");
            }

            // Bind the click action confirmation callback hook
            abilityWidget.setOnAbilityActivated(_ -> executeAbility(smurf, ability));

            detailAbilitiesContainer.getChildren().add(abilityWidget);
        }
    }

    /**
     * Triggers model state changes and re-synchronizes all view counters instantly.
     */
    private void executeAbility(SmurfCharacter smurf, CharacterAbility ability) {
    	SmurfVillage village = game.getVillage();

    	village.executeCouncilMemberAbility(smurf.getType(), ability);

        // 2. RE-SYNC GLOBAL SIDEBAR HUD INDICATORS ON THE SPOT (Bars and deltas re-render)
        masterController.updateHudResources();

        // 3. Read back fresh updated variables directly out of memory
        int newEnergyAmount = village.getCouncilMember(smurf.getType()).getEnergy();

        // 4. Update row values text layout safely
        currentlySelectedRow.updateEnergy(newEnergyAmount, smurf.getMaxEnergy());

        // 5. Update right profile card metrics
        detailHeaderCard.updateData(smurf.getName(), "smurf role", newEnergyAmount, smurf.getMaxEnergy());

        // 6. Loop refresh: Immediately re-evaluate lock conditions on all active buttons
        loadAbilitiesForSelectedMember(smurf);
    }
}