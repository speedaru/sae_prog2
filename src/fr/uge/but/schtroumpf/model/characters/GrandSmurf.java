package fr.uge.but.schtroumpf.model.characters;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.view.Logger;

public class GrandSmurf implements SmurfCharacter {
	private int energy = 0;
	
	public GrandSmurf() {
		energy = 10;
	}
	
	@Override
	public String getName() { return "Grand Schtroumpf"; }

	@Override
	public List<CharacterAbility> getAbilities() {
		// consulter grimoire
		CharacterAbility checkSpellBook = new CharacterAbility(
			"Consulter le grimoire (+Savoir, -Moral si échec)",
			2,
			this::executeCheckSpellBook
		);
		
		// Organiser une réunion
		CharacterAbility planMeeting = new CharacterAbility(
			"Organiser une réunion (+Moral)",
			3,
			this::executePlanMeeting
		);
		
		// Négocier avec les animaux
		CharacterAbility talkToAnimals = new CharacterAbility(
			"Négocier avec les animaux (+Or ou +Défense)",
			1,
			this::executeTalkToAnimals
		);
		
		return List.of(checkSpellBook, planMeeting, talkToAnimals);
	}

	@Override
	public int getEnergy() { return energy; }

	@Override
	public void updateEnergy(int delta) { energy += delta; }
	
	@Override
	public String toString() { return getName(); }
	
	private List<Effect> executeCheckSpellBook(SmurfVillage village) {
		final double successChance = 0.5;

		Effect plusKnowledge = new Effect(ResourceType.KNOWLEDGE, 1); 
		Effect minusMoral = new Effect(ResourceType.MORAL, -1);

		if (GameRandomness.rollChance(successChance)) {
			Logger.LogDebug("Grand Smurf successfully consulted the grimoire");
			return List.of(plusKnowledge);
		}
		
		return List.of(minusMoral);
	}

	private List<Effect> executePlanMeeting(SmurfVillage village) {
		Logger.LogDebug("Grand Smurf organized a meeting");
		return List.of(new Effect(ResourceType.MORAL, 2));
	}

	private List<Effect> executeTalkToAnimals(SmurfVillage village) {
		final double successChance = 0.5;

		if (GameRandomness.rollChance(successChance)) {
			Logger.LogTrace("Grand Smurf talked to the animals and got gold");
			return List.of(new Effect(ResourceType.GOLD, 1));
		}

		Logger.LogTrace("Grand Smurf talked to the animals and asked for defense");
		return List.of(new Effect(ResourceType.DEFENSE, 1));
	}
}
