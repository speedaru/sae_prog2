package fr.uge.but.schtroumpf.view.themes;

import fr.uge.but.schtroumpf.model.ResourceType;

import javafx.scene.paint.Color;

public class ThemeManager {
    private static ResourceTheme currentTheme = ResourceTheme.STANDARD;

    public static Color getResourceSidebarBarColor(ResourceType type) {
    	return getResourceThemeColor(type);
    }

    public static Color getResourceSummaryTextColor(ResourceType type) {
    	return getResourceThemeColor(type);
    }

    private static Color getResourceThemeColor(ResourceType type) {
    	// color blind theme
		if (currentTheme == ResourceTheme.COLOR_BLIND) {
			return switch (type) {
			case BERRIES -> Color.web("#D55E00"); // natural berry red-orange
			case SARSAPARILLA -> Color.web("#009E73"); // herbal / plant green
			case GOLD -> Color.web("#F0E442"); // metallic gold / treasure
			case TOOLS -> Color.web("#7A7A7A"); // steel / crafted tools
			case MORAL -> Color.web("#CC79A7"); // warm morale / community
			case DEFENSE -> Color.web("#0072B2"); // defensive military blue
			case KNOWLEDGE -> Color.web("#56B4E9"); // arcane / knowledge cyan
			};
		}

		// Standard theme
		return switch (type) {
		case BERRIES -> Color.web("#C62828"); // deep berry crimson
		case SARSAPARILLA -> Color.web("#2E8B57"); // earthy medicinal green
		case GOLD -> Color.web("#D4AF37"); // rich gold
		case TOOLS -> Color.web("#6D4C41"); // iron / bronze tool color
		case MORAL -> Color.web("#FF6F61"); // warm hopeful morale color
		case DEFENSE -> Color.web("#3F51B5"); // defensive steel blue
		case KNOWLEDGE -> Color.web("#7E57C2"); // mystical knowledge purple
		};
    }

	public enum ResourceTheme {
		STANDARD, COLOR_BLIND;
	}
}
