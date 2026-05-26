package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import fr.uge.but.schtroumpf.model.crises.CrisisType;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;

/**
 * A reusable vertical alert card representing an active village crisis.
 * Displays the crisis name, the missing resource cause, and the systemic penalty.
 */
public class CrisisSummaryRow extends VBox {
    private final CrisisType crisisType;

    private final Runnable themeUpdater = this::applyCurrentThemeColors;

    public CrisisSummaryRow(CrisisType crisisType) {
        this.crisisType = crisisType;

        Color resourceColor = ThemeManager.getResourceColor(crisisType.getCause());
        String hexColor = colorToHex(resourceColor);
        
        
        // 1. Configure parent container constraints (Alert Card Style)
        this.setSpacing(6.0);
        this.setPadding(new Insets(12.0));
        this.setStyle(
            "-fx-background-color: #2d3139; " +
            "-fx-border-color: " + hexColor + "; " + 
            "-fx-border-width: 2; " +      // Thick accent line on the left side
            "-fx-background-radius: 4; " +
            "-fx-border-radius: 4;"
        );

        // 2. Build the Header Row (Icon, Title, and Cause)
        HBox headerRow = new HBox(10.0);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(crisisType.getName().toUpperCase());
        titleLabel.setTextFill(Color.web("#fca5a5")); // Soft alert red
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 13.0));

        // Note: Assumes ResourceType has a getDisplayName() or similar clean string output
        Label causeLabel = new Label("Manque de " + crisisType.getCause() + "");
        causeLabel.setTextFill(Color.web("#94a3b8")); // Muted slate gray
        causeLabel.setFont(Font.font("System", FontPosture.ITALIC, 11.0));

        headerRow.getChildren().addAll(titleLabel, causeLabel);

        // 3. Build the Description Body
        Label descriptionLabel = new Label(crisisType.getDescription());
        descriptionLabel.setTextFill(Color.web("#cbd5e1")); // Light slate
        descriptionLabel.setWrapText(true);
        descriptionLabel.setFont(Font.font("System", 12.0));

        // 4. Assemble the final node hierarchy
        this.getChildren().addAll(headerRow, descriptionLabel);
        
        ThemeManager.addThemeChangeListener(themeUpdater);
    }
    
    public CrisisType getCrisisType() {
        return this.crisisType;
    }
    
    private String colorToHex(Color color) {
    	return String.format("#%02X%02X%02X",
    			(int)(color.getRed() * 255),
    			(int)(color.getGreen() * 255),
    			(int)(color.getBlue() * 255)
    	);
    }

    private void applyCurrentThemeColors() {
        Color resourceColor = ThemeManager.getResourceColor(crisisType.getCause());
        String hexColor = colorToHex(resourceColor);

    	String style = this.getStyle();
    	style += "-fx-border-color: " + hexColor + "; ";
    	this.setStyle(style);
    }
}
