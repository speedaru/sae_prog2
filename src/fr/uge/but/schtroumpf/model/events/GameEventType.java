package fr.uge.but.schtroumpf.model.events;

import module java.base;

public enum GameEventType {
	GARGAMEL_ATTACK(1, 35, GargamelAttack::getFrequencyModifier, "Attaque de Gargamel", "votre village subit un assaut de Gargamel"),
	MAGIC_BERRIES(2, 15, GameEventType::noFreqModifier, "Découverte de baies magique", "des baies magiques se retrouvent dans le village"),
	FRIENDLY_VILLAGE(3, 15, GameEventType::noFreqModifier, "Visite d'un village ami", "un village ami de schtroumpfs visite votre village"),
	SARSAPARILLA_STORM(4, 25, GameEventType::noFreqModifier, "Tempête de Salsepareille", "votre village subit une tempete de salsepareille"),
	SMURF_PARTY(5, 20, GameEventType::noFreqModifier, "Fête des Schtroumpfs", "votre village fait la fête"),
	FOREST_CURSE(6, 30, GameEventType::noFreqModifier, "Malédiction de la forêt", "votre village subit une malédiction venant des bois" );

	private final int code;
	private final int frequency;
	private final Function<Integer, Integer> getFreqModifier;
	private final String title;
	private final String description;
	
	/** @param frequency value for how frequently the event should appear */
	GameEventType(int code, int frequency, Function<Integer, Integer> getFreqModifier, String title, String description) {
		if (frequency < 1) {
			throw new IllegalArgumentException("frequency must be between at least 1");
		}
		
		this.code = code;
		this.frequency = frequency;
		this.getFreqModifier = getFreqModifier;
		this.title = Objects.requireNonNull(title);
		this.description = Objects.requireNonNull(description);
	}
	
	public int getCode() { return code; }
	public int getBaseFrequency() { return frequency; }
	public String getTitle() { return title; }
	public String getDescription() { return description; }

	public int calcFrequency(int currentRound) {
		return frequency + getFreqModifier.apply(currentRound);
	}
	
	private static int noFreqModifier(int currentRound) { return 0; }
}
