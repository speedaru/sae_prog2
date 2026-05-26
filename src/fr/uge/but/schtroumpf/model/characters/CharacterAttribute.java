package fr.uge.but.schtroumpf.model.characters;

import java.util.Objects;

public enum CharacterAttribute {
	WISDOM(1, "Sagesse"),
	KINDNESS(2, "Gentillesse");
	
	private final int code;
    private final String name;
    
    CharacterAttribute(int code, String name) {
    	this.code = code;
    	this.name = Objects.requireNonNull(name);
    }
    
    public int getCode() { return code; }
    public String getName() { return name; }
}
