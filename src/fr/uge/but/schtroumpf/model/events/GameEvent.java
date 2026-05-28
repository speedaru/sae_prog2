package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;

public interface GameEvent {
	GameEventType getEventType();
	
	/** @return a list of effects that should be applied to the village */
	List<ResourceEffect> trigger(SmurfVillage village);

	public static GameEvent fromType(GameEventType type) {
		return switch(type) {
			case SARSAPARILLA_STORM -> new SarsaparillaStorm();
			case GARGAMEL_ATTACK -> new GargamelAttack();
			case MAGIC_BERRIES -> new MagicBerries();
			case FRIENDLY_VILLAGE -> new FriendlyVillage();
			case SMURF_PARTY -> new SmurfParty();
			case FOREST_CURSE -> new ForestCurse();
			default -> throw new IllegalArgumentException("invalid type"); 
		};
	}
}


