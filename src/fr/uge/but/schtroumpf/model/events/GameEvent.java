package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.Effect;

public interface GameEvent {
	GameEventType getEventType();

	/** @return a list of effects that should be applied to the village */
	List<Effect> trigger(SmurfVillage village);
}
