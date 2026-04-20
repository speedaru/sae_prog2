package fr.uge.but.schtroumpf.model.characters;

import fr.uge.but.schtroumpf.model.ResourceType;

/**
 * represents a change to the village resource
 * @param resourceType the resource to modify
 * @param delta the amount to add/decrease
 */
public record Effect(ResourceType resourceType, int delta) {
	@Override
	public String toString() {
		String format = String.format("%d %s", delta, resourceType);
		
		// if delta is positive then add a + in front 
		return delta > 0 ? "+" + format : format;
	}
}
