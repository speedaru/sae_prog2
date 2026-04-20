package fr.uge.but.schtroumpf.model;

import module java.base;

public enum ResourceType {
	BERRIES(1, "Baies", "Nourriture des Schtroumpfs"),
	SARSAPARILLA(2, "Salsepareille", "Plante magique (soins, potions)"),
	GOLD(3, "Or", "Richesse du village"),
	TOOLS(4, "Outils", "Équipement pour les tâches"),
	MORAL(5, "Moral", "Cohésion des Schtroumpfs"),
	DEFENSE(6, "Défense", "Protection contre les menaces"),
	KNOWLEDGE(7, "Savoir", "Connaissances du Grand Schtroumpf");
	
	private int code;
	private String displayName;
	private String description;
	
	ResourceType(int code, String displayName, String description) {
		this.code = code;
		this.displayName = Objects.requireNonNull(displayName);
		this.description = Objects.requireNonNull(description);
	}
	
	public int getCode() { return code; }
	public String getDisplayName() { return displayName; }
	public String getDescription() { return description; }
	
	@Override
	public String toString() {
		return displayName;
	}
}
