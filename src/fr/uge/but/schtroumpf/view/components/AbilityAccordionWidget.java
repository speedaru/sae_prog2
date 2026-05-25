package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Objects;

import fr.uge.but.schtroumpf.model.characters.CharacterAbility;
import fr.uge.but.schtroumpf.model.characters.ResourceEffect;

/**
 * Interactive accordion widget representing a council member's active ability.
 * Optimized with dynamic text boundary wrappers to prevent window expansion.
 */
public class AbilityAccordionWidget extends VBox {

    @FunctionalInterface
    public interface AbilityActivationListener {
        void onActivate(CharacterAbility targetAbility);
    }

    private final CharacterAbility ability;
    private final Button activateButton;
    private final Label chevronLabel;
    private final VBox detailsContainer;
    private final VBox effectsListContainer;
    private final Label descriptionLabel;

    private AbilityActivationListener activationListener;

    public AbilityAccordionWidget(CharacterAbility ability) {
        super();
        this.ability = Objects.requireNonNull(ability, "La compétence ne peut pas être nulle.");

        this.setSpacing(0);
        this.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: #3f444c; " +
            "-fx-border-width: 0 0 1 0;" 
        );

        // ==========================================
        // 1. Always Visible Header Row
        // ==========================================
        BorderPane outerRow = new BorderPane();
        outerRow.setPadding(new Insets(10, 12, 10, 12));
        outerRow.setStyle(
            "-fx-background-color: #2d3139; " +
            "-fx-background-radius: 6 6 0 0; " +
            "-fx-cursor: hand;"
        );

        HBox outerRowLeft = createOuterRow();
        HBox outerRowRight = createOuterRow();

        this.chevronLabel = new Label("▼");
        this.chevronLabel.setTextFill(Color.web("#94a3b8"));
        this.chevronLabel.setFont(Font.font("System", FontWeight.BOLD, 10));

        Label nameLabel = new Label(ability.name());
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        outerRowLeft.getChildren().addAll(this.chevronLabel, nameLabel);
        outerRow.setLeft(outerRowLeft);

        Label costBadge = new Label(ability.energyCost() + " ⚡");
        costBadge.setTextFill(Color.web("#3b82f6"));
        costBadge.setFont(Font.font("System", FontWeight.BOLD, 11));
        costBadge.setStyle(
            "-fx-background-color: #1a1c1e; " +
            "-fx-padding: 3 6 3 6; " +
            "-fx-background-radius: 4;"
        );

        this.activateButton = new Button("Activer");
        this.activateButton.setMinSize(75, 26);
        this.activateButton.setPrefSize(75, 26);
        this.activateButton.setMaxSize(75, 26);
        setActivationAllowed(true, "Activer");

        this.activateButton.setOnAction(_ -> {
            if (this.activationListener != null) {
                this.activationListener.onActivate(this.ability);
            }
        });

        outerRowRight.getChildren().addAll(costBadge, this.activateButton);
        outerRow.setRight(outerRowRight);

        // ==========================================
        // 2. Collapsible Details Dropdown Panel
        // ==========================================
        this.detailsContainer = new VBox();
        this.detailsContainer.setSpacing(4);
        this.detailsContainer.setPadding(new Insets(5, 6, 5, 12)); 
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
        
        // CRITICAL FIX: Forces text wrapping vertically based on parent width bounds
        this.descriptionLabel.maxWidthProperty().bind(this.widthProperty().subtract(40));

        this.effectsListContainer = new VBox();
        this.effectsListContainer.setSpacing(0);

        this.detailsContainer.getChildren().addAll(this.descriptionLabel, this.effectsListContainer);
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

        // Auto-populate the resource summary changes lists right on instantiation
        populateEffectsDisplay(ability);
    }

    /**
     * Rebuilds the internal resource modification list view.
     */
    public void populateEffectsDisplay(CharacterAbility ability) {
        Objects.requireNonNull(ability, "L'abilité ne peut pas être nulle.");
        this.effectsListContainer.getChildren().clear();

        List<ResourceEffect> primaryEffects = ability.primaryEffects();
        for (ResourceEffect effect : primaryEffects) {
            ResourceSummaryRow row = new ResourceSummaryRow(effect.resourceType());
            row.updateDelta(effect.delta());
            this.effectsListContainer.getChildren().add(row);
        }
    }

    public void setOnAbilityActivated(AbilityActivationListener listener) {
        this.activationListener = listener;
    }

    public void setActivationAllowed(boolean allowed, String lockReason) {
        if (allowed) {
            this.activateButton.setDisable(false);
            this.activateButton.setText("Activer");
            this.activateButton.setStyle(
                "-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 4; " +
                "-fx-font-weight: bold; -fx-font-size: 10px; -fx-cursor: hand;"
            );
        } else {
            this.activateButton.setDisable(true);
            this.activateButton.setText(lockReason != null ? lockReason : "Verrouillé");
            this.activateButton.setStyle(
                "-fx-background-color: #4b5563; -fx-text-fill: #94a3b8; -fx-background-radius: 4; " +
                "-fx-font-weight: bold; -fx-font-size: 9px; -fx-cursor: not-allowed;"
            );
        }
    }

    public CharacterAbility getAbility() {
        return this.ability;
    }

    private HBox createOuterRow() {
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        return hbox;
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
