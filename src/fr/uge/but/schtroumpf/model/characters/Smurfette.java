package fr.uge.but.schtroumpf.model.characters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.model.utils.OutcomeChoice;
import fr.uge.but.schtroumpf.model.utils.WeightedOutcomeSelector;

public class Smurfette implements SmurfCharacter {
	private int energy = 10;
	private final Map<CharacterAttribute, Integer> attributes = new HashMap<>();
	
	public Smurfette() {
        attributes.put(CharacterAttribute.KINDNESS, 0);
    }
	
	@Override public SmurfType getType() { return SmurfType.SMURFETTE; }
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
		CharacterAbility negociate = new CharacterAbility(
			"Négocier avec villages voisins",
			"Rencontre avec des villages voisins pour tenter d'obtenir de l'or ou de la Salsepareille",
			3,
			List.of(
				new ResourceSnapshot(ResourceType.DEFENSE, 1),
				new ResourceSnapshot(ResourceType.GOLD, 2)
			),
			List.of(
				new ResourceEffect(ResourceType.BERRIES, 2),
				new ResourceEffect(ResourceType.SARSAPARILLA, 2),
				new ResourceEffect(ResourceType.GOLD,-2)
			),
			this::executeNegociate
		);
		
		CharacterAbility appease = new CharacterAbility(
			"Apaiser un conflit interne",
			"Schtroumpfette calme deux Schtroupfs en embrouille",
			1,
			List.of(
					new ResourceSnapshot(ResourceType.KNOWLEDGE, 6)
				),
			List.of(
					new ResourceEffect(ResourceType.MORAL, 2)
				),
			this::executeAppease
		);
			
		CharacterAbility feast = new CharacterAbility(
			"Organise une grande fête pour le village",
			"Schtroumpfette Prépare et invite les Schtroumpfs à un festin!",
			4, 
			List.of(
				new ResourceSnapshot(ResourceType.BERRIES, 7)
			),
			List.of(
				new ResourceEffect(ResourceType.MORAL, 5),
				new ResourceEffect(ResourceType.BERRIES,-6)
			),
			this::executeFeast
		);

		return List.of(negociate, appease, feast);
				
	}
	private AbilityResult executeNegociate(SmurfVillage village) {
		final double chance = 0.5;

		ResourceEffect plusBerries = new ResourceEffect(ResourceType.BERRIES, 2);
		ResourceEffect plusSarsaparilla = new ResourceEffect(ResourceType.SARSAPARILLA, 2);
		ResourceEffect minusGold= new ResourceEffect(ResourceType.GOLD, -2);

		String resMessage = "Schtroumpfette va parler aux villages voisins et a obtenu ";
		return new WeightedOutcomeSelector()
			.addChoice(new OutcomeChoice(
				chance,
				AbilityResultType.NEUTRAL,
				resMessage,
				List.of(plusBerries, minusGold)
			))
			.addChoice(new OutcomeChoice(
				chance,
				AbilityResultType.NEUTRAL,
				resMessage,
				List.of(plusSarsaparilla, minusGold)
			))
			.selectAndExecute(village, null);
	}
	
	private AbilityResult executeAppease(SmurfVillage village) {
		int kindnessSkills = getAttribute(CharacterAttribute.KINDNESS);
		
		final double successChance = 0.60 + kindnessSkills * 0.1;
		final double failureChance = 0.40;
		
		return new WeightedOutcomeSelector()
			.addChoice(new OutcomeChoice(
				successChance,
				AbilityResultType.SUCCESS,
				"Succès ! La Schtroumpfette a réconcilié les Schtroumpfs ",
				List.of(new ResourceEffect(ResourceType.MORAL, 2))
			))
			.addChoice(new OutcomeChoice(
				failureChance,
				AbilityResultType.NEUTRAL,
				"Échec ! Les Schtroumpfs ne veulent rien entendre ",
				List.of()
			))
			.selectAndExecute(village, null);
	}

	private AbilityResult executeFeast(SmurfVillage village) {
		ResourceEffect plusMoral = new ResourceEffect(ResourceType.MORAL, 5);
		ResourceEffect minusBerries = new ResourceEffect(ResourceType.BERRIES, -6);
		
		Logger.LogDebug("Les schtroumpfs font la fête, +5 moral -6 baies");
		return new AbilityResult(AbilityResultType.SUCCESS,
				"Les schtroumpfs ont fait la fête : " + plusMoral + ", " + minusBerries,
				List.of(plusMoral, minusBerries));
	}

}
