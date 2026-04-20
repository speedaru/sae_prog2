package fr.uge.but.schtroumpf.model.characters;

import module java.base;

import fr.uge.but.schtroumpf.model.SmurfVillage;

/**
 * @param description display text for the ability
 * @param canExecute condition to check if the action is available
 * @param actionLogic the code that runs when selected that returns a list of effects
 */
public record CharacterAbility(
    String description,
    int energyCost,
    Function<SmurfVillage, List<Effect>> actionLogic
)
{
	public static boolean canAlwaysExecute(SmurfCharacter village) {
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%d energie)", description, energyCost);
	}
}
