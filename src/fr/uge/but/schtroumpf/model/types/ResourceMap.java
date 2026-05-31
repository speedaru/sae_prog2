package fr.uge.but.schtroumpf.model.types;

import module java.base;

import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;

/** associate resource type with quantity */
public class ResourceMap extends EnumMap<ResourceType, Integer> {
	private static final long serialVersionUID = 8376766436241533979L; // ide warning

	public ResourceMap() {
		super(ResourceType.class);
	}

	public ResourceMap(ResourceMap other) {
		super(other);
	}
	
	public List<ResourceSnapshot> getSnapshot() {
		ArrayList<ResourceSnapshot> snaps = new ArrayList<>();
		
		for (var entry : entrySet()) {
			snaps.add(new ResourceSnapshot(entry.getKey(), entry.getValue()));
		}
		
		return List.copyOf(snaps);
	}
}
