package fr.uge.but.schtroumpf.view.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;
import fr.uge.but.schtroumpf.model.ResourceManager;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class ResourceSidebarWidget extends VBox {
    // 1. Restore the dynamic ratio tracker!
    private final DoubleProperty fillRatio = new SimpleDoubleProperty(0.0);
    private final ResourceType type;
    private final Rectangle progressFill;
    private final Label quantityLabel;
    private final Label deltaLabel;

    private final Runnable themeUpdater = this::applyCurrentThemeColors;

    // 2. Remove the 'parent' argument. It's no longer needed!
    public ResourceSidebarWidget(ResourceType type) {
        this.type = type;
        
        this.setSpacing(4.0);
        this.setStyle("-fx-background-color: #2d3139; -fx-background-radius: 8;");
        this.setPadding(new Insets(10, 10, 10 ,10));
        
        // 3. THE FIX: Tell JavaFX this widget is allowed to stretch infinitely to fill its parent VBox.
        this.setMaxWidth(Double.MAX_VALUE);
        
        Label nameLabel = new Label(type.getDisplayName());
        nameLabel.setTextFill(Color.web("#e2e8f0"));
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        
        StackPane progressStack = new StackPane();
        progressStack.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(progressStack, Priority.ALWAYS); // Push everything to the right
        progressStack.setPadding(new Insets(4, 0, 4, 0));
        
        Rectangle backgroundTrack = new Rectangle(0, 24.0, Color.web("#1e1e24"));
        backgroundTrack.setArcWidth(8.0);
        backgroundTrack.setArcHeight(8.0);
        backgroundTrack.setStroke(Color.web("#4b5563"));
        
        // This bind is safe because it binds to the StackPane, which is controlled by the VBox
        backgroundTrack.setManaged(false);
        backgroundTrack.widthProperty().bind(progressStack.widthProperty());
        
        progressFill = new Rectangle(0.0, 24.0, ThemeManager.getResourceSidebarBarColor(type));
        progressFill.setArcWidth(6.0);
        progressFill.setArcHeight(6.0);
        StackPane.setMargin(progressFill, new Insets(0, 0, 0, 1.0));
        
        // 4. Restore the internal fill binding!
        progressFill.setManaged(false);
        progressFill.widthProperty().bind(progressStack.widthProperty().multiply(fillRatio));
        
        quantityLabel = new Label("0 / " + ResourceManager.MAX_QUANTITY);
        quantityLabel.setTextFill(Color.WHITE);
        quantityLabel.setPadding(new Insets(0, 0, 0, 8.0));
        quantityLabel.setFont(Font.font("System", FontWeight.BOLD, 11.0));
        quantityLabel.setAlignment(Pos.BOTTOM_LEFT);
        StackPane.setAlignment(quantityLabel, Pos.CENTER_LEFT);
        
        progressStack.getChildren().addAll(backgroundTrack, progressFill, quantityLabel);
        
        deltaLabel = new Label();
        deltaLabel.setFont(Font.font("System", FontWeight.BOLD, 13.0));
        deltaLabel.setPrefWidth(20.0);
        deltaLabel.setMinWidth(20.0);
        deltaLabel.setMaxWidth(20.0);
        deltaLabel.setAlignment(Pos.BOTTOM_RIGHT);
        
        HBox interactionRow = new HBox(10.0, progressStack, deltaLabel);
        interactionRow.setAlignment(Pos.CENTER_LEFT);
        
        this.getChildren().addAll(nameLabel, interactionRow);
        ThemeManager.addThemeChangeListener(themeUpdater);
    }

    public void updateState(int quantity, int delta) {
        quantityLabel.setText(quantity + " / " + ResourceManager.MAX_QUANTITY);
        
        if (delta > 0) {
            deltaLabel.setText("+" + delta);
            deltaLabel.setStyle("-fx-text-fill: #10b981;"); 
        } else if (delta < 0) {
            deltaLabel.setText(String.valueOf(delta));
            deltaLabel.setStyle("-fx-text-fill: #ef4444;"); 
        } else {
            deltaLabel.setText("");
        }
        
        // 5. Restore the ratio update logic!
        double ratio = (double) quantity / ResourceManager.MAX_QUANTITY;
        fillRatio.set(Math.max(0.0, Math.min(1.0, ratio))); // Clamped for safety
    }

    public void refreshColors() {
        progressFill.setFill(ThemeManager.getResourceSidebarBarColor(type));
    }
    
    private void applyCurrentThemeColors() {
        this.progressFill.setFill(ThemeManager.getResourceSidebarBarColor(type));
    }
}
