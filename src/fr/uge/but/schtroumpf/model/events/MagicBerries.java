package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.characters.*;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class MagicBerries implements GameEvent {
	
	
	
	
	@Override public GameEventType getEventType() { return GameEventType.MAGIC_BERRIES; }

	@Override
	public List<ResourceEffect> trigger(SmurfVillage village) {
		List<ResourceEffect> impacts = new ArrayList<ResourceEffect>();
		
		// always gain berries
		impacts.add(new ResourceEffect(ResourceType.BERRIES, 2));
		
		// always gain sarsaparilla
		impacts.add(new ResourceEffect(ResourceType.SARSAPARILLA, 2));
		
		return impacts;
	}
}
