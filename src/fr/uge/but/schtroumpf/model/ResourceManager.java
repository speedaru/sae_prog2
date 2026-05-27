package fr.uge.but.schtroumpf.model;

import module java.base;

import fr.uge.but.schtroumpf.model.types.ResourceMap;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class ResourceManager {
	public record ResourceSnapshot(ResourceType type, int quantity) { }

	// hash map of resource type and quantity
	private ResourceMap resources = new ResourceMap();

	// constants
	public static final int MAX_QUANTITY = 10;
	
	public ResourceManager() { }
	
	// create using snapshot list
	public ResourceManager(List<ResourceSnapshot> snapshot) {
		for (ResourceSnapshot snap : snapshot) {
			resources.put(snap.type(), snap.quantity);
		}
	}

	// create using resource map
	public ResourceManager(ResourceMap resources) {
		// create copy of map
		this.resources = new ResourceMap(resources);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Resource Manager:\n");
		
		for (var entry : resources.entrySet()) {
			sb.append(String.format("%s: %d\n", entry.getKey(), entry.getValue()));
		}
		
		return sb.toString().stripTrailing();
	}

	public void set(ResourceType type, int quantity) { 
		// if resource doesn't exist create it with a quantity of 0
		if (!resources.containsKey(type)) {
			resources.put(type, 0);
		}
		
		// max out resource at MAX_QUANTITY
		int newQuantity = Math.clamp(quantity, 0, MAX_QUANTITY);
		resources.put(type, newQuantity);
	}
	
	/** add or decrease resources, to decrease just set quantity to a negative value */
	public void add(ResourceType type, int quantity) {
		set(type, resources.getOrDefault(type, 0) + quantity);
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
