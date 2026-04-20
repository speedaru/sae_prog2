package fr.uge.but.schtroumpf.model.crises;

import module java.base;

import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.model.SmurfVillage;

public enum CrisisType {
	FAMINE(1, ResourceType.BERRIES, "Famine", "moins d’actions aux prochains tours, ou moins de ressources aux prochains tours");
	
	private final int code;
	private final ResourceType cause;
	private final String name;
	private final String description;
	
	CrisisType(int code, ResourceType cause, String name, String description) {
		this.code = code;
		this.cause = Objects.requireNonNull(cause);
		this.name = Objects.requireNonNull(name);
		this.description = Objects.requireNonNull(description);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// add name + description
		sb.append(String.format("%s : %s", name, description)).append("\n");
		
		// add cause of crisis
		sb.append(String.format("Cause : manque de %s", cause));
		
		return sb.toString();
	}
	
	public int getCode() { return code; }
	public ResourceType getCause() { return cause; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	
	public boolean isActive(SmurfVillage village) {
		return village.getResourceQuantity(cause) == 0;
	}
	
	public static Crisis getCrisis(CrisisType type) {
		return switch (type) {
			case FAMINE -> new FamineCrisis();

			default -> throw new IllegalArgumentException("invalid type"); 
		};
	}
}
