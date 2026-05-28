package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Objects;

import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;

/**
 * Interactive accordion widget representing a council member's active ability.
 * Upgraded with multi-line wrap text formatting for missing resources and explicit cost vs output details.
 */
public class AbilityAccordionWidget extends VBox {

    @FunctionalInterface
    public interface AbilityActivationListener {
        void onActivate(CharacterAbility targetAbility);
    }

    private final CharacterAbility ability;
    private final SmurfVillage village;
    
    private final Button activateButton;
    private final Label chevronLabel;
    
    private final VBox detailsContainer;
    private final VBox requirementsSection;
    private final VBox requirementsContainer;
    private final VBox effectsSection;
    private final VBox effectsListContainer;
    private final Label descriptionLabel;

    private AbilityActivationListener activationListener;

    public AbilityAccordionWidget(CharacterAbility ability, SmurfVillage village) {
        super();
        this.ability = Objects.requireNonNull(ability);
        this.village = Objects.requireNonNull(village);
        

        this.setSpacing(0);
        this.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: #3f444c; " +
            "-fx-border-width: 0 0 1 0;" 
        );

        // ==========================================
        // 1. Always Visible Header Row
        // ==========================================
        HBox outerRow = new HBox();
        outerRow.setPadding(new Insets(6, 12, 6, 12));
        outerRow.setStyle(
            "-fx-background-color: #2d3139; " +
            "-fx-background-radius: 6 6 0 0; " +
            "-fx-cursor: hand;"
        );
        
        outerRow.setAlignment(Pos.CENTER_LEFT);
        outerRow.setSpacing(6);

        this.chevronLabel = new Label("▼");
        this.chevronLabel.setTextFill(Color.web("#94a3b8"));
        this.chevronLabel.setFont(Font.font("System", FontWeight.BOLD, 10));

        Label nameLabel = new Label(ability.name());
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Label costBadge = new Label(ability.energyCost() + " ⚡");
        costBadge.setTextFill(Color.web("#3b82f6"));
        costBadge.setFont(Font.font("System", FontWeight.BOLD, 11));
        costBadge.setStyle(
            "-fx-background-color: #1a1c1e; " +
            "-fx-padding: 3 6 3 6; " +
            "-fx-background-radius: 4;"
        );
        
        Region regionSeparator = new Region();
        HBox.setHgrow(regionSeparator, Priority.ALWAYS);

        // MODIFIED: Increased width/height to properly contain wrapped text like "Ressources manquantes"
        this.activateButton = new Button("Activer");
        {
        	double width = 90;
        	double height = 34;
        	this.activateButton.setMinSize(width, height);
        	this.activateButton.setPrefSize(width, height);
        	this.activateButton.setMaxSize(width, height);
        }
        setActivationAllowed(true, "Activer");

        this.activateButton.setOnAction(_ -> {
            if (this.activationListener != null) {
                this.activationListener.onActivate(this.ability);
            }
        });

        outerRow.getChildren().addAll(this.chevronLabel, nameLabel, regionSeparator, costBadge, this.activateButton);

        // ==========================================
        // 2. Collapsible Details Dropdown Panel
        // ==========================================
        this.detailsContainer = new VBox();
        this.detailsContainer.setSpacing(12); // Slightly increased spacing between text blocks
        this.detailsContainer.setPadding(new Insets(10, 12, 10, 12)); 
        this.detailsContainer.setStyle(
            "-fx-background-color: #1a1c1e; " +
            "-fx-background-radius: 0 0 6 6;"
        );

        this.detailsContainer.setVisible(false);
        this.detailsContainer.managedProperty().bind(this.detailsContainer.visibleProperty());

        // Narrative Description Label
        this.descriptionLabel = new Label(ability.description());
        this.descriptionLabel.setTextFill(Color.web("#cbd5e1"));
        this.descriptionLabel.setFont(Font.font("System", 11));
        this.descriptionLabel.setWrapText(true);
        this.descriptionLabel.maxWidthProperty().bind(this.widthProperty().subtract(40));

        // Paragraph 1: Resources Required (Costs)
        this.requirementsSection = new VBox(6);
        Label reqHeader = new Label("Ressources requises :");
        reqHeader.setTextFill(Color.web("#fca5a5")); // Soft alert red for cost requirements
        reqHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
        this.requirementsContainer = new VBox(2);
        this.requirementsSection.getChildren().addAll(reqHeader, this.requirementsContainer);

        // Paragraph 2: Potential Effects Produced (Yields)
        this.effectsSection = new VBox(6);
        Label effHeader = new Label("Effets potentiels produits :");
        effHeader.setTextFill(Color.web("#34d399")); // Emerald green for produced yields
        effHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
        this.effectsListContainer = new VBox(2);
        this.effectsSection.getChildren().addAll(effHeader, this.effectsListContainer);

