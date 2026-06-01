package fr.uge.but.schtroumpf.model.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.but.schtroumpf.model.save.GameSave;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class VillageModifierContext {
    private final Map<GameModifierType, Object> persistentModifiers = new HashMap<>();
    private final List<ModifierEffect> temporaryModifiers = new ArrayList<>();

    public VillageModifierContext() {}
    
    /** constructor from deserialized data */
    public VillageModifierContext(GameSave.VillageModifierCtxState state) {
    	// load persisten modifiers
    	for (var entry : state.persistentModifiers().entrySet()) {
    		this.persistentModifiers.put(entry.getKey(), entry.getValue());
    	}

    	// restore temp modifiers
        for (var tempState : state.temporaryModifiers()) {
        	ModifierEffect effect = new ModifierEffect(
                tempState.type(),
                tempState.value(),
                tempState.remainingRounds(),
                tempState.isCrisis()
            );
        	effect.setStarted(tempState.started());

            this.temporaryModifiers.add(effect);
        	Logger.LogDebug("loaded temp modifier: %s", temporaryModifiers.getLast());
        }
    }

    public GameSave.VillageModifierCtxState serialize() {
    	// copy persistent modifiers
		Map<GameModifierType, Object> persistentStates = new HashMap<>(this.persistentModifiers);

		// get temp state
		List<GameSave.TemporaryModifierState> tempStates = new java.util.ArrayList<>();
		for (ModifierEffect effect : this.temporaryModifiers) {
			tempStates.add(new GameSave.TemporaryModifierState(
				effect.getType(),
				effect.getValue(),
				effect.getRemainingRounds(),
				effect.isCrisis(),
				effect.started()
			));
		}
		
		return new GameSave.VillageModifierCtxState(persistentStates, tempStates);
    }

    /** stores modifiers in map */
    public void accumulatePersistentEffect(GameModifierType type, Object deltaVal) {
        if (!type.getType().isInstance(deltaVal)) {
            throw new IllegalArgumentException("deltaVal for modifier is not expected type: " + type.getName());
        }

        Object current = persistentModifiers.getOrDefault(type, type.getDefaultValue());
        Object newValue = type.combine(current, deltaVal);
        persistentModifiers.put(type, newValue);
    }
    
    /** stores each modifier separately in a list */
    public void accumulateTempEffect(ModifierEffect effect) {
    	temporaryModifiers.add(effect);
    }

    /** decrements all temporary effect durations and remove finished */
    public void tickRounds() {
        List<ModifierEffect> activeOnly = new ArrayList<>();
        for (ModifierEffect effect : temporaryModifiers) {
            boolean expired = effect.tick();
            if (!expired) {
                activeOnly.add(effect);
            }
            else {
            	Logger.LogDebug("finished temp modifier: %s", effect.getType().getName());
            }
        }
        temporaryModifiers.clear();
        temporaryModifiers.addAll(activeOnly);
    }

    /** removes all temp effects that were added by crises */
    public void clearCrisisEffects() {
        List<ModifierEffect> nonCrisisModifier = new ArrayList<>();
        for (ModifierEffect effect : temporaryModifiers) {
            if (!effect.isCrisis()) {
                nonCrisisModifier.add(effect);
            }
        }
        temporaryModifiers.clear();
        temporaryModifiers.addAll(nonCrisisModifier);
    }
    
    @SuppressWarnings("unchecked")
	public <T> T get(GameModifierType type) {
		Object total = type.getDefaultValue();
		
		// add persisiten modifiers
		if (persistentModifiers.containsKey(type)) {
			total = type.combine(total, persistentModifiers.get(type));
		}
		
		for (ModifierEffect effect : temporaryModifiers) {
			if (effect.getType() == type) {
				total = type.combine(total, effect.getValue());
			}
		}
		
		return (T)total;
		
    }

    public int getInt(GameModifierType type) {
        return get(type);
    }

    public double getDouble(GameModifierType type) {
        return get(type);
    }

    public boolean getBool(GameModifierType type) {
    	return get(type);
    }
}
