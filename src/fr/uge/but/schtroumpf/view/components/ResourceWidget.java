package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;
import fr.uge.but.schtroumpf.model.ResourceManager;

public class ResourceWidget extends VBox {
    private final ResourceType type;
    private final Rectangle progressFill;
    private final Label quantityLabel;
    private final Label deltaLabel;

    public ResourceWidget(ResourceType type) {
        this.type = type;
        
        // 1. Configure parent container styling (Matching your FXML attributes)
        this.setSpacing(4.0);
        this.setStyle("-fx-background-color: #2d3139; -fx-padding: 10; -fx-background-radius: 8;");
        
        // 2. Resource Name Header Label
        Label nameLabel = new Label(type.getDisplayName()); // Uses enum configuration
        nameLabel.setTextFill(Color.web("#e2e8f0"));
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        
        // 3. Progress Track Stack
        StackPane progressStack = new StackPane();
        progressStack.setAlignment(Pos.CENTER_LEFT);
        progressStack.setPrefSize(130.0, 24.0);
        
        Rectangle backgroundTrack = new Rectangle(130.0, 24.0, Color.web("#1e1e24"));
        backgroundTrack.setArcWidth(8.0);
        backgroundTrack.setArcHeight(8.0);
        backgroundTrack.setStroke(Color.web("#4b5563"));
        
        progressFill = new Rectangle(0.0, 22.0, ThemeManager.getBarColor(type));
        progressFill.setArcWidth(8.0);
        progressFill.setArcHeight(8.0);
        
        quantityLabel = new Label("0 / " + ResourceManager.MAX_QUANTITY);
        quantityLabel.setTextFill(Color.WHITE);
        quantityLabel.setPadding(new Insets(0, 0, 0, 8.0));
        quantityLabel.setFont(Font.font("System", FontWeight.BOLD, 11.0));
        
        progressStack.getChildren().addAll(backgroundTrack, progressFill, quantityLabel);
        
        // 4. Delta text tracking box
        deltaLabel = new Label();
        deltaLabel.setFont(Font.font("System", FontWeight.BOLD, 13.0));
        
        HBox interactionRow = new HBox(10.0, progressStack, deltaLabel);
        interactionRow.setAlignment(Pos.CENTER_LEFT);
        
        // 5. Pack children structural graphs
        this.getChildren().addAll(nameLabel, interactionRow);
    }

    /**
     * Pure updating logic completely decoupled from master initialization overheads.
     */
    public void updateState(int currentQuantity, int delta) {
        // Safe proportional scaling mapping to 130.0px container tracks
        double widthRatio = (double) currentQuantity / ResourceManager.MAX_QUANTITY;
        progressFill.setWidth(130.0 * Math.clamp(widthRatio, 0.0, 1.0));
        
        quantityLabel.setText(currentQuantity + " / " + ResourceManager.MAX_QUANTITY);
        
        if (delta > 0) {
            deltaLabel.setText("+" + delta);
            deltaLabel.setStyle("-fx-text-fill: #10b981;"); // Green
        } else if (delta < 0) {
            deltaLabel.setText(String.valueOf(delta));
            deltaLabel.setStyle("-fx-text-fill: #ef4444;"); // Red
        } else {
            deltaLabel.setText("");
        }
    }

    /** Called when the user flips the color-blind setting */
    public void refreshColors() {
        progressFill.setFill(ThemeManager.getBarColor(type));
    }
}
