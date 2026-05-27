package fr.uge.but.schtroumpf.model.characters;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class HandySmurf implements SmurfCharacter {
	private int energy = 10;
	private final Map<CharacterAttribute, Integer> attributes = new HashMap<>();

	@Override public SmurfType getType() { return SmurfType.HANDY_SMURF; }
	@Override public int getEnergy() { return energy; }
	@Override public void setEnergy(int value) { energy = value; }

	@Override
	public void updateEnergy(SmurfVillage village, int delta) {
		final int finalMaxEnergy = village.getDynamicMaxEnergy(this);
		energy = Math.clamp(energy + delta, 0, finalMaxEnergy);
	}

	@Override
	public int getAttribute(CharacterAttribute attrib) {
		return attributes.getOrDefault(attrib, 0);
	}

	@Override
	public void updateAttribute(CharacterAttribute attrib, int delta) {
		attributes.put(attrib, getAttribute(attrib) + delta);
	}

	@Override
	public String toString() {
		return getType().getName();
	}

	@Override
	public List<CharacterAbility> getAbilities() {

		CharacterAbility repairHouses = new CharacterAbility("Réparer les maisons",
				"Le Schtroumpf Bricoleur utilise de la salsepareille pour fabriquer des outils.", 2,
				List.of(new ResourceSnapshot(ResourceType.SARSAPARILLA, 1)),
				List.of(new ResourceEffect(ResourceType.TOOLS, 1), new ResourceEffect(ResourceType.SARSAPARILLA, -1)),
				this::executeRepairHouses);

		CharacterAbility buildTrap = new CharacterAbility("Fabriquer un piège",
				"Le Schtroumpf Bricoleur utilise des outils pour renforcer les défenses du village.", 2,
				List.of(new ResourceSnapshot(ResourceType.TOOLS, 1)),
				List.of(new ResourceEffect(ResourceType.DEFENSE, 1), new ResourceEffect(ResourceType.TOOLS, -1)),
				this::executeBuildTrap);

		CharacterAbility inventGadget = new CharacterAbility("Inventer un gadget",
				"Le Schtroumpf Bricoleur tente une invention imprévisible.", 3, List.of(), List.of(),
				this::executeInventGadget);

		return List.of(repairHouses, buildTrap, inventGadget);
	}

	private AbilityResult executeRepairHouses(SmurfVillage village) {
		Logger.LogDebug("Handy Smurf repaired houses");

		ResourceEffect plusTools = new ResourceEffect(ResourceType.TOOLS, 1);
		ResourceEffect minusSals = new ResourceEffect(ResourceType.SARSAPARILLA, -1);

		return new AbilityResult(AbilityResultType.SUCCESS,
				"Le Schtroumpf Bricoleur a réparé les maisons : " + plusTools + ", " + minusSals,
				List.of(plusTools, minusSals));
	}

	private AbilityResult executeBuildTrap(SmurfVillage village) {
		Logger.LogDebug("Handy Smurf built a trap");

		ResourceEffect plusDefense = new ResourceEffect(ResourceType.DEFENSE, 1);
		ResourceEffect minusTools = new ResourceEffect(ResourceType.TOOLS, -1);

		return new AbilityResult(AbilityResultType.SUCCESS,
				"Le Schtroumpf Bricoleur a fabriqué un piège : " + plusDefense + ", " + minusTools,
				List.of(plusDefense, minusTools));
	}

	private AbilityResult executeInventGadget(SmurfVillage village) {
		Logger.LogDebug("Handy Smurf invented a gadget");

		final double SUCCESS_CHANCE = 0.3;
		final double NEUTRAL_CHANCE = 0.5;

		if (village.rollChance(SUCCESS_CHANCE)) {
			ResourceEffect plusTools = new ResourceEffect(ResourceType.TOOLS, 2);

			return new AbilityResult(AbilityResultType.SUCCESS, "Succès ! Le gadget améliore les outils : " + plusTools,
					List.of(plusTools));
		} else if (village.rollChance(NEUTRAL_CHANCE)) {
			ResourceEffect plusMoral = new ResourceEffect(ResourceType.MORAL, 1);

			return new AbilityResult(AbilityResultType.NEUTRAL, "Le gadget amuse le village : " + plusMoral,
					List.of(plusMoral));
		} else {
			ResourceEffect minusTools = new ResourceEffect(ResourceType.TOOLS, -1);
			return new AbilityResult(AbilityResultType.FAILURE, "Échec ! Le gadget explose : " + minusTools,
					List.of(minusTools));
		}
	}
}