package fr.uge.but.schtroumpf.view.themes;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import javafx.scene.paint.Color;

public class ThemeManager {
	// Stores weak references so we don't accidentally keep dead UI widgets alive in RAM
    private static final List<WeakReference<Runnable>> listeners = new ArrayList<>();
    private static ResourceTheme currentTheme = ResourceTheme.STANDARD;

    public static void addThemeChangeListener(Runnable listener) {
        listeners.add(new WeakReference<>(listener));
    }
    
    public static void setCurrentTheme(ResourceTheme newTheme) {
    	currentTheme = newTheme;
    	
    	// Iterate backward to safely remove dead references while firing active ones
        for (int i = listeners.size() - 1; i >= 0; i--) {
            Runnable listener = listeners.get(i).get();
            if (listener != null) {
                listener.run(); // Widget is alive, update its colors!
            } else {
                listeners.remove(i); // Widget was garbage collected, prune the dead link
            }
        }
    }
    
    public static Color getResourceSidebarBarColor(ResourceType type) {
    	return getResourceThemeColor(type);
    }

    public static Color getResourceColor(ResourceType type) {
    	return getResourceThemeColor(type);
    }
    
    public static Color getAbilityResultTypeColor(AbilityResultType resType) {
    	if (currentTheme == ResourceTheme.COLOR_BLIND) {
    		return switch (resType) {
    		case SUCCESS -> Color.ALICEBLUE;
    		case FAILURE -> Color.ALICEBLUE;
    		case NEUTRAL -> Color.ALICEBLUE;
    		};    
    	}
		return switch (resType) {
		case SUCCESS -> Color.web("#10b981"); // Tailwind Vibrant Emerald Green
		case FAILURE -> Color.web("#ef4444"); // Tailwind Clear Contrast Red
		case NEUTRAL -> Color.web("#3b82f6"); // Strategy Soft Alert Blue
		};    
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
