package fr.uge.but.schtroumpf.model.events;

import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class Pillage implements GameEvent {
	/** if at least 3 defense then loose less tools */
	final int REQUIRED_DEFENSE = 3;
	
	@Override public GameEventType getEventType() { return GameEventType.PILLAGE; }

	@Override
	public List<ResourceEffect> trigger(SmurfVillage village) {
		List<ResourceEffect> impacts = new ArrayList<ResourceEffect>();
		
		int toolsToLose = -3;
		
		// lose less tools
		if (village.getResourceQuantity(ResourceType.DEFENSE) >= REQUIRED_DEFENSE) {
			toolsToLose++;
		}
		
		impacts.add(new ResourceEffect(ResourceType.TOOLS, toolsToLose));
		
		return impacts;
	}
}
