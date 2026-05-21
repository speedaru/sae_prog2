package fr.uge.but.schtroumpf.view.themes;

import fr.uge.but.schtroumpf.model.ResourceType;

public class ThemeManager {
    private static ResourceTheme currentTheme = ResourceTheme.STANDARD;

    public static javafx.scene.paint.Color getBarColor(ResourceType type) {
        if (currentTheme == ResourceTheme.COLOR_BLIND) {
            return switch (type) {
                case BERRIES -> javafx.scene.paint.Color.ORANGE; // Accessible contrast
                case GOLD -> javafx.scene.paint.Color.BLUE;
                default -> javafx.scene.paint.Color.GRAY;
            };
        }

        // Standard theme colors
        return switch (type) {
            case BERRIES -> javafx.scene.paint.Color.web("#ef4444"); // Red
            case GOLD -> javafx.scene.paint.Color.web("#eab308");    // Yellow/Gold
            default -> javafx.scene.paint.Color.web("#3b82f6");      // Blue
        };
    }

	public enum ResourceTheme {
		STANDARD, COLOR_BLIND;
	}
}
