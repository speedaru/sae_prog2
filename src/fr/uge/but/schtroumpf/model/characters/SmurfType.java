package fr.uge.but.schtroumpf.model.characters;

import java.nio.file.Path;
import java.util.Objects;

public enum SmurfType {
	GRAND_SMURF(1, "Grand Schtroumpf", "gardien du village", Path.of("src/main/resources/sprites/grand_smurf.png")),
	OLD_SMURF(2, "Vieux Schtroumpf", "vieux con", null),
	SMURFETTE(3, "Schtroumpfette", "Support emotionnel du village", Path.of("src/main/resources/sprites/smurfette.png")),
	HANDY_SMURF(4, "Schtroumpf Bricoleur", "artisan du village", Path.of("src/main/resources/sprites/handy_smurf.png")), 
	GLUTTON_SMURF(5, "Schtroumpf Gourmand", "biblical levels of gluttony", Path.of("src/main/resources/sprites/glutton_smurf.png")),
	GROUCHY_SMURF(6, "Schtroumpf Grognon", "Maintient la discipline et anticipe les menaces", Path.of("src/main/resources/sprites/grouchy_smurf.png")),
	BRAINY_SMURF(7, "Schtroumpf Lunettes", "Intellectuel du village", Path.of("src/main/resources/sprites/brainy_smurf.png"));

	private final int code;
    private final String name;
    private final String roleDescription;
    private final Path spritePath;
    
    SmurfType(int code, String name, String roleDescription, Path spritePath) {
    	this.code = code;
    	this.name = Objects.requireNonNull(name);
    	this.roleDescription = Objects.requireNonNull(roleDescription);
    	this.spritePath = spritePath;
    }
    
    public int getCode() { return code; }
    public String getName() { return name; }
    public String getRoleDescription() { return roleDescription; }
    public Path getSpritePath() { return spritePath; }
}