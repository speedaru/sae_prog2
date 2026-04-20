package fr.uge.but.schtroumpf.model.crises;

import module java.base;

import fr.uge.but.schtroumpf.model.ResourceType;

public enum CrisisType {
	FAMINE(1, ResourceType.BERRIES, "moins d’actions aux prochains tours, ou moins de ressources aux prochains tours");
	
	private final int code;
	private final ResourceType cause;
	private final String description;
	
	CrisisType(int code, ResourceType cause, String description) {
		this.code = code;
		this.cause = Objects.requireNonNull(cause);
		this.description = Objects.requireNonNull(description);
	}
	
	public int getCode() { return code; }
	public ResourceType getCause() { return cause; }
	public String getDescription() { return description; }
}
