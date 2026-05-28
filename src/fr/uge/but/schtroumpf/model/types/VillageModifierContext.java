package fr.uge.but.schtroumpf.model.types;

import java.util.EnumMap;
import java.util.Map;

import fr.uge.but.schtroumpf.model.save.GameSave;

public class VillageModifierContext {
    private final Map<GameModifierType, Object> modifiers = new EnumMap<>(GameModifierType.class);
    
    public VillageModifierContext() {
        // initialize all modifiers to defaults
        for (GameModifierType modifier : GameModifierType.values()) {
            modifiers.put(modifier, modifier.getDefaultValue());
        }
    }
    
    public VillageModifierContext(GameSave.VillageModifierCtxState state) {
    	// init all modifiers to default
        this();

        for (var entry : state.modifiers().entrySet()) {
        	GameModifierType type = entry.getKey();
        	Object val = entry.getValue();
        	
        	// modifier unset
        	if (val == null) {
        		continue;
        	}
        	
        	// handle doubles deserializing manually
        	if (type.getType() == Double.class) {
        		Number num = (Number)val;
        		this.set(type, num.doubleValue());
        	}
        	else if (type.getType() == Integer.class) {
        		Number num = (Number)val;
        		this.set(type, num.intValue());
        	}
        	else {
        		this.set(type, val);
        	}
        }
    }
    
    /** any type getter */
    @SuppressWarnings("unchecked")
    public <T> T get(GameModifierType modifier) {
        Object value = modifiers.getOrDefault(modifier, modifier.getDefaultValue());
        return (T)modifier.getType().cast(value);
    }

    // ------------------------- types getters
    
    public double getDouble(GameModifierType modifier) {
        return get(modifier);
    }

    public int getInt(GameModifierType modifier) {
        return get(modifier);
    }

    public boolean getBoolean(GameModifierType modifier) {
        return get(modifier);
    }

    /** updates modifier value */
    public void set(GameModifierType modifier, Object value) {
//        if (!modifier.getType().isInstance(value)) {
//            throw new IllegalArgumentException(String.format("cant set modifier %s to type %s. expected type: %s",
//                modifier.name(),
//                value.getClass().getSimpleName(),
//                modifier.getType().getSimpleName()
//            ));
//        }
        modifiers.put(modifier, value);
    }

    public void setBool(GameModifierType modifier, boolean value) {
        set(modifier, value);
    }

    public void addInt(GameModifierType modifier, int delta) {
        set(modifier, getInt(modifier) + delta);
    }

    public void addDouble(GameModifierType modifier, double delta) {
        set(modifier, getDouble(modifier) + delta);
    }

    /** copy of modifiers map */
    public Map<GameModifierType, Object> getModifiers() {
        return Map.copyOf(modifiers);
    }
}
