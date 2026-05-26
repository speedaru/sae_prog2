package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.characters.*;

public class ForestCurse implements GameEvent {
	
	
	
	
	@Override public GameEventType getEventType() { return GameEventType.FOREST_CURSE; }

	@Override
	public List<ResourceEffect> trigger(SmurfVillage village) {
		List<ResourceEffect> impacts = new ArrayList<ResourceEffect>();
		
		// lose knowledge after a few turns passed
		// this isn't actually behaviour that I know how to add
		impacts.add(new ResourceEffect(ResourceType.KNOWLEDGE, -3));
		
		
		
		return impacts;
	}
}
