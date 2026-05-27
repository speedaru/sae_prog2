package fr.uge.but.schtroumpf.model.characters;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class GrandSmurf implements SmurfCharacter {
	private int energy = 10;
	private final Map<CharacterAttribute, Integer> attributes = new HashMap<>();
	
	public GrandSmurf() {
        attributes.put(CharacterAttribute.WISDOM, 5);
    }
	
	@Override public SmurfType getType() { return SmurfType.GRAND_SMURF; }
	@Override public int getEnergy() { return energy; }
	@Override public void setEnergy(int value) { energy = value; }

	@Override
	public void updateEnergy(SmurfVillage village, int delta) {
		final int finalMaxEnergy = village.getDynamicMaxEnergy(this);
		energy = Math.clamp(energy + delta, 0, finalMaxEnergy);
	}

	@Override public int getAttribute(CharacterAttribute attrib) { return attributes.getOrDefault(attrib, 0); }
    @Override public void updateAttribute(CharacterAttribute attrib, int delta) { attributes.put(attrib, getAttribute(attrib) + delta); }

	@Override
	public String toString() { return getType().getName(); }

	@Override
	public List<CharacterAbility> getAbilities() {
		// Consulter le grimoire
		CharacterAbility checkSpellBook = new CharacterAbility(
			"Consulter le grimoire",
			"Le Grand Schtroumpf étudie un ancien grimoire afin de découvrir de nouvelles connaissances magiques."
			+ " Succès (+1 Savoir) indexé sur votre Sagesse, mais risque d'échec infligeant -1 Moral.",
			2,
			List.of(
				new ResourceSnapshot(ResourceType.MORAL, 1)
			),
			List.of(
				new ResourceEffect(ResourceType.KNOWLEDGE, 1),
				new ResourceEffect(ResourceType.MORAL, -1)
			),
			this::executeCheckSpellBook
		);

		// Organiser une réunion
		CharacterAbility planMeeting = new CharacterAbility(
			"Organiser une réunion",
			"Le Grand Schtroumpf rassemble le village pour motiver les Schtroumpfs et renforcer leur moral.",
			3,
			List.of(),
			List.of(
				new ResourceEffect(ResourceType.MORAL, 2)
			),
			this::executePlanMeeting
		);

		// Négocier avec les animaux
		CharacterAbility talkToAnimals = new CharacterAbility(
			"Négocier avec les animaux",
			"Le Grand Schtroumpf négocie avec les animaux de la forêt pour obtenir leur aide. Permet d'obtenir +1 d'Or"
			+ "  ou +1 de Défense",
			1,
			List.of(),
			List.of(
				new ResourceEffect(ResourceType.GOLD, 1),
				new ResourceEffect(ResourceType.DEFENSE, 1)
			),
			this::executeTalkToAnimals
		);

		return List.of(
			checkSpellBook,
			planMeeting,
			talkToAnimals
		);
	}
	
	private AbilityResult executeCheckSpellBook(SmurfVillage village) {
		// base success chance on wisdom level
		final double successChance = Math.min(1.0, 0.5 + (getAttribute(CharacterAttribute.WISDOM) * 0.05));

		ResourceEffect plusKnowledge = new ResourceEffect(ResourceType.KNOWLEDGE, 1); 
		ResourceEffect minusMoral = new ResourceEffect(ResourceType.MORAL, -1);

		if (GameRandomness.rollChance(successChance)) {
			Logger.LogDebug("Grand Smurf successfully consulted the grimoire");
			updateAttribute(CharacterAttribute.WISDOM, 1); // smurf gets wiser
			return new AbilityResult(
				AbilityResultType.SUCCESS,
				"Succès ! Le Grand Schtroumpf a déchiffré une formule : " + plusKnowledge,
				List.of(plusKnowledge)
			);
		}
		else {
			return new AbilityResult(
				AbilityResultType.FAILURE,
				"Échec ! Les grimoires étaient trop complexes : " + minusMoral,
				List.of(minusMoral)
			);
		}
	}

	private AbilityResult executePlanMeeting(SmurfVillage village) {
		Logger.LogDebug("Grand Smurf organized a meeting");
		ResourceEffect plusMoral = new ResourceEffect(ResourceType.MORAL, 2);
		return new AbilityResult(
			AbilityResultType.NEUTRAL,
			"Le Grand Schtroumpf a reuni le village " + plusMoral,
			List.of(plusMoral)
		);
	}

	private AbilityResult executeTalkToAnimals(SmurfVillage village) {
		final double successChance = 0.5;

		ResourceEffect plusGold = new ResourceEffect(ResourceType.GOLD, 1);
		ResourceEffect plusDefense = new ResourceEffect(ResourceType.DEFENSE, 1);

		String resMessage = "Le Grand Schtroumpf a parle aux animaux de la foret ";
		if (GameRandomness.rollChance(successChance)) {
			Logger.LogTrace("Grand Smurf talked to the animals and got gold");
			return new AbilityResult(
				AbilityResultType.NEUTRAL,
				resMessage + plusGold,
				List.of(plusGold)
			);
		}
		else { 
			Logger.LogTrace("Grand Smurf talked to the animals and asked for defense");
			return new AbilityResult(
				AbilityResultType.NEUTRAL,
				resMessage + plusDefense,
				List.of(plusDefense)
			);
		}
	}
}
