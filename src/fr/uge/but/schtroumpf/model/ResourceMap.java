package fr.uge.but.schtroumpf.model;

import module java.base;

/** associate resource type with quantity */
public class ResourceMap extends EnumMap<ResourceType, Integer> {
	/** to suppress java warning */
	private static final long serialVersionUID = 8376766436241533979L;

	public ResourceMap() {
		super(ResourceType.class);
	}

	public ResourceMap(ResourceMap other) {
		super(other);
	}
}
