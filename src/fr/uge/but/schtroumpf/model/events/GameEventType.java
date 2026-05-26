package fr.uge.but.schtroumpf.model.events;

import module java.base;

public enum GameEventType {
//	GARGAMEL_ATTACK, 	// Attaque de Gargamel
	GARGAMEL_ATTACK(1, 35, "Attaque de Gargamel", "votre village subit un assaut de Gargamel"),
//	MAGIC_BERRIES,		// Découverte de baies magiques
	MAGIC_BERRIES(2, 15, "Découverte de baies magique", "des baies magiques se retrouvent dans le village"),
//	FRIENDLY_VILLAGE,	// Visite d’un village ami
	FRIENDLY_VILLAGE(3, 15, "Visite d'un village ami", "un village ami de schtroumpfs visite votre village"),
	
	SARSAPARILLA_STORM(4, 25, "Tempête de Salsepareille", "votre village subit une tempete de salsepareille"),
//	SMURF_PARTY,		// Fête des Schtroumpfs
	SMURF_PARTY(5, 20, "Fête des Schtroumpfs", "votre village fait la fête"),
//	FOREST_CURSE;		// Malédiction de la forêt
	FOREST_CURSE(6, 30, "Malédiction de la forêt", "votre village subit une malédiction venant des bois" );
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
			case GARGAMEL_ATTACK -> new GargamelAttack();
			case MAGIC_BERRIES -> new MagicBerries();
			case FRIENDLY_VILLAGE -> new FriendlyVillage();
			case SMURF_PARTY -> new SmurfParty();
			case FOREST_CURSE -> new ForestCurse();
			default -> throw new IllegalArgumentException("invalid type"); 
		};
	}
}