        this.detailsContainer.getChildren().addAll(this.descriptionLabel, this.requirementsSection, this.effectsSection);
        this.getChildren().addAll(outerRow, this.detailsContainer);

        // ==========================================
        // 3. Interactive Toggling Filter Mechanics
        // ==========================================
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (isClickTargetingNode(event, this.activateButton)) {
                return;
            }
            toggleAccordionState();
            event.consume();
        });

        // Auto-populate all sub-row displays inside the panels
        populateEffectsDisplay(ability);
    }

    /**
     * Rebuilds the dynamic costs and produced outputs lists.
     */
    public void populateEffectsDisplay(CharacterAbility ability) {
        Objects.requireNonNull(ability, "L'abilité ne peut pas être nulle.");
        
        // 1. Populate Required Resources (Flat quantities)
        this.requirementsContainer.getChildren().clear();
        List<ResourceSnapshot> requiredResources = ability.requiredResources();
        
        if (requiredResources == null || requiredResources.isEmpty()) {
            this.requirementsSection.setVisible(false);
            this.requirementsSection.setManaged(false);
        } else {
            this.requirementsSection.setVisible(true);
            this.requirementsSection.setManaged(true);
            for (ResourceSnapshot req : requiredResources) {
                // Instantiated with false flag to display flat uncolored quantities
                ResourceSummaryRow row = new ResourceSummaryRow(req.type(), false);
                row.updateDelta(req.quantity());
                this.requirementsContainer.getChildren().add(row);
            }
        }

        // 2. Populate Potential Yields (Delta-colored tracking changes)
        this.effectsListContainer.getChildren().clear();
        List<ResourceEffect> primaryEffects = ability.primaryEffects();
        
        if (primaryEffects == null || primaryEffects.isEmpty()) {
            this.effectsSection.setVisible(false);
            this.effectsSection.setManaged(false);
        } else {
            this.effectsSection.setVisible(true);
            this.effectsSection.setManaged(true);
            for (ResourceEffect effect : primaryEffects) {
                // Instantiated with default true flag to display red/green delta values
                ResourceSummaryRow row = new ResourceSummaryRow(effect.resourceType(), true);
                row.updateDelta(village.getEffectDeltaWithEfficiencyModifier(effect));
                this.effectsListContainer.getChildren().add(row);
            }
        }
    }

    public void setOnAbilityActivated(AbilityActivationListener listener) {
        this.activationListener = listener;
    }

    public void setActivationAllowed(boolean allowed, String lockReason) {
    	String ENABLE_FMT = "-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 4; "
    			+ "-fx-font-weight: bold; -fx-font-size: %dpx; -fx-cursor: hand;";
    	
    	String DISABLE_FMT = "-fx-background-color: #4b5563; -fx-text-fill: #b8c2d1; -fx-background-radius: 4; "
    			+ "-fx-font-weight: bold; -fx-font-size: %dpx; -fx-cursor: not-allowed; "
    	        + "-fx-text-alignment: center; -fx-wrap-text: true;";

    	int NORMAL_FONT_SIZE = 12;
    	int SMALL_FONT_SIZE = 9;

        if (allowed) {       
            this.activateButton.setDisable(false);
            this.activateButton.setText("Activer");
            this.activateButton.setStyle(String.format(ENABLE_FMT, NORMAL_FONT_SIZE));
        } else {
            this.activateButton.setDisable(true);

            // diminuer taille si message long
            if (lockReason.length() >= 15) {
            	int lastSpace = lockReason.lastIndexOf(' ');
            	if (lastSpace != -1) {
            		lockReason = String.format("%s\n%s", lockReason.substring(0, lastSpace), lockReason.substring(lastSpace + 1));
            	}
            	this.activateButton.setStyle(String.format(DISABLE_FMT, SMALL_FONT_SIZE));
            }
            else {
            	this.activateButton.setStyle(String.format(DISABLE_FMT, NORMAL_FONT_SIZE));
            }
            
            this.activateButton.setText(lockReason);
        }
    }

    public CharacterAbility getAbility() {
        return this.ability;
    }

    private void toggleAccordionState() {
        boolean nextState = !this.detailsContainer.isVisible();
        this.detailsContainer.setVisible(nextState);
        this.chevronLabel.setText(nextState ? "▲" : "▼");
    }

    private boolean isClickTargetingNode(MouseEvent event, Node node) {
        Node target = (Node) event.getTarget();
        while (target != null) {
            if (target == node) {
                return true;
            }
            target = target.getParent();
        }
        return false;
    }
}
