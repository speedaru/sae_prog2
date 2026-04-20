package fr.uge.but.schtroumpf.model;

import module java.base;

import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.*;
import fr.uge.but.schtroumpf.model.events.*;

public class SmurfVillage {
	private final ResourceManager resourceManager = new ResourceManager();
	private List<ResourceSnapshot> previousRoundResources;
	private List<SmurfCharacter> councilMembers;
	private ArrayList<EventHistory> eventsHistory = new ArrayList<EventHistory>();
	
	public SmurfVillage() {
		// set arbitrary quantities
		resourceManager.add(ResourceType.BERRIES, 9);
		resourceManager.add(ResourceType.GOLD, 7);
		
		councilMembers = createSmurfs();
	}
	
	private static List<SmurfCharacter> createSmurfs() {
		GrandSmurf grandSmurf = new GrandSmurf();
		
		return List.of(grandSmurf);
	}
	
	public List<ResourceSnapshot> getResources() {
		return resourceManager.getResourcesSnap();
	}

	/** get a list of resources snapshot of by how much each resource increased/decreased */
	public List<ResourceSnapshot> getResourcesDiff() {
		var diffSnap = new ArrayList<ResourceSnapshot>();
		var currentSnap = resourceManager.getResourcesSnap();
		
		// previous snap null
		if (previousRoundResources == null) {
			return currentSnap;
		}
		
		for (int i = 0 ; i < previousRoundResources.size(); i++) {
			ResourceSnapshot previousResourceSnap = previousRoundResources.get(i);
			ResourceSnapshot currentResourceSnap = currentSnap.get(i);
			
			// same index should have same resource type
			if (previousResourceSnap.resource() != currentResourceSnap.resource()) {
				throw new IllegalStateException("both snapshots should have same resource at same index");
			}
			
			ResourceType type = currentResourceSnap.resource();
			int delta = currentResourceSnap.quantity() - previousResourceSnap.quantity();
			diffSnap.add(new ResourceSnapshot(type, delta));
		}
		
		return List.copyOf(diffSnap);
	}

	public List<SmurfCharacter> getCouncilMembers() {
		return List.copyOf(councilMembers);
	}
	
	public List<EventHistory> getHistory() {
		return List.copyOf(eventsHistory);
	}
	
	public int getResourceQuantity(ResourceType resourceType) {
		return resourceManager.get(resourceType);
	}

	public EventHistory getEventFromRound(int round) {
		for (var eventLog : eventsHistory) {
			if (eventLog.round() == round) {
				return eventLog;
			}
		}
		
		return null;
	}
	
	/** @return number of resources that are at 0 */
	public int checkCrises() {
		int depletedResourceCount = 0;
		
		for (var resource : resourceManager.getResourcesSnap()) {
			if (resource.quantity() == 0) {
				depletedResourceCount += 1;
			}
		}

		return depletedResourceCount;
	}
	
	public void saveRoundResources() {
		previousRoundResources = resourceManager.getResourcesSnap();
	}
	
	public void applyResourceConsumption() {
		// TODO : implement
		resourceManager.add(ResourceType.BERRIES, -2);
	}
	
	public void recordEvent(EventHistory recordedEvent) {
		eventsHistory.add(recordedEvent);
	}
	
	public void applyEffect(Effect effect) {
		resourceManager.add(effect.resourceType(), effect.delta());
	}
}
