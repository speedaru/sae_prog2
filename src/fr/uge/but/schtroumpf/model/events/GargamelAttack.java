package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.characters.*;

public class GargamelAttack implements GameEvent {
	
	
	
	
	@Override public GameEventType getEventType() { return GameEventType.GARGAMEL_ATTACK; }

	@Override
	public List<ResourceEffect> trigger(SmurfVillage village) {
		List<ResourceEffect> impacts = new ArrayList<ResourceEffect>();
		
		// always lose defense
		impacts.add(new ResourceEffect(ResourceType.DEFENSE, -1));
		
		// always lose moral
		impacts.add(new ResourceEffect(ResourceType.MORAL, -2));
		
		return impacts;
	}
}
