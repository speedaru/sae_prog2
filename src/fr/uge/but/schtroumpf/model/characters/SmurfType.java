package fr.uge.but.schtroumpf.model.characters;

import java.util.List;
import java.util.Objects;

import fr.uge.but.schtroumpf.model.types.ResourceType;

public enum SmurfType {
	GRAND_SMURF(1, "Grand Schtroumpf", "gardien du village",
		List.of(ResourceType.KNOWLEDGE, ResourceType.MORAL, ResourceType.GOLD),
		"/sprites/grand_smurf.png"
	),
	HANDY_SMURF(2, "Schtroumpf Bricoleur", "artisan du village",
		List.of(ResourceType.TOOLS, ResourceType.DEFENSE),
		"/sprites/handy_smurf.png"
	), 
	SMURFETTE(3, "Schtroumpfette", "Support emotionnel du village",
		List.of(ResourceType.MORAL, ResourceType.BERRIES, ResourceType.SARSAPARILLA),
		"/sprites/smurfette.png"
	),
	GLUTTON_SMURF(4, "Schtroumpf Gourmand", "biblical levels of gluttony",
		List.of(ResourceType.BERRIES),
		"/sprites/glutton_smurf.png"
	),
	GROUCHY_SMURF(5, "Schtroumpf Grognon", "Maintient la discipline et anticipe les menaces",
		List.of(ResourceType.DEFENSE, ResourceType.GOLD),
		"/sprites/grouchy_smurf.png"
	),
	BRAINY_SMURF(6, "Schtroumpf Lunettes", "Intellectuel du village",
		List.of(ResourceType.KNOWLEDGE),
		"/sprites/brainy_smurf.png"
	);

	private final int code;
    private final String name;
    private final String roleDescription;
    private final List<ResourceType> associatedResources;
    private final String spritePath;
    
    SmurfType(int code, String name, String roleDescription, List<ResourceType> associatedResources, String spritePath) {
    	this.code = code;
    	this.name = Objects.requireNonNull(name);
    	this.roleDescription = Objects.requireNonNull(roleDescription);
    	this.associatedResources = Objects.requireNonNull(associatedResources);
    	this.spritePath = spritePath;
    }
    
    public int getCode() { return code; }
    public String getName() { return name; }
    public String getRoleDescription() { return roleDescription; }
    public List<ResourceType> getAssociatedResources() { return associatedResources; }
    public String getSpritePath() { return spritePath; }
}