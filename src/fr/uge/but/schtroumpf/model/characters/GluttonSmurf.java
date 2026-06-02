package fr.uge.but.schtroumpf.model.characters;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.GameRandomness;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class GluttonSmurf implements SmurfCharacter {
	private int energy = 10;
	private final Map<CharacterAttribute, Integer> attributes = new HashMap<>();
	
	public GluttonSmurf() {
        attributes.put(CharacterAttribute.WISDOM, 1);
    }
	
	@Override public SmurfType getType() { return SmurfType.GLUTTON_SMURF; }
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
		// Cueillir des baies
		CharacterAbility gatherBerries = new CharacterAbility(
			"Cueillir des baies",
			"Le Schtroumpf Gourmand va cueillir des baies.",
			1,
			List.of(),
			List.of(
				new ResourceEffect(ResourceType.BERRIES, 1)
			),
			this::executeGatherBerries
		);

		// Organiser une réunion
		CharacterAbility stealBerries = new CharacterAbility(
			"Voler des baies",
			"Le Schtroumpf Gourmand vole des baies à un village voisin",
			3,
			List.of(new ResourceSnapshot(ResourceType.DEFENSE,3)),
			List.of(
				new ResourceEffect(ResourceType.DEFENSE, -1),
				new ResourceEffect(ResourceType.BERRIES, 3)
			),
			this::executeStealBerries
		);

		// Négocier avec les animaux
		CharacterAbility findRareMushroom = new CharacterAbility(
			"Trouver un champignon rare",
			"Le Schtroumpf Gourmand trouve un champignon rare. Bonus entièrement aléatoire.",
			1,
			List.of(),
			List.of(
				new ResourceEffect(ResourceType.BERRIES, 1),
				new ResourceEffect(ResourceType.SARSAPARILLA, 1),
				new ResourceEffect(ResourceType.GOLD, 1),
				new ResourceEffect(ResourceType.TOOLS, 1),
				new ResourceEffect(ResourceType.MORAL, 1),
				new ResourceEffect(ResourceType.DEFENSE, 1),
				new ResourceEffect(ResourceType.KNOWLEDGE, 1)
			),
			this::executeFindRareMushroom
		);

		return List.of(
			gatherBerries,
			stealBerries,
			findRareMushroom
		);
	}
	
	private AbilityResult executeGatherBerries(SmurfVillage village) {
		Logger.LogDebug("Glutton gathered berries");
		ResourceEffect plusBerries = new ResourceEffect(ResourceType.BERRIES, 1);
		return new AbilityResult(
			AbilityResultType.NEUTRAL,
			"Succès ! Le Schtroumpf Gourmand a cueillis des baies : " + plusBerries,
			List.of(plusBerries)
			);
		}

	private AbilityResult executeStealBerries(SmurfVillage village) {
		Logger.LogDebug("Steals Berries from another village");
		ResourceEffect minusDefense = new ResourceEffect(ResourceType.DEFENSE, -1);
		ResourceEffect plusBerries = new ResourceEffect(ResourceType.BERRIES, 2);
		return new AbilityResult(
			AbilityResultType.NEUTRAL,
			"Le Schtroumpf Gourmand a volé des baies chez le village voisin " + plusBerries + minusDefense,
			List.of(plusBerries, minusDefense)
		);
	}

	private AbilityResult executeFindRareMushroom(SmurfVillage village) {
		Logger.LogDebug("Glutton found a rare mushroom");
		ResourceType[] allResources = ResourceType.values();
		ResourceType randomRes = allResources[GameRandomness.randomChoice(0, allResources.length)];
		ResourceEffect randomBonus = new ResourceEffect(randomRes, 1);
		
		
		String resMessage = "Le Schtroumpf Gourmand a trouvé un champignon rare :";
		return new AbilityResult(
				AbilityResultType.NEUTRAL,
				resMessage + randomBonus, 
				List.of(randomBonus)		
		);
	}
}
