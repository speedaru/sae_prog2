package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Objects;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.model.characters.SmurfType;

public class SmurfListRow extends HBox {
    private final SmurfCharacter smurf;
    private final ImageView avatarView;
    private final Label nameLabel;
    private final Label energyLabel;
    private boolean isExhausted = false;
    
    private final Color labelColor = Color.WHITE;
    private final Color exhaustedLabelColor = Color.web("#ef4444");

    public SmurfListRow(SmurfVillage village, SmurfCharacter smurf) {
        super();
        this.smurf = Objects.requireNonNull(smurf, "Le modèle de données du Schtroumpf ne peut pas être nul.");

        this.setStyle(
            "-fx-background-color: #202225; " +
            "-fx-background-radius: 6; " +
            "-fx-border-color: #3f444c; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand;"
        );

        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(8, 8, 8, 8));
        this.setSpacing(6);

        this.avatarView = new ImageView();
        this.avatarView.setFitWidth(24);
        this.avatarView.setFitHeight(24);
        this.avatarView.setPreserveRatio(true);
        this.avatarView.setSmooth(true);
        SmurfType type = smurf.getType();
        loadAvatar(type.getSpritePath());

        this.nameLabel = new Label(smurf.getType().getName());
        this.nameLabel.setTextFill(labelColor);
        this.nameLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        HBox.setHgrow(this.nameLabel, Priority.ALWAYS);
        
        Region regionSeparator = new Region();
        HBox.setHgrow(regionSeparator, Priority.ALWAYS);

        this.energyLabel = new Label();
        this.energyLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        this.energyLabel.setTextFill(labelColor);
        updateEnergyDisplay(smurf.getEnergy(), village.getDynamicMaxEnergy(smurf));

        this.getChildren().addAll(this.avatarView, this.nameLabel, regionSeparator, this.energyLabel);

		setExhaustedState(smurf.getEnergy());
    }

    public void updateEnergy(int currentEnergy, int maxEnergy) {
        updateEnergyDisplay(currentEnergy, maxEnergy);
        setExhaustedState(currentEnergy);
    }

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

    public void setExhaustedState(int energy) {
        this.isExhausted = energy <= 0;
        if (this.isExhausted) {
            this.setOpacity(0.45);
            this.energyLabel.setTextFill(exhaustedLabelColor);
        } else {
            this.setOpacity(1.0);
            this.energyLabel.setTextFill(labelColor);
        }
    }

    public SmurfCharacter getSmurf() {
        return this.smurf;
    }
    
    private void updateEnergyDisplay(int current, int max) {
        this.energyLabel.setText(String.format("%d/%d ⚡", current, max));
    }

    private void loadAvatar(String spritePath) {
    	if (spritePath != null) {
			this.avatarView.setImage(new Image(spritePath));
    	}
    }
}
