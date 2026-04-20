package fr.uge.but.schtroumpf.model.events;

import module java.base;

public enum GameEventType {
//	GARGAMEL_ATTACK, 	// Attaque de Gargamel 
//	MAGIC_BERRIES,		// Découverte de baies magiques
//	FRIENDLY_VILLAGE,	// Visite d’un village ami

	SARSAPARILLA_STORM(4, 1, "Tempête de Salsepareille", "votre village subit une tempete de salsepareille");
//	SMURF_PARTY,		// Fête des Schtroumpfs
//	FOREST_CURSE;		// Malédiction de la forêt
	
	private final int code;
	private final int frequency;
	private final String title;
	private final String description;
	
	/** @param frequency value between 1 and 100 for how frequently the event should appear */
	GameEventType(int code, int frequency, String title, String description) {
		if (100 < frequency || frequency < 1) {
			throw new IllegalArgumentException("frequency must be between 1 and 100");
		}
		
		this.code = code;
		this.frequency = frequency;
		this.title = Objects.requireNonNull(title);
		this.description = Objects.requireNonNull(description);
	}
	
	public int getCode() { return code; }
	public int getFrequency() { return frequency; }
	public String getTitle() { return title; }
	public String getDescription() { return description; }
	
	public static GameEvent getEvent(GameEventType type) {
		return switch(type) {
			case SARSAPARILLA_STORM -> new SarsaparillaStorm();
			
			default -> throw new IllegalArgumentException("invalid type"); 
		};
	}
}
