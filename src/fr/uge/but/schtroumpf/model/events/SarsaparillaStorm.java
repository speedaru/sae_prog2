package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class SarsaparillaStorm implements GameEvent {
	// constants

	/** knowledge required to study storm */
	final int REQUIRED_KNOWLEDGE = 3;
	
	@Override public GameEventType getEventType() { return GameEventType.SARSAPARILLA_STORM; }

	@Override
	public List<ResourceEffect> trigger(SmurfVillage village) {
		List<ResourceEffect> impacts = new ArrayList<ResourceEffect>();
		
		// always lose tools
		impacts.add(new ResourceEffect(ResourceType.SARSAPARILLA, -3));
		
		// if knowledge is high then study storm
		if (village.getResourceQuantity(ResourceType.KNOWLEDGE) >= REQUIRED_KNOWLEDGE) {
			impacts.add(new ResourceEffect(ResourceType.KNOWLEDGE, 1));
		}
		
		return impacts;
	}
}
