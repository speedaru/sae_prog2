package fr.uge.but.schtroumpf.model.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillageModifierContext {
    private final Map<GameModifierType, Object> persistentModifiers = new HashMap<>();
    private final List<GameModifierEffect> temporaryModifiers = new ArrayList<>();

    public VillageModifierContext() { }

    /** handles infinite stacking properties via the map */
    public void addPersistentInt(GameModifierType type, int delta) {
        int current = 0;
        if (persistentModifiers.containsKey(type)) {
            current = (Integer) persistentModifiers.get(type);
        }
        
        int newValue = current + delta;
		persistentModifiers.put(type, newValue);
    }

    /** temporary effects that last fixed rounds amount */
    public void addTemporaryEffect(GameModifierType type, Object value, int duration, boolean isCrisis) {
        temporaryModifiers.add(new GameModifierEffect(type, value, duration, isCrisis));
    }

    /** decrements all temporary effect durations and remove finished */
    public void tickRounds() {
        List<GameModifierEffect> activeOnly = new ArrayList<>();
        for (GameModifierEffect effect : temporaryModifiers) {
            boolean expired = effect.tick();
            if (!expired) {
                activeOnly.add(effect);
            }
        }
        temporaryModifiers.clear();
        temporaryModifiers.addAll(activeOnly);
    }

    /** removes all temp effects that were added by crisis */
    public void clearCrisisEffects() {
        List<GameModifierEffect> smurfBuffsOnly = new ArrayList<>();
        for (GameModifierEffect effect : temporaryModifiers) {
            if (!effect.isCrisis()) {
                smurfBuffsOnly.add(effect);
            }
        }
        temporaryModifiers.clear();
        temporaryModifiers.addAll(smurfBuffsOnly);
    }

    public int getInt(GameModifierType type) {
        int total = (Integer) type.getDefaultValue();

        // get persistent amount
        if (persistentModifiers.containsKey(type)) {
            total += (Integer) persistentModifiers.get(type);
        }

        // get temp amount
        for (GameModifierEffect effect : temporaryModifiers) {
            if (effect.getType() == type) {
                total += (Integer) effect.getValue();
            }
        }
        return total;
    }

    public double getDouble(GameModifierType type) {
        double total = (Double) type.getDefaultValue();

        // get persistent amount
        if (persistentModifiers.containsKey(type)) {
            total += (Double) persistentModifiers.get(type);
        }

        // get temp amount
        for (GameModifierEffect effect : temporaryModifiers) {
            if (effect.getType() == type) {
                total += (Double) effect.getValue();
            }
        }
        return total;
    }

    public boolean getBool(GameModifierType type) {
        boolean total = (Boolean) type.getDefaultValue();

        // get persistent amount
        if (persistentModifiers.containsKey(type)) {
            total = total || (Boolean) persistentModifiers.get(type);
        }

        // or at least 1 persistent amount
        for (GameModifierEffect effect : temporaryModifiers) {
            if (effect.getType() == type) {
                total = total || (Boolean) effect.getValue();
            }
        }
        return total;
    }
}
