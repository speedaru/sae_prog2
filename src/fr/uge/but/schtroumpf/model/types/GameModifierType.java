package fr.uge.but.schtroumpf.model.types;

import java.util.Objects;
import java.util.function.Function;

public enum GameModifierType {
    SUCCESS_CHANCE_BONUS(Double.class, "Chance", 0.0,
    	obj -> formatPct(obj)
    ),
    ENERGY_RECHARGE_RATE_DELTA(Integer.class, "Recuperation d'energie", 0,
    	obj -> formatIntDelta(obj)
    ),
    MAX_ENERGY_DELTA(Integer.class, "Max energie", 0, 
    	obj -> formatIntDelta(obj)
    ),
    EFFICIENCY_DELTA(Integer.class, "Efficacite des actions", 0,
    	obj -> formatIntDelta(obj)
    ),
    PRODUCTION_DELTA(Integer.class, "Production", 0,
    	obj -> formatIntDelta(obj)
    ),
    ABILITIES_PER_TURN_DELTA(Integer.class, "Actions par tour", 0,
    	obj -> formatIntDelta(obj)
	),
    PASSIVE_FOOD_PRODUCTION_BLOCKED(Boolean.class, "Production baies bloque", false,
    	obj -> formatBool(obj)
    ),
	CRISIS_SHIELD_COUNT(Integer.class, "Bouclier de crise", 0, 
		obj -> formatIntDelta(obj)
	);
	
    private final Class<?> type;
    private final String name;
    private final Object defaultValue;
    private final Function<Object, String> format;

    GameModifierType(Class<?> type, String name, Object defaultValue, Function<Object, String> format) {
        this.type = type;
        this.name = Objects.requireNonNull(name);
        this.defaultValue = defaultValue;
        this.format = Objects.requireNonNull(format);
    }

    public Class<?> getType() { return type; }
    public String getName() { return name; }
    public String formatDisplayValue(Object value) { return format.apply(value); }

    @SuppressWarnings("unchecked")
	public <T> T getDefaultValue() {
        return (T)defaultValue;
    }
    
    // ------------------------- private formaters

    private static String formatPct(Object obj) {
    	int pct = (int) Math.round(((double) obj) * 100);
    	return String.format(pct > 0 ? "+%d%%" : "%d%%", pct);
	}

    private static String formatIntDelta(Object obj) {
    	int i = (int)obj;
    	return String.format(i > 0 ? "+%d" : "%d", i);
    }

    private static String formatBool(Object obj) {
    	return (boolean)obj ? "oui" : "non";
    }
}
