package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Objects;

import fr.uge.but.schtroumpf.model.characters.CharacterAbility;

/**
 * Widget d'accordéon réutilisable représentant une compétence ou capacité active.
 * Il gère son propre état d'expansion fluide pour dévoiler ses prérequis d'activation
 * sans nécessiter de modale ou de modification de la taille globale de l'interface.
 */
public class AbilityAccordionWidget extends VBox {

    /**
     * callback interface passed by controller
     */
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

    /**
     * Crée une carte d'accordéon interactive pour une compétence donnée.
     * * @param ability Le modèle de la compétence (non nul).
     */
    public AbilityAccordionWidget(CharacterAbility ability) {
        super();
        this.ability = Objects.requireNonNull(ability, "La compétence ne peut pas être nulle.");

        this.setSpacing(0);
        this.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: #3f444c; " +
            "-fx-border-width: 0 0 1 0;" // Ligne de séparation basse
        );

        // ==========================================
        // 1. Ligne Principale (Always Visible Outer Row)
        // ==========================================
        HBox outerRow = new HBox();
        outerRow.setAlignment(Pos.CENTER_LEFT);
        outerRow.setPadding(new Insets(10, 12, 10, 12));
        outerRow.setSpacing(10);
        outerRow.setStyle(
            "-fx-background-color: #2d3139; " +
            "-fx-background-radius: 6 6 0 0; " +
            "-fx-cursor: hand;"
        );

        // Nom de l'action
        Label nameLabel = new Label(ability.name());
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // Badge de coût en énergie (Lisibilité Thierry)
        Label costBadge = new Label(ability.energyCost() + " ⚡");
        costBadge.setTextFill(Color.web("#3b82f6"));
        costBadge.setFont(Font.font("System", FontWeight.BOLD, 11));
        costBadge.setStyle(
            "-fx-background-color: #1a1c1e; " +
            "-fx-padding: 3 6 3 6; " +
            "-fx-background-radius: 4;"
        );

        // Flèche de déploiement (Chevron)
        this.chevronLabel = new Label("▼");
        this.chevronLabel.setTextFill(Color.web("#94a3b8"));
        this.chevronLabel.setFont(Font.font("System", FontWeight.BOLD, 10));

        // Bouton d'action Vert "Activer"
        this.activateButton = new Button("Activer");
        this.activateButton.setMinSize(75, 26);
        this.activateButton.setPrefSize(75, 26);
        this.activateButton.setMaxSize(75, 26);
        this.activateButton.setStyle(
            "-fx-background-color: #10b981; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 4; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 10px; " +
            "-fx-cursor: hand;"
        );

        // Liaison du clic d'activation
        this.activateButton.setOnAction(_ -> {
            if (this.activationListener != null) {
                this.activationListener.onActivate(this.ability);
            }
        });

        outerRow.getChildren().addAll(this.chevronLabel, nameLabel, costBadge, this.activateButton);

        // ==========================================
        // 2. Panneau de Détails Caché (Collapsed Sub-Panel)
        // ==========================================
        this.detailsContainer = new VBox();
        this.detailsContainer.setSpacing(8);
        this.detailsContainer.setPadding(new Insets(10, 12, 10, 24)); // Renfoncement léger
        this.detailsContainer.setStyle(
            "-fx-background-color: #1a1c1e; " +
            "-fx-background-radius: 0 0 6 6;"
        );

        // Rendre le composant invisible et non-géré par le Layout d'origine
        this.detailsContainer.setVisible(false);
        this.detailsContainer.managedProperty().bind(this.detailsContainer.visibleProperty());

        // Descriptif narratif
        this.descriptionLabel = new Label(ability.description());
        this.descriptionLabel.setTextFill(Color.web("#cbd5e1"));
        this.descriptionLabel.setFont(Font.font("System", 11));
        this.descriptionLabel.setWrapText(true);

        // Conteneur interne accueillant les ResourceSummaryRow de manière dynamique
        this.effectsListContainer = new VBox();
        this.effectsListContainer.setSpacing(4);

        this.detailsContainer.getChildren().addAll(this.descriptionLabel, this.effectsListContainer);

        // Assemblage global
        this.getChildren().addAll(outerRow, this.detailsContainer);

        // ==========================================
        // 3. Mécanisme d'ouverture Accordéon
        // ==========================================
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            // Empêche le déploiement de l'accordéon si l'utilisateur clique sur le bouton "Activer"
            if (isClickTargetingNode(event, this.activateButton)) {
                return;
            }
            toggleAccordionState();
            event.consume();
        });
    }

    /**
     * Remplit et génère dynamiquement la liste d'impact de ressources
     * en instanciant le composant partagé ResourceSummaryRow.
     * * @param effects list of ability descriptions
     */
    public void setAbilityDescription(String description) {
    	Objects.requireNonNull(description, "description can't be null");
        this.effectsListContainer.getChildren().clear();

		try {
			// Instanciation directe de la rangée de ressource partagée par votre projet
			// Note: Si votre ResourceSummaryRow prend un type 'ResourceType' énuméré dans son constructeur, 
			// effectuez simplement la conversion de type ici (ex: ResourceType.valueOf(effect.getResourceType())).
			
			// Exemple générique assumant un constructeur (String) ou équivalent :
			// ResourceSummaryRow row = new ResourceSummaryRow(effect.getResourceType());
			// row.updateDelta(effect.getDelta());
			// this.effectsListContainer.getChildren().add(row);
			
			// Fallback graphique local sécurisé au cas où la classe externe présenterait une autre signature :
			HBox fallbackRow = new HBox();
			fallbackRow.setAlignment(Pos.CENTER_LEFT);
			fallbackRow.setSpacing(8);
			
			Label valLabel = new Label("+-");
			valLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
//			valLabel.setTextFill(effect.getDelta() ? Color.web("#10b981") : Color.web("#ef4444"));

			Label resLabel = new Label(description);
			resLabel.setTextFill(Color.web("#f1f5f9"));
			resLabel.setFont(Font.font("System", 11));

			fallbackRow.getChildren().addAll(valLabel, resLabel);
			this.effectsListContainer.getChildren().add(fallbackRow);

		} catch (Exception ex) {
			System.err.println("[AbilityAccordionWidget] Échec de l'injection d'effet: " + ex.getMessage());
		}
	}

    /**
     * Enregistre un écouteur d'événement d'activation auprès du widget.
     */
    public void setOnAbilityActivated(AbilityActivationListener listener) {
        this.activationListener = listener;
    }

    /**
     * Permet d'activer ou de désactiver le bouton de commande selon l'évaluation
     * des ressources et prérequis calculés par le moteur du contrôleur.
     */
    public void setActivationAllowed(boolean allowed, String lockReason) {
        if (allowed) {
            this.activateButton.setDisable(false);
            this.activateButton.setText("Activer");
            this.activateButton.setStyle(
                "-fx-background-color: #10b981; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 4; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 10px; " +
                "-fx-cursor: hand;"
            );
        } else {
            this.activateButton.setDisable(true);
            this.activateButton.setText(lockReason != null ? lockReason : "Verrouillé");
            this.activateButton.setStyle(
                "-fx-background-color: #4b5563; " +
                "-fx-text-fill: #94a3b8; " +
                "-fx-background-radius: 4; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 9px; " +
                "-fx-cursor: not-allowed;"
            );
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
