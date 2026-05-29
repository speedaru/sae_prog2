package fr.uge.but.schtroumpf.model.types;

import fr.uge.but.schtroumpf.model.utils.Logger;

public class ModifierEffect {
    private final GameModifierType type;
    private final Object value;
    private int remainingRounds;
    private final boolean isCrisis;

    // flag so we start decreasing remainingRounds only when after entering a new round
    private boolean started = false;

    /** duration is duration in FULL rounds (doesnt count current round) */
    public ModifierEffect(GameModifierType type, Object value, int duration, boolean isCrisis) {
        this.type = type;
        this.value = value;
        this.remainingRounds = duration;
        this.isCrisis = isCrisis;
    }
    
    public static ModifierEffect crisisModifierEffect(GameModifierType type, Object value) {
    	return new ModifierEffect(type, value, 1, true);
    }

    public static ModifierEffect crisisModifierEffect(GameModifierType type, Object value, int duration) {
    	return new ModifierEffect(type, value, duration, true);
    }

    public GameModifierType getType() { return type; }
    public Object getValue() { return value; }
    public int getRemainingRounds() { return remainingRounds; }
    public boolean isCrisis() { return isCrisis; }
    public boolean started() { return started; }

    /**
     * starts the counter if not started, decrements round counter
     * @return true if the effect has expired
     **/
    public boolean tick() {
    	if (!started) {
    		started = true;
    		Logger.LogDebug("will start decreasing %s (%d rounds left)", type.getName(), remainingRounds);
    		return false;
    	}
    	
        remainingRounds--;
        return remainingRounds <= 0;
    }
}
