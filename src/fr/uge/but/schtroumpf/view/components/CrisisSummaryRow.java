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
import fr.uge.but.schtroumpf.model.utils.ColorUtils;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;

public class CrisisSummaryRow extends VBox {
    private final CrisisType crisisType;

    private final Runnable themeUpdater = this::applyCurrentThemeColors;

    public CrisisSummaryRow(CrisisType crisisType) {
        this.crisisType = crisisType;

        Color resourceColor = ThemeManager.getResourceColor(crisisType.getCause());
        String hexColor = ColorUtils.colorToHex(resourceColor);
        
        this.setSpacing(6.0);
        this.setPadding(new Insets(12.0));
        this.setStyle(
            "-fx-background-color: #2d3139; " +
            "-fx-border-color: " + hexColor + "; " + 
            "-fx-border-width: 2; " +
            "-fx-background-radius: 4; " +
            "-fx-border-radius: 4;"
        );

        HBox headerRow = new HBox(10.0);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(crisisType.getName().toUpperCase());
        titleLabel.setTextFill(Color.web("#fca5a5"));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 13.0));

        Label causeLabel = new Label("Manque de " + crisisType.getCause() + "");
        causeLabel.setTextFill(Color.web("#94a3b8"));
        causeLabel.setFont(Font.font("System", FontPosture.ITALIC, 11.0));

        headerRow.getChildren().addAll(titleLabel, causeLabel);

        Label descriptionLabel = new Label(crisisType.getDescription());
        descriptionLabel.setTextFill(Color.web("#cbd5e1"));
        descriptionLabel.setWrapText(true);
        descriptionLabel.setFont(Font.font("System", 12.0));

        this.getChildren().addAll(headerRow, descriptionLabel);
        
        ThemeManager.addThemeChangeListener(themeUpdater);
    }
    
    public CrisisType getCrisisType() {
        return this.crisisType;
    }
    
    private void applyCurrentThemeColors() {
        Color resourceColor = ThemeManager.getResourceColor(crisisType.getCause());
        String hexColor = ColorUtils.colorToHex(resourceColor);

    	String style = this.getStyle();
    	style += "-fx-border-color: " + hexColor + "; ";
    	this.setStyle(style);
    }
}
