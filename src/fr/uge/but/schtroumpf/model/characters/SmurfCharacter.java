package fr.uge.but.schtroumpf.model.characters;

import module java.base;

public interface SmurfCharacter {
	String getName();
	int getEnergy();
	void updateEnergy(int delta);
	List<CharacterAbility> getAbilities();
}
