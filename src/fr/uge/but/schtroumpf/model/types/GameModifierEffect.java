package fr.uge.but.schtroumpf.model.types;

public class GameModifierEffect {
    private final GameModifierType type;
    private final Object value;
    private int remainingRounds;
    private final boolean isCrisis;

    public GameModifierEffect(GameModifierType type, Object value, int duration, boolean isCrisis) {
        this.type = type;
        this.value = value;
        this.remainingRounds = duration;
        this.isCrisis = isCrisis;
    }

    public GameModifierType getType() { return type; }
    public Object getValue() { return value; }
    public int getRemainingRounds() { return remainingRounds; }
    public boolean isCrisis() { return isCrisis; }

    /**
     * decrements round counter
     * @return true if the effect has expired
     **/
    public boolean tick() {
        remainingRounds--;
        return remainingRounds <= 0;
    }
}
