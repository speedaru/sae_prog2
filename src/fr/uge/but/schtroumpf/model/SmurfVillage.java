package fr.uge.but.schtroumpf.model;

import module java.base;

import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.*;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.events.*;

public class SmurfVillage {
	private final static int CRISES_LIMIT = 3; // 3+ crises = lose
	
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
			if (previousResourceSnap.type() != currentResourceSnap.type()) {
				throw new IllegalStateException("both snapshots should have same resource at same index");
			}
			
			ResourceType type = currentResourceSnap.type();
			int delta = currentResourceSnap.quantity() - previousResourceSnap.quantity();
			diffSnap.add(new ResourceSnapshot(type, delta));
		}
		
		return List.copyOf(diffSnap);
	}

	public List<SmurfCharacter> getCouncilMembers() {
		return List.copyOf(councilMembers);
	}
	
	public List<SmurfCharacter> getAvailableSmurfs() {
		ArrayList<SmurfCharacter> available = new ArrayList<SmurfCharacter>();
		
		for (SmurfCharacter smurf : councilMembers) {
			// smurf is available if has at least 1 energy
			if (smurf.getEnergy() >= 1) {
				available.add(smurf);
			}
		}
		
		return List.copyOf(available);
	}
	
	public SmurfCharacter getCouncilMember(SmurfType type) {
		for (SmurfCharacter smurf : councilMembers) {
			if (smurf.getType() == type) {
				return smurf;
			}
		}
		throw new IllegalStateException(String.format("%s is not a part of the smurf council", type));
	}
	
	public List<CharacterAbility> getAbilitiesFor(SmurfType type) {
		for (SmurfCharacter smurf : councilMembers) {
			if (smurf.getType() == type) {
				return smurf.getAbilities();
			}
		}
		throw new IllegalStateException(String.format("%s is not a part of the smurf council", type));
	}

//	public List<CharacterAbility> getAvailableAbilitiesFor(SmurfType type) {
//		for (SmurfCharacter smurf : councilMembers) {
//			if (smurf.getType() == type) {
//				return smurf.getAvailableAbilities();
//			}
//		}
//		throw new IllegalStateException(String.format("%s is not a part of the smurf council", type));
//	}
	
	public List<EventHistory> getHistory() {
		return List.copyOf(eventsHistory);
	}
	
	public int getResourceQuantity(ResourceType resourceType) {
		return resourceManager.get(resourceType);
	}
	
	public int getResourceDelta(ResourceType resourceType) {
		int previousRoundQuanity = 0;
		for (ResourceSnapshot snap : previousRoundResources) {
			if (snap.type() == resourceType) {
				previousRoundQuanity = snap.quantity();
				break;
			}
		}

		return resourceManager.get(resourceType) - previousRoundQuanity;
	}

	public final EventHistory getEventFromRound(int round) {
		for (var eventLog : eventsHistory) {
			if (eventLog.round() == round) {
				return eventLog;
			}
		}
		
		return null;
	}
	
	public final EventHistory getLastEvent() {
		return eventsHistory.getLast();
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
	
	/** checks if the village has enough of each resource specified */
	public boolean verifyResources(List<ResourceSnapshot> resourcesRequired) {
		for (ResourceSnapshot req : resourcesRequired) {
			// not enough of required resource
			if (resourceManager.get(req.type()) < req.quantity()) {
				return false;
			}
		}
		return true;
	}
	
	/** 3 or more crises means lost */
	public boolean isDefeated() {
		return checkCrises() >= CRISES_LIMIT;
	}
	
	public void saveRoundResources() {
		previousRoundResources = resourceManager.getResourcesSnap();
	}
	
	public void recordEvent(EventHistory recordedEvent) {
		eventsHistory.add(recordedEvent);
	}
	
	public void applyEffect(Effect effect) {
		resourceManager.add(effect.resourceType(), effect.delta());
	}

	public void applyEffects(List<Effect> effects) {
		for (Effect effect : effects) {
			resourceManager.add(effect.resourceType(), effect.delta());
		}
	}
	
	/**
	 * @param type council member smurf type
	 */
	public AbilityResult executeCouncilMemberAbility(SmurfCharacter smurf, CharacterAbility ability) {
        if (!smurf.canExecute(this, ability)) {
            throw new IllegalStateException("Action processing denied: Preconditions or energy thresholds unmet.");
        }

        // consume energy
        smurf.updateEnergy(-ability.energyCost());

        // calculate logic and apply effects
        AbilityResult result = ability.actionLogic().apply(this);
        this.applyEffects(result.effectsToApply());

        // return result so controller can display result in feedback label
        return result;
	}
	
	public void increaseResource(ResourceType resource, int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("cannot decrease negative amount");
		}

		resourceManager.add(resource, amount);
	}
	
	public void decreaseResource(ResourceType resource, int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("cannot decrease by a negative amount");
		}
		
		resourceManager.add(resource, -amount);
	}
	
	// ------------------------- private helpers
	
	private static List<SmurfCharacter> createSmurfs() {
		GrandSmurf grandSmurf = new GrandSmurf();
		
		return List.of(grandSmurf);
	}
}
