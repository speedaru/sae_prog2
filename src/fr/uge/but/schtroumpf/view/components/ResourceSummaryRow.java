package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.nio.file.Path;

import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;

public class ResourceSummaryRow extends HBox {
    private final ResourceType type;
    private final ImageView resourceIconImage;
    private final Label deltaLabel;
    private final Label nameLabel;
    private final boolean displayingDelta;
    
    private final Runnable themeUpdater = this::applyCurrentThemeColors;

    public ResourceSummaryRow(ResourceType type) {
        this(type, true);
    }

    public ResourceSummaryRow(ResourceType type, boolean displayingDelta) {
        this.type = type;
        this.displayingDelta = displayingDelta;

        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefHeight(30.0);
        this.setSpacing(2);
        this.setPadding(new Insets(0, 15.0, 0, 15.0));

        this.resourceIconImage = new ImageView();
        this.resourceIconImage.setFitHeight(24.0);
        this.resourceIconImage.setFitWidth(24.0);
        this.resourceIconImage.setPickOnBounds(true);
        this.resourceIconImage.setPreserveRatio(true);
        
        loadIconResource();

        this.deltaLabel = new Label();
        this.deltaLabel.setAlignment(Pos.CENTER);
        this.deltaLabel.setPrefWidth(30.0);
        this.deltaLabel.setFont(Font.font("System", FontWeight.BOLD, 14.0));

        this.nameLabel = new Label(type.getDisplayName());
        this.nameLabel.setTextFill(ThemeManager.getResourceColor(type));
        this.nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14.0));

        this.getChildren().addAll(resourceIconImage, deltaLabel, nameLabel);
        
        ThemeManager.addThemeChangeListener(themeUpdater);
    }

    public void updateDelta(int value) {
        if (displayingDelta) {
            if (value > 0) {
                deltaLabel.setText("+" + value);
                deltaLabel.setTextFill(Color.web("#10b981"));
            } else if (value < 0) {
                deltaLabel.setText(String.valueOf(value));
                deltaLabel.setTextFill(Color.web("#ef4444"));
            } else {
                deltaLabel.setText("0");
                deltaLabel.setTextFill(Color.web("#94a3b8"));
            }
        } else {
            deltaLabel.setText(String.valueOf(Math.abs(value)));
            deltaLabel.setTextFill(Color.web("#cbd5e1"));
        }
    }

    private void applyCurrentThemeColors() {
    	this.nameLabel.setTextFill(ThemeManager.getResourceColor(type));
    }
    
    private void loadIconResource() {
		String resource = "src/main/resources/icons/" + type.name().toLowerCase() + ".png";
		Path path = Path.of(resource);
		try {
			resourceIconImage.setImage(new Image(path.toUri().toString()));
		} catch (Exception e) {
			resourceIconImage.setImage(null);
		}
    }
}
