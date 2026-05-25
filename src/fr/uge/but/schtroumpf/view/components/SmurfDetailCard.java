package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Représente l'en-tête de fiche d'identité d'un Schtroumpf sous la forme d'une carte
 * profilée élégante intégrée au panneau de détails à droite.
 */
public class SmurfDetailCard extends VBox {

    private final ImageView portraitView;
    private final Label nameLabel;
    private final Label roleLabel;
    private final Label energyLabel;

    /**
     * Initialise la carte de détails vide, prête à recevoir ses données dynamiquement.
     */
    public SmurfDetailCard() {
        super();

        // Configuration de la carte principale
        this.setPadding(new Insets(12));
        this.setSpacing(10);
        this.setStyle(
            "-fx-background-color: #202225; " +
            "-fx-background-radius: 8; "
        );

        // HBox unifiant le portrait à gauche et la zone textuelle à droite
        HBox profileLayout = new HBox();
        profileLayout.setAlignment(Pos.CENTER_LEFT);
        profileLayout.setSpacing(15);

        // Cadre stylisé pour accueillir le portrait 64x64
        StackPane portraitFrame = new StackPane();
        portraitFrame.setPrefSize(68, 68);
        portraitFrame.setMinSize(68, 68);
        portraitFrame.setMaxSize(68, 68);
        portraitFrame.setStyle(
            "-fx-background-color: #1a1c1e; " +
            "-fx-background-radius: 6; " +
            "-fx-border-color: #4b5563; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6;"
        );

        this.portraitView = new ImageView();
        this.portraitView.setFitWidth(64);
        this.portraitView.setFitHeight(64);
        this.portraitView.setPreserveRatio(true);
        this.portraitView.setSmooth(true);
        portraitFrame.getChildren().add(this.portraitView);

        // VBox regroupant l'état civil et l'énergie du conseiller
        VBox textLayout = new VBox();
        textLayout.setAlignment(Pos.CENTER_LEFT);
        textLayout.setSpacing(3);
        HBox.setHgrow(textLayout, Priority.ALWAYS);

        // Label du Nom principal
        this.nameLabel = new Label("Aucun membre sélectionné");
        this.nameLabel.setTextFill(Color.WHITE);
        this.nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Label du Rôle en italique (Ergonomie Dorian)
        this.roleLabel = new Label("Sélectionnez un membre pour assigner ses actions");
        this.roleLabel.setTextFill(Color.web("#94a3b8"));
        this.roleLabel.setFont(Font.font("System", FontPosture.ITALIC, 11));

        // Label de l'Énergie globale
        this.energyLabel = new Label("Énergie : -- / -- ⚡");
        this.energyLabel.setTextFill(Color.web("#3b82f6"));
        this.energyLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        textLayout.getChildren().addAll(this.nameLabel, this.roleLabel, this.energyLabel);

        // Ajout des structures internes à la disposition principale
        profileLayout.getChildren().addAll(portraitFrame, textLayout);
        this.getChildren().add(profileLayout);
    }

    public void updateEnergy(int newEnergy, int maxEnergy) {
        this.energyLabel.setText("Énergie : " + newEnergy + " / " + maxEnergy + " ⚡");
    }

    /**
     * Réécrit dynamiquement les valeurs d'identité de la carte lors d'un changement de contexte.
     * * @param name Nom du Schtroumpf.
     * @param description Description de son rôle ou spécialité.
     * @param currentEnergy Énergie résiduelle.
     * @param maxEnergy Jauge énergétique maximale autorisée.
     */
    public void updateData(String name, String description, int currentEnergy, int maxEnergy) {
        this.nameLabel.setText(Objects.requireNonNull(name, "Le nom ne peut pas être nul."));
        this.roleLabel.setText(Objects.requireNonNull(description, "La description ne peut pas être nulle."));
		updateEnergy(currentEnergy, maxEnergy);
        
        // Alerte visuelle de l'indicateur textuel d'énergie en cas d'épuisement total
        if (currentEnergy <= 0) {
            this.energyLabel.setTextFill(Color.web("#ef4444"));
        } else {
            this.energyLabel.setTextFill(Color.web("#3b82f6"));
        }
    }

    /**
     * Modifie le portrait de la fiche à partir d'une image chargée.
     * * @param portrait Image du portrait.
     */
    public void setPortrait(Path portraitPath) {
    	if (portraitPath != null) {
			this.portraitView.setImage(new Image(portraitPath.toUri().toString()));
    	}
    }
}
