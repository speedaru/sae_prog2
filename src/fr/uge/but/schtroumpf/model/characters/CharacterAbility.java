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
    Predicate<SmurfVillage> canExecute,
    Function<SmurfVillage, List<Effect>> actionLogic
)
{
	public static boolean canAlwaysExecute(SmurfVillage village) {
		return true;
	}
}
