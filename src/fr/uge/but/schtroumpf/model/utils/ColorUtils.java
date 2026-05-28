package fr.uge.but.schtroumpf.model.utils;

import javafx.scene.paint.Color;

public class ColorUtils {
    public static String colorToHex(Color color) {
    	return String.format("#%02X%02X%02X",
    			(int)(color.getRed() * 255),
    			(int)(color.getGreen() * 255),
    			(int)(color.getBlue() * 255)
    	);
    }
    
    /** 0 <= amount <= 255 */
    public static Color darker(Color color, int amount) {
    	if (amount < 0) {
    		throw new IllegalArgumentException("amount must be positive");
    	}
    	
    	double amountFloat = amount / 255.0;
    	
        return new Color(
            Math.max(0, color.getRed() - amountFloat),
            Math.max(0, color.getGreen() - amountFloat),
            Math.max(0, color.getBlue() - amountFloat),
            color.getOpacity()
        );
    }
}
