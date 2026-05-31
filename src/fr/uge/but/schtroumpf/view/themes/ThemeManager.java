package fr.uge.but.schtroumpf.view.themes;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import javafx.scene.paint.Color;

public class ThemeManager {
	// weak references to not store unreferenced ui widgets
    private static final List<WeakReference<Runnable>> listeners = new ArrayList<>();
    private static ResourceTheme currentTheme = ResourceTheme.STANDARD;

    public static void addThemeChangeListener(Runnable listener) {
        listeners.add(new WeakReference<>(listener));
    }
    
    public static void setCurrentTheme(ResourceTheme newTheme) {
    	currentTheme = newTheme;
    	
    	// iterate backwards so we can remove unreferenced widgets
        for (int i = listeners.size() - 1; i >= 0; i--) {
            Runnable listener = listeners.get(i).get();
            if (listener != null) {
                listener.run(); // update widget color
            } else {
                listeners.remove(i); // unreferenced widget
            }
        }
    }
    
    public static Color getResourceSidebarBarColor(ResourceType type) {
    	return getResourceThemeColor(type);
    }

    public static Color getResourceColor(ResourceType type) {
    	return getResourceThemeColor(type);
    }
    
    public static Color getCrisisColor(ResourceType type) {
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
		case SUCCESS -> Color.web("#10b981");
		case FAILURE -> Color.web("#ef4444");
		case NEUTRAL -> Color.web("#3b82f6");
		};    
    }

    private static Color getResourceThemeColor(ResourceType type) {
    	// color blind theme
		if (currentTheme == ResourceTheme.COLOR_BLIND) {
			return switch (type) {
			case BERRIES -> Color.web("#D55E00");
			case SARSAPARILLA -> Color.web("#009E73");
			case GOLD -> Color.web("#F0E442");
			case TOOLS -> Color.web("#7A7A7A");
			case MORAL -> Color.web("#CC79A7");
			case DEFENSE -> Color.web("#0072B2");
			case KNOWLEDGE -> Color.web("#56B4E9");
			};
		}

		// standard theme
		return switch (type) {
		case BERRIES -> Color.web("#C62828");
		case SARSAPARILLA -> Color.web("#2E8B57");
		case GOLD -> Color.web("#D4AF37");
		case TOOLS -> Color.web("#6D4C41");
		case MORAL -> Color.web("#FF6F61");
		case DEFENSE -> Color.web("#3F51B5");
		case KNOWLEDGE -> Color.web("#7E57C2");
		};
    }

	public enum ResourceTheme {
		STANDARD, COLOR_BLIND;
	}
}
