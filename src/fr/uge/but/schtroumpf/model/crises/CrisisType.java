package fr.uge.but.schtroumpf.model.crises;

import module java.base;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.types.ResourceMap;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public enum CrisisType {
	FAMINE(1, ResourceType.BERRIES, "Famine", "moins d’actions aux prochains tours, ou moins de ressources aux prochains tours"),
	EPIDEMIC(2, ResourceType.SARSAPARILLA, "Épidémie", "La salsepareille manque ! Les Schtroumpfs sont malades et leurs actions physiques sont deux fois moins efficaces."),
    REVOLT(3, ResourceType.MORAL, "Révolte", "Le moral est à zéro. Les Schtroumpfs contestent l'autorité ; les chances de succès des compétences sont réduites de 25%."),
    MASSIVE_ATTACK(4, ResourceType.DEFENSE, "Attaque Massive", "Les fortifications sont détruites ! Le village subit des pillages constants, drainant de l'Or chaque mois."),
    DARK_AGES(5, ResourceType.KNOWLEDGE, "Oubli des Recettes", "Le savoir est perdu. Les Schtroumpfs oubrient les techniques agricoles, bloquant la production passive de nourriture.");
	
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

	/** returns true if crisis cause resource is at 0 */
	public boolean shouldTrigger(SmurfVillage village) {
		return village.getResourceQuantity(this.cause) == 0;
	}

	/** returns true if crisis cause resource is at 0 */
	public boolean shouldTrigger(ResourceMap resources) {
		return resources.get(this.cause) == 0;
	}
}
