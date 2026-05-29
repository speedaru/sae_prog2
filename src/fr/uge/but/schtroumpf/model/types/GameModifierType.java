package fr.uge.but.schtroumpf.model.types;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum GameModifierType {
    SUCCESS_CHANCE_BONUS(Double.class, "Chance", 0.0,
    	obj -> formatPct(obj),
    	GameModifierType::addDouble
    ),
    ENERGY_RECHARGE_RATE_DELTA(Integer.class, "Recuperation d'energie", 0,
    	obj -> formatIntDelta(obj),
    	GameModifierType::addInt
    ),
    MAX_ENERGY_DELTA(Integer.class, "Max energie", 0, 
    	obj -> formatIntDelta(obj),
    	GameModifierType::addInt
    ),
    EFFICIENCY_DELTA(Integer.class, "Efficacite des actions", 0,
    	obj -> formatIntDelta(obj),
    	GameModifierType::addInt
    ),
    PRODUCTION_DELTA(Integer.class, "Production", 0,
    	obj -> formatIntDelta(obj),
    	GameModifierType::addInt
    ),
    ABILITIES_PER_TURN_DELTA(Integer.class, "Actions par tour", 0,
    	obj -> formatIntDelta(obj),
    	GameModifierType::addInt
	),
    PASSIVE_FOOD_PRODUCTION_BLOCKED(Boolean.class, "Production baies bloque", false,
    	obj -> formatBool(obj),
    	GameModifierType::addBool
    ),
	CRISIS_SHIELD_COUNT(Integer.class, "Bouclier de crise", 0, 
		obj -> formatIntDelta(obj),
    	GameModifierType::addInt
	);
	
    private final Class<?> type;
    private final String name;
    private final Object defaultValue;
    private final Function<Object, String> format;
    private final BiFunction<Object, Object, Object> addFunc;

	GameModifierType(Class<?> type, String name, Object defaultValue, Function<Object, String> format,
			BiFunction<Object, Object, Object> addFunc) {
        this.type = type;
        this.name = Objects.requireNonNull(name);
        this.defaultValue = defaultValue;
        this.format = Objects.requireNonNull(format);
        this.addFunc = Objects.requireNonNull(addFunc);
    }


    public Class<?> getType() { return type; }
    public String getName() { return name; }
    public String formatDisplayValue(Object value) { return format.apply(value); }
    public Object combine(Object a, Object b) { return addFunc.apply(a, b); }

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
    
    private static Object addDouble(Object a, Object b) { return (Double)a + (Double)b; }
    private static Object addInt(Object a, Object b) { return (Integer)a + (Integer)b; }
    private static Object addBool(Object a, Object b) { return (Boolean)a | (Boolean)b; }
}
