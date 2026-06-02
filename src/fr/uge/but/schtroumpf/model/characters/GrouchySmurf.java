package fr.uge.but.schtroumpf.model.characters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class GrouchySmurf implements SmurfCharacter {
    private int energy = 10;
    private final Map<CharacterAttribute, Integer> attributes = new HashMap<>();

    public GrouchySmurf() {
        attributes.put(CharacterAttribute.WISDOM, 1);
    }

    @Override public SmurfType getType() { return SmurfType.GROUCHY_SMURF; }
    @Override public int getEnergy() { return energy; }
	@Override public void setEnergy(int value) { energy = value; }

    @Override
    public void updateEnergy(SmurfVillage village, int delta) {
        final int finalMaxEnergy = village.getDynamicMaxEnergy(this);
        energy = Math.clamp(energy + delta, 0, finalMaxEnergy);
    }

    @Override public int getAttribute(CharacterAttribute attrib) { return attributes.getOrDefault(attrib, 0); }
    @Override public void setAttribute(CharacterAttribute attrib, int value) { attributes.put(attrib, value); }
    @Override public void updateAttribute(CharacterAttribute attrib, int delta) { attributes.put(attrib, getAttribute(attrib) + delta); }

    @Override
    public String toString() { return getType().getName(); }

    @Override
    public List<CharacterAbility> getAbilities() {
        // watch surroundings
        CharacterAbility watchSurroundings = new CharacterAbility(
            "surveiller les alentours",
            "le schtroumpf grognon surveille le village pour augmenter la defense.",
            1,
            List.of(),
            List.of(
                new ResourceEffect(ResourceType.DEFENSE, 1)
            ),
            this::executeWatchSurroundings
        );

        // snitch lazy
        CharacterAbility snitchOnSlacker = new CharacterAbility(
            "denoncer un paresseux",
            "le schtroumpf grognon denonce un autre schtroumpf. ca enerve mais ca rapporte.",
            2,
            List.of(),
            List.of(
                new ResourceEffect(ResourceType.MORAL, -1),
                new ResourceEffect(ResourceType.GOLD, 1)
            ),
            this::executeSnitchOnSlacker
        );

        // prevent attack
        CharacterAbility preventAttack = new CharacterAbility(
            "prevenir une attaque",
            "le schtroumpf grognon rale tellement fort qu'il annule un evenement negatif.",
            3,
            List.of(
            	new ResourceSnapshot(ResourceType.DEFENSE, 4)
            ),
            List.of(
            	new ResourceEffect(ResourceType.DEFENSE, -3)
			),
            this::executePreventAttack
        );

        return List.of(
            watchSurroundings,
            snitchOnSlacker,
            preventAttack
        );
    }

    private AbilityResult executeWatchSurroundings(SmurfVillage village) {
        ResourceEffect plusDefense = new ResourceEffect(ResourceType.DEFENSE, 1);
        return new AbilityResult(
            AbilityResultType.NEUTRAL,
            "le schtroumpf grognon a ameliore la defense : " + plusDefense,
            List.of(plusDefense)
        );
    }

    private AbilityResult executeSnitchOnSlacker(SmurfVillage village) {
        ResourceEffect minusMoral = new ResourceEffect(ResourceType.MORAL, -1);
        ResourceEffect plusGold = new ResourceEffect(ResourceType.GOLD, 1);
        return new AbilityResult(
            AbilityResultType.NEUTRAL,
            "le schtroumpf grognon a balance quelqu'un " + minusMoral + plusGold,
            List.of(minusMoral, plusGold)
        );
    }

    private AbilityResult executePreventAttack(SmurfVillage village) {
    	AbilityResult result = new AbilityResult(
            AbilityResultType.NEUTRAL,
            "le schtroumpf grognon a prevenu la prochaine attaque !",
            List.of(new ResourceEffect(ResourceType.DEFENSE, -3))
        );
    	
    	village.accumulatePersistenModifier(GameModifierType.CRISIS_SHIELD_COUNT, 1);
    	
    	return result;
    }
}