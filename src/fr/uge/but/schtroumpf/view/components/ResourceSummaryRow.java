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
import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;

/**
 * A reusable horizontal item row representing resource modifications
 * displayed inside the Consumption, Crisis, or Month End summary panels.
 */
public class ResourceSummaryRow extends HBox {
    private final ResourceType type;
    private final ImageView resourceIconImage;
    private final Label deltaLabel;
    private final Label nameLabel;

    public ResourceSummaryRow(ResourceType type) {
        this.type = type;

        // 1. Configure parent row container constraints matching your FXML properties
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefHeight(30.0);
        this.setSpacing(8.0);
        this.setPadding(new Insets(0, 15.0, 0, 15.0));

        // 2. Instantiate and configure the Resource Icon Graphic
        this.resourceIconImage = new ImageView();
        this.resourceIconImage.setFitHeight(24.0);
        this.resourceIconImage.setFitWidth(24.0);
        this.resourceIconImage.setPickOnBounds(true);
        this.resourceIconImage.setPreserveRatio(true);
        
        // Load the icon asset dynamically based on the enum name
        loadIconResource();

        // 3. Delta value text tracker (styled big and bold)
        this.deltaLabel = new Label();
        this.deltaLabel.setAlignment(Pos.CENTER);
        this.deltaLabel.setPrefWidth(35.0); // Slightly wider to safely hold double-digit numbers like "-10"
        this.deltaLabel.setGraphicTextGap(0.0);
        this.deltaLabel.setFont(Font.font("System", FontWeight.BOLD, 14.0));

        // 4. Structural Resource Identity Label
        this.nameLabel = new Label(type.getDisplayName());
        this.nameLabel.setTextFill(ThemeManager.getResourceSummaryTextColor(type));
        this.nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14.0));

        // 5. Build layout children node hierarchy
        this.getChildren().addAll(resourceIconImage, deltaLabel, nameLabel);
    }

    /**
     * Mutates the inner tracking labels dynamically based on phase output arrays.
     * * @param delta The quantity modifier applied to this specific resource type.
     */
    public void updateDelta(int delta) {
        if (delta > 0) {
            deltaLabel.setText("+" + delta);
            deltaLabel.setTextFill(Color.web("#10b981")); // Tailwind emerald green
        } else if (delta < 0) {
            deltaLabel.setText(String.valueOf(delta));
            deltaLabel.setTextFill(Color.web("#ef4444")); // Tailwind red
        } else {
            deltaLabel.setText("0");
            deltaLabel.setTextFill(Color.web("#94a3b8")); // Slate muted gray
        }
    }

    /**
     * Tries to locate graphic image indicators inside your build paths.
     */
    private void loadIconResource() {
        try {
            // Assumes images are named lowercase matching your identifiers, e.g., "berries.png"
            String resourcePath = "/icons/" + type.name().toLowerCase() + ".png";
            var url = getClass().getResource(resourcePath);
            
            if (url != null) {
                resourceIconImage.setImage(new Image(url.toExternalForm()));
            }
        } catch (Exception e) {
            // Fails silently to prevent crash logs if assets are missing during early dev steps
            resourceIconImage.setImage(null);
        }
    }
}
