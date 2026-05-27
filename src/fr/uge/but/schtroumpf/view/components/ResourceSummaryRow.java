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

/**
 * A reusable horizontal item row representing resource modifications
 * displayed inside the Consumption, Crisis, or Month End summary panels.
 * Features an optional flag to display flat values (for costs) or delta values (for yields).
 */
public class ResourceSummaryRow extends HBox {
    private final ResourceType type;
    private final ImageView resourceIconImage;
    private final Label deltaLabel;
    private final Label nameLabel;
    private final boolean displayingDelta;
    
    private final Runnable themeUpdater = this::applyCurrentThemeColors;

    /**
     * Default constructor - displays standard colored deltas with + or - signs.
     */
    public ResourceSummaryRow(ResourceType type) {
        this(type, true);
    }

    /**
     * Customizable constructor to toggle between Delta representations and flat quantities.
     */
    public ResourceSummaryRow(ResourceType type, boolean displayingDelta) {
        this.type = type;
        this.displayingDelta = displayingDelta;

        // 1. Configure parent row container constraints matching your FXML properties
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefHeight(30.0);
        this.setSpacing(2);
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
        this.deltaLabel.setPrefWidth(30.0); // Slightly wider to safely hold double-digit numbers like "-10"
        this.deltaLabel.setFont(Font.font("System", FontWeight.BOLD, 14.0));

        // 4. Structural Resource Identity Label
        this.nameLabel = new Label(type.getDisplayName());
        this.nameLabel.setTextFill(ThemeManager.getResourceColor(type));
        this.nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14.0));

        // 5. Build layout children node hierarchy
        this.getChildren().addAll(resourceIconImage, deltaLabel, nameLabel);
        
        ThemeManager.addThemeChangeListener(themeUpdater);
    }

    /**
     * Mutates the inner tracking labels dynamically based on phase output arrays.
     * @param value The quantity modifier or cost applied to this specific resource type.
     */
    public void updateDelta(int value) {
        if (displayingDelta) {
            if (value > 0) {
                deltaLabel.setText("+" + value);
                deltaLabel.setTextFill(Color.web("#10b981")); // Tailwind emerald green
            } else if (value < 0) {
                deltaLabel.setText(String.valueOf(value));
                deltaLabel.setTextFill(Color.web("#ef4444")); // Tailwind red
            } else {
                deltaLabel.setText("0");
                deltaLabel.setTextFill(Color.web("#94a3b8")); // Slate muted gray
            }
        } else {
            // Flat requirement representation (plain, clean slate color)
            deltaLabel.setText(String.valueOf(Math.abs(value)));
            deltaLabel.setTextFill(Color.web("#cbd5e1")); // Slate white
        }
    }

    private void applyCurrentThemeColors() {
    	this.nameLabel.setTextFill(ThemeManager.getResourceColor(type));
    }
    
    /**
     * Tries to locate graphic image indicators inside your build paths.
     */
    private void loadIconResource() {
		// Assumes images are named lowercase matching your identifiers, e.g., "berries.png"
		String resource = "src/main/resources/icons/" + type.name().toLowerCase() + ".png";
		Path path = Path.of(resource);
		try {
			resourceIconImage.setImage(new Image(path.toUri().toString()));
		} catch (Exception e) {
			resourceIconImage.setImage(null);
		}
    }
}
