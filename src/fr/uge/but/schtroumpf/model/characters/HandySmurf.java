package fr.uge.but.schtroumpf.model.characters;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.*;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.model.utils.OutcomeChoice;
import fr.uge.but.schtroumpf.model.utils.WeightedOutcomeSelector;

public class HandySmurf implements SmurfCharacter {
	private int energy = 10;
	private final Map<CharacterAttribute, Integer> attributes = new HashMap<>();

	public HandySmurf() {
        attributes.put(CharacterAttribute.BUILDING, 0);
	}
	
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
    public void setAttribute(CharacterAttribute attrib, int value) {
    	attributes.put(attrib, value);
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
		CharacterAbility repairHouses = new CharacterAbility(
			"Réparer les maisons",
			"Le Schtroumpf Bricoleur utilise de la salsepareille pour fabriquer des outils. Augmente ses capacites de construction.",
			2,
			List.of(new ResourceSnapshot(ResourceType.SARSAPARILLA, 1)),
			List.of(
				new PossibleBranch("Effets :", List.of(
					new ResourceEffect(ResourceType.TOOLS, 1),
					new ResourceEffect(ResourceType.SARSAPARILLA, -1)
				))
			),
			this::executeRepairHouses
		);

		CharacterAbility buildTrap = new CharacterAbility(
			"Fabriquer un piège",
			"Le Schtroumpf Bricoleur utilise des outils pour renforcer les défenses du village. Augmente ses capacites de construction.",
			2,
			List.of(new ResourceSnapshot(ResourceType.TOOLS, 1)),
			List.of(
				new PossibleBranch("Effets :", List.of(
					new ResourceEffect(ResourceType.DEFENSE, 1),
					new ResourceEffect(ResourceType.TOOLS, -1))
				)),
			this::executeBuildTrap
		);

		CharacterAbility inventGadget = new CharacterAbility(
			"Inventer un gadget",
			"Le Schtroumpf Bricoleur tente une invention imprévisible. Augmente ses capacites de construction s'il reussie.",
			3,
			List.of(new ResourceSnapshot(ResourceType.TOOLS, 1)),
			List.of(
				new PossibleBranch("Succès :", List.of(
					new ResourceEffect(ResourceType.TOOLS, 2)
				)),
				new PossibleBranch("Neutre :", List.of(
					new ResourceEffect(ResourceType.MORAL, 1)
				)),
				new PossibleBranch("Echec :", List.of(
					new ResourceEffect(ResourceType.TOOLS, -1)
				))
			),
			this::executeInventGadget
		);

		return List.of(repairHouses, buildTrap, inventGadget);
	}

	private AbilityResult executeRepairHouses(SmurfVillage village) {
		Logger.LogDebug("Handy Smurf repaired houses");

		ResourceEffect plusTools = new ResourceEffect(ResourceType.TOOLS, 1);
		ResourceEffect minusSals = new ResourceEffect(ResourceType.SARSAPARILLA, -1);

		updateAttribute(CharacterAttribute.BUILDING, 1);
		return new AbilityResult(AbilityResultType.SUCCESS,
				"Le Schtroumpf Bricoleur a réparé les maisons : " + plusTools + ", " + minusSals,
				List.of(plusTools, minusSals));
	}

	private AbilityResult executeBuildTrap(SmurfVillage village) {
		Logger.LogDebug("Handy Smurf built a trap");

		ResourceEffect plusDefense = new ResourceEffect(ResourceType.DEFENSE, 1);
		ResourceEffect minusTools = new ResourceEffect(ResourceType.TOOLS, -1);

		updateAttribute(CharacterAttribute.BUILDING, 1);
		return new AbilityResult(AbilityResultType.SUCCESS,
				"Le Schtroumpf Bricoleur a fabriqué un piège : " + plusDefense + ", " + minusTools,
				List.of(plusDefense, minusTools));
	}

	private AbilityResult executeInventGadget(SmurfVillage village) {
		Logger.LogDebug("Handy Smurf invented a gadget");

		int buildingSkills = getAttribute(CharacterAttribute.BUILDING);

		final double betterToolsChance = 0.3 + buildingSkills * 0.1;
		final double moreMoralChance = 0.5 + buildingSkills * 0.05;
		final double failureChance = 0.2;
		
		return new WeightedOutcomeSelector()
			.addChoice(new OutcomeChoice(
				betterToolsChance,
				AbilityResultType.SUCCESS,
				"Succès ! Le gadget améliore les outils ",
				List.of(new ResourceEffect(ResourceType.TOOLS, 2)),
				() -> updateAttribute(CharacterAttribute.BUILDING, 2)
			))
			.addChoice(new OutcomeChoice(
				moreMoralChance,
				AbilityResultType.SUCCESS,
				"Le gadget amuse le village ",
				List.of(new ResourceEffect(ResourceType.MORAL, 1))
			))
			.addChoice(new OutcomeChoice(
				failureChance,
				AbilityResultType.FAILURE,
				"Échec ! Le gadget explose ",
				List.of(new ResourceEffect(ResourceType.TOOLS, -1))
			))
			.selectAndExecute(village, null);
	}
}