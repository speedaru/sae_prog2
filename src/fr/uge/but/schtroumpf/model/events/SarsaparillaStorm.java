package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.characters.*;

public class SarsaparillaStorm implements GameEvent {
	// constants
	/** knowledge required to study storm */
	final int REQUIRED_KNOWLEDGE = 3;
	
	@Override public GameEventType getEventType() { return GameEventType.SARSAPARILLA_STORM; }

	@Override
	public List<Effect> trigger(SmurfVillage village) {
		List<Effect> impacts = new ArrayList<Effect>();
		
		// always lose tools
		impacts.add(new Effect(ResourceType.TOOLS, -1));
		
		// if knowledge is high then study storm
		if (village.getResourceQuantity(ResourceType.KNOWLEDGE) >= REQUIRED_KNOWLEDGE) {
			impacts.add(new Effect(ResourceType.KNOWLEDGE, 1));
		}
		
		return impacts;
	}
}
