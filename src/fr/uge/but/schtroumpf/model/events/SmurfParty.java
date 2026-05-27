package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.characters.*;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class SmurfParty implements GameEvent {
	//constant
	final int REQUIRED_BERRIES = 2;
	
	
	@Override public GameEventType getEventType() { return GameEventType.SMURF_PARTY; }

	@Override
	public List<ResourceEffect> trigger(SmurfVillage village) {
		List<ResourceEffect> impacts = new ArrayList<ResourceEffect>();
		
		// always lose berries
		impacts.add(new ResourceEffect(ResourceType.BERRIES, -2));
		
		// if we don't have berries, we don't gain moral
		if (village.getResourceQuantity(ResourceType.BERRIES) >= REQUIRED_BERRIES) {
			impacts.add(new ResourceEffect(ResourceType.MORAL, 3));
		}
		
		return impacts;
	}
}
