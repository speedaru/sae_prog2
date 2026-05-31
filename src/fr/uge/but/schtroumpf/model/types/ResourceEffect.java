package fr.uge.but.schtroumpf.model.types;

/**
 * @param resourceType the resource to modify
 * @param delta the amount to add/decrease
 */
public record ResourceEffect(ResourceType resourceType, int delta) {
	@Override
	public String toString() {
		String format = String.format("%d %s", delta, resourceType);
		
		return delta > 0 ? "+" + format : format;
	}
}
