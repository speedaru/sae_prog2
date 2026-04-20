package fr.uge.but.schtroumpf.model.characters;

import module java.base;

public interface SmurfCharacter {
	String getName();
	int getEnergy();
	void updateEnergy(int delta);
	List<CharacterAbility> getAbilities();
	
	default boolean canExecute(CharacterAbility ability) {
		return getEnergy() >= ability.energyCost();
	}
	
	default List<CharacterAbility> getAvailableAbilities() {
		List<CharacterAbility> abilities = getAbilities();
		ArrayList<CharacterAbility> availableAbilities = new ArrayList<CharacterAbility>();
		
		for (CharacterAbility ability : abilities) {
			if (canExecute(ability)) {
				availableAbilities.add(ability);
			}
		}
		
		return List.copyOf(availableAbilities);
	}
}
