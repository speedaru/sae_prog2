package fr.uge.but.schtroumpf.model.types;

/**
 * represents a change to the village resource
 * @param resourceType the resource to modify
 * @param delta the amount to add/decrease
 */
public record ResourceEffect(ResourceType resourceType, int delta) {
	@Override
	public String toString() {
		String format = String.format("%d %s", delta, resourceType);
		
		// if delta is positive then add a + in front 
		return delta > 0 ? "+" + format : format;
	}
}
