package fr.uge.but.schtroumpf.model;

import module java.base;

public class ResourceManager {
	/** a resource type paired with its quantity */
	public record ResourceSnapshot(ResourceType resource, int quantity) { }

	// hash map of resource type and quantity
	private final HashMap<ResourceType, Integer> resources = new HashMap<ResourceType, Integer>();

	// constants
	public static final int MAX_QUANTITY = 10;
	
	/** add or decrease resources, to decrease just set quantity to a negative value */
	public void add(ResourceType type, int quantity) {
		// if resource doesn't exist create it with a quantity of 0
		if (!resources.containsKey(type)) {
			resources.put(type, 0);
		}
		
		// max out resource at MAX_QUANTITY
		int newQuantity = Math.clamp(resources.get(type) + quantity, 0, MAX_QUANTITY);
		resources.put(type, newQuantity);
	}
	
	/** get the quantity of a resource */
	public int get(ResourceType type) {
		return resources.getOrDefault(type, 0);
	}
	
	/** @return a list of resource snapshots */
	public List<ResourceSnapshot> getResourcesSnap() {
		var snap = new ArrayList<ResourceSnapshot>();

		for (ResourceType type : ResourceType.values()) {
			snap.add(new ResourceSnapshot(type, get(type)));
		}
		
		return List.copyOf(snap);
	}
}
