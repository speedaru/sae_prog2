package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.characters.*;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class FriendlyVillage implements GameEvent {
	
	
	
	
	@Override public GameEventType getEventType() { return GameEventType.FRIENDLY_VILLAGE; }

	@Override
	public List<ResourceEffect> trigger(SmurfVillage village) {
		List<ResourceEffect> impacts = new ArrayList<ResourceEffect>();
		
		// always gain gold
		impacts.add(new ResourceEffect(ResourceType.GOLD, 2));
		
		// the following code is locked as comments, as it is purely TEMPORARY 
		// speculation as to what an eventual conditional moral boost will look like
		//I can only hope this idea is good enough conceptually
		
		
		SmurfCharacter smurfetteInstance = village.getCouncilMember(SmurfType.SMURFETTE);
		if (smurfetteInstance != null && smurfetteInstance.getEnergy() > 3) {
			impacts.add(new ResourceEffect(ResourceType.MORAL, 2));
		}
			
		
		return impacts;
	}
}
