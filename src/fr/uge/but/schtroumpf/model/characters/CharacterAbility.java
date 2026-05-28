package fr.uge.but.schtroumpf.model.characters;

import module java.base;

import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.SmurfVillage;

/**
 * represents an ability that can be executed by a smurf character
 *
 * @param name display name
 * @param description ability description (can also contain info like "-1 moral on failure")
 * @param energyCost energy cost to use the ability
 * @param requiredResources amount of required resources by the village to use the ability
 * @param primaryEffects primary effects that the ability has on the village
 * @param actionLogic callback to execute the logic of the ability, returns a list of effects that should applied
 */
public record CharacterAbility(
    String name,
    String description,
    int energyCost,
    List<ResourceSnapshot> requiredResources,
	List<ResourceEffect> primaryEffects, // for UI, not logic
    Function<SmurfVillage, AbilityResult> actionLogic
)
{
	/**
	 * Structural categories to drive frontend presentation layer styles.
	 */
	public enum AbilityResultType {
	    SUCCESS,
	    FAILURE,
	    NEUTRAL
	}

	/**
	 * Rich outcome payload returned directly by an action execution.
	 */
	public record AbilityResult(
	    AbilityResultType type,
	    String message,
	    List<ResourceEffect> effectsToApply
	) {}
	
	@Override
	public String toString() {
		return String.format("%s (%d ⚡)", description, energyCost);
	}
}
