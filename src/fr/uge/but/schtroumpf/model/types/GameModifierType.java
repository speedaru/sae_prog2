package fr.uge.but.schtroumpf.model.types;

public enum GameModifierType {
    SUCCESS_CHANCE_BONUS(Double.class, 0.0),
    ENERGY_RECHARGE_RATE_DELTA(Integer.class, 0),
    MAX_ENERGY_DELTA(Integer.class, 0),
    EFFICIENCY_MULTIPLIER(Double.class, 1.0),
    PASSIVE_FOOD_PRODUCTION_BLOCKED(Boolean.class, false);
	
    private final Class<?> type;
    private final Object defaultValue;

    GameModifierType(Class<?> type, Object defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public Class<?> getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
	public <T> T getDefaultValue() {
        return (T)defaultValue;
    }
}
