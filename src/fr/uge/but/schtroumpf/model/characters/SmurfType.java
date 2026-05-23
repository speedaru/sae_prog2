package fr.uge.but.schtroumpf.model.characters;

import java.nio.file.Path;
import java.util.Objects;

public enum SmurfType {
	GRAND_SMURF(1, "Grand Schtroumpf", "gardien du village", null),
	OLD_SMURF(2, "Vieux Schtroumpf", "vieux con", null),
	SMURFETTE(3, "Schtroumpfette", "tana 92i", null);

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
