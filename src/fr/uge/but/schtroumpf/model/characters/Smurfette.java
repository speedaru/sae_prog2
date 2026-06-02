package fr.uge.but.schtroumpf.model.characters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.*;
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
    @Override public void setAttribute(CharacterAttribute attrib, int value) { attributes.put(attrib, value); }
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
				new PossibleBranch("Effets possibles :", List.of(
					new ResourceEffect(ResourceType.GOLD,-2),
					new ResourceEffect(ResourceType.BERRIES, 2)
				)),
				new PossibleBranch("Effets possibles :", List.of(
					new ResourceEffect(ResourceType.GOLD,-2),
					new ResourceEffect(ResourceType.SARSAPARILLA, 2)
				))
			),
			this::executeNegociate
		);
		
		CharacterAbility appease = new CharacterAbility(
			"Apaiser un conflit interne",
			"Schtroumpfette calme deux Schtroupfs en embrouille",
			1,
			List.of(
				new ResourceSnapshot(ResourceType.KNOWLEDGE, 2)
			),
			List.of(
				new PossibleBranch("Succès :", List.of(
					new ResourceEffect(ResourceType.MORAL, 2)
				)),
				new PossibleBranch("Echec :", List.of())
			),
			this::executeAppease
		);
			
		CharacterAbility feast = new CharacterAbility(
			"Organiser une grande fête",
			"Schtroumpfette Prépare et invite les Schtroumpfs à un festin!",
			4, 
			List.of(
				new ResourceSnapshot(ResourceType.BERRIES, 4)
			),
			List.of(
				new PossibleBranch("Effets :", List.of(
					new ResourceEffect(ResourceType.MORAL, 3),
					new ResourceEffect(ResourceType.BERRIES, -3)
				))
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
		ResourceEffect plusMoral = new ResourceEffect(ResourceType.MORAL, 3);
		ResourceEffect minusBerries = new ResourceEffect(ResourceType.BERRIES, -3);
		
		Logger.LogDebug("Les schtroumpfs font la fête, %s %s", plusMoral, minusBerries);
		return new AbilityResult(AbilityResultType.SUCCESS,
				"Les schtroumpfs ont fait la fête : " + plusMoral + ", " + minusBerries,
				List.of(plusMoral, minusBerries));
	}

}
