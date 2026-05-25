package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.nio.file.Path;
import java.util.Objects;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.model.characters.SmurfType;

/**
 * Représente une ligne interactive et stylisée pour l'affichage d'un membre du conseil
 * dans le panneau "Master List" à gauche de la vue.
 * * Conçu spécifiquement pour être hautement lisible, notamment pour Thierry (accessibilité daltonisme)
 * en évitant toute dépendance exclusive à des indicateurs de couleur abstraits.
 */
public class SmurfListRow extends BorderPane {
    private final SmurfCharacter smurf;
    private final ImageView avatarView;
    private final Label nameLabel;
    private final Label energyLabel;
    private boolean isExhausted = false;
    
    private final Color labelColor = Color.WHITE;
    private final Color exhaustedLabelColor = Color.web("#ef4444");

    /**
     * Construit une ligne réactive pour un Schtroumpf donné.
     * * @param smurf Le modèle de données du Schtroumpf (non nul).
     */
    public SmurfListRow(SmurfVillage village, SmurfCharacter smurf) {
        super();
        this.smurf = Objects.requireNonNull(smurf, "Le modèle de données du Schtroumpf ne peut pas être nul.");

        // Style de base - Thème sombre premium
        this.setStyle(
            "-fx-background-color: #202225; " +
            "-fx-background-radius: 6; " +
            "-fx-border-color: #3f444c; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand;"
        );
        
        HBox left = createHBox();
        HBox right = createHBox();

        // Avatar de prévisualisation (24x24)
        this.avatarView = new ImageView();
        this.avatarView.setFitWidth(24);
        this.avatarView.setFitHeight(24);
        this.avatarView.setPreserveRatio(true);
        this.avatarView.setSmooth(true);
        SmurfType type = smurf.getType();
        loadAvatar(type.getSpritePath());

        // Nom du personnage (Prend tout l'espace restant pour l'alignement)
        this.nameLabel = new Label(smurf.getType().getName());
        this.nameLabel.setTextFill(labelColor);
        this.nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        HBox.setHgrow(this.nameLabel, Priority.ALWAYS);

        left.getChildren().addAll(this.avatarView, this.nameLabel);
        this.setLeft(left);

        // Indicateur d'énergie explicite en texte brut (Thierry-friendly)
        this.energyLabel = new Label();
        this.energyLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        this.energyLabel.setTextFill(labelColor);
        updateEnergyDisplay(smurf.getEnergy(), village.getDynamicMaxEnergy(smurf));

        right.getChildren().add(this.energyLabel);
        this.setRight(right);

        // Vérification automatique de l'état d'épuisement initial
		setExhaustedState(smurf.getEnergy());
    }

    /**
     * Permet d'injecter une nouvelle valeur d'énergie et de recalculer l'affichage textuel associé.
     */
    public void updateEnergy(int currentEnergy, int maxEnergy) {
        updateEnergyDisplay(currentEnergy, maxEnergy);
        setExhaustedState(currentEnergy);
    }

    /**
     * Alterne l'état de sélection visuelle de cette ligne (utilisé lors du clic).
     * * @param selected Vrai pour appliquer le style de surbrillance bleu.
     */
    public void setSelectedState(boolean selected) {
        if (selected) {
            this.setStyle(
                "-fx-background-color: #3b82f6; " +
                "-fx-background-radius: 6; " +
                "-fx-border-color: #60a5fa; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6; " +
                "-fx-cursor: hand;"
            );
        } else {
            // Restaure le thème sombre par défaut tout en respectant l'état d'épuisement
            this.setStyle(
                "-fx-background-color: #202225; " +
                "-fx-background-radius: 6; " +
                "-fx-border-color: #3f444c; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6; " +
                "-fx-cursor: hand;"
            );
        }
    }

    /**
     * Applique une opacité réduite pour indiquer visuellement un état épuisé (0 énergie)
     * tout en maintenant la ligne pleinement sélectionnable pour consulter la fiche.
     */
    public void setExhaustedState(int energy) {
        this.isExhausted = energy <= 0;
        if (this.isExhausted) {
            this.setOpacity(0.45);
            this.energyLabel.setTextFill(exhaustedLabelColor); // Rouge vif de contraste
        } else {
            this.setOpacity(1.0);
            this.energyLabel.setTextFill(labelColor); // Couleur de texte adoucie
        }
    }

    /**
     * Renvoie le modèle de données du Schtroumpf associé à cette ligne.
     */
    public SmurfCharacter getSmurf() {
        return this.smurf;
    }
    
    private HBox createHBox() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(8, 8, 8, 8));
        hbox.setSpacing(6);
        return hbox;
    }

    private void updateEnergyDisplay(int current, int max) {
        this.energyLabel.setText(current + " / " + max + " ⚡");
    }

    private void loadAvatar(Path spritePath) {
    	if (spritePath != null) {
			this.avatarView.setImage(new Image(spritePath.toUri().toString()));
    	}
    }
}
