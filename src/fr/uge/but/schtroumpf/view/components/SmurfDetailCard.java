package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Objects;

import fr.uge.but.schtroumpf.model.types.ResourceType;

public class SmurfDetailCard extends HBox {
    private static final double RESOURCE_INDICATOR_SIZE = 32;
	private static final int INDICATORS_COUNT = 3;

    private final ImageView portraitView;
    private final Label nameLabel;
    private final Label roleLabel;
    private final Label energyLabel;
	private final HBox resourceIndicators;

    public SmurfDetailCard() {
        super();

        this.setPadding(new Insets(12));
        this.setAlignment(Pos.BOTTOM_LEFT);
        this.setSpacing(15);
        this.setFillHeight(false);
        HBox.setHgrow(this, Priority.ALWAYS);
        this.setStyle(
            "-fx-background-color: #202225; " +
            "-fx-background-radius: 8; "
        );

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

        VBox textLayout = new VBox();
        textLayout.setAlignment(Pos.CENTER_LEFT);
        textLayout.setSpacing(3);
        HBox.setHgrow(textLayout, Priority.ALWAYS);

        this.nameLabel = new Label("Aucun membre sélectionné");
        this.nameLabel.setTextFill(Color.WHITE);
        this.nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        this.roleLabel = new Label("Sélectionnez un membre pour assigner ses actions");
        this.roleLabel.setTextFill(Color.web("#94a3b8"));
        this.roleLabel.setFont(Font.font("System", FontPosture.ITALIC, 11));

        this.energyLabel = new Label("Énergie : -- / -- ⚡");
        this.energyLabel.setTextFill(Color.web("#3b82f6"));
        this.energyLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        textLayout.getChildren().addAll(this.nameLabel, this.roleLabel, this.energyLabel);

        Region regionSeparator = new Region();
        HBox.setHgrow(regionSeparator, Priority.ALWAYS);
        
        // resource indicators
        this.resourceIndicators = new HBox();
        this.resourceIndicators.setMaxWidth(RESOURCE_INDICATOR_SIZE * INDICATORS_COUNT);
        this.resourceIndicators.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        this.resourceIndicators.setSpacing(8);
        
        this.getChildren().addAll(portraitFrame, textLayout, regionSeparator, resourceIndicators);
    }

    public void updateEnergy(int newEnergy, int maxEnergy) {
        this.energyLabel.setText("Énergie : " + newEnergy + " / " + maxEnergy + " ⚡");
    }

    public void updateData(String name, String description, int currentEnergy, int maxEnergy) {
        this.nameLabel.setText(Objects.requireNonNull(name, "Le nom ne peut pas être nul."));
        this.roleLabel.setText(Objects.requireNonNull(description, "La description ne peut pas être nulle."));
		updateEnergy(currentEnergy, maxEnergy);
        
        if (currentEnergy <= 0) {
            this.energyLabel.setTextFill(Color.web("#ef4444"));
        } else {
            this.energyLabel.setTextFill(Color.web("#3b82f6"));
        }
    }

    public void setPortrait(String portraitPath) {
    	if (portraitPath != null) {
			this.portraitView.setImage(new Image(portraitPath));
    	}
    }
    
    public void setAssociatedResources(List<ResourceType> resources) {
    	this.resourceIndicators.getChildren().clear();

    	for (ResourceType type : resources) {
			ImageView resourceIcon = new ImageView(new Image(
				type.getSpritePath()
			));
			resourceIcon.setFitWidth(RESOURCE_INDICATOR_SIZE);
			resourceIcon.setFitHeight(RESOURCE_INDICATOR_SIZE);
			this.resourceIndicators.getChildren().add(resourceIcon);
    	}
    }
}
