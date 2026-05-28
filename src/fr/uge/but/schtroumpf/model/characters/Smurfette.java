package fr.uge.but.schtroumpf.model.characters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.but.schtroumpf.model.GameRandomness;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class Smurfette implements SmurfCharacter {
	private int energy = 10;
	private final Map<CharacterAttribute, Integer> attributes = new HashMap<>();
	
	public Smurfette() {
        attributes.put(CharacterAttribute.KINDNESS, 5);
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
					new ResourceEffect(ResourceType.SARSAPARILLA, 1),
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
		final double successChance = 0.5;

		ResourceEffect plusBerries = new ResourceEffect(ResourceType.BERRIES, 1);
		ResourceEffect plusSarsaparilla = new ResourceEffect(ResourceType.SARSAPARILLA, 1);
		ResourceEffect minusGold= new ResourceEffect(ResourceType.GOLD, -2);
		String resMessage = "Schtroumpfette va parler aux villages voisins et a obtenu: ";
		if (GameRandomness.rollChance(successChance)) {
			Logger.LogTrace("Smurfette got gold");
			return new AbilityResult(
				AbilityResultType.NEUTRAL,
				resMessage + plusBerries + minusGold,
				List.of(plusBerries,minusGold)
			);
		}
		else { 
			Logger.LogTrace("smurfette got sarsaparilla");
			return new AbilityResult(
				AbilityResultType.NEUTRAL,
				resMessage + plusSarsaparilla + minusGold,
				List.of(plusSarsaparilla, minusGold)
			);
		}
	}
	private AbilityResult executeAppease(SmurfVillage village) {
		final double successChance = 0.80;
		ResourceEffect plusMoral = new ResourceEffect(ResourceType.MORAL, 2);
		
		
	
		if (village.rollChance(successChance)) {
			Logger.LogDebug("Les schtroumpfs ne sont plus en conflit");
			return new AbilityResult(
				AbilityResultType.SUCCESS,
				"Succès ! La Schtroumpfette a réconcilié les Schtroumpfs"+plusMoral,
				List.of(plusMoral)
			);
		}
		else {
			return new AbilityResult(
				AbilityResultType.FAILURE,
				"Échec ! Les Schtroumpfs ne veulent rien entendre..",
				List.of()
			);
		}
	}
	private AbilityResult executeFeast(SmurfVillage village) {
		ResourceEffect plusMoral = new ResourceEffect(ResourceType.MORAL, 5);
		ResourceEffect minusBerries = new ResourceEffect(ResourceType.BERRIES,6);
		
		Logger.LogDebug("Les schtroumpfs font la fête, +5 moral -6 baies");
		return new AbilityResult(AbilityResultType.SUCCESS,
				"Les schtroumpfs ont fait la fête : " + plusMoral + ", " + minusBerries,
				List.of(plusMoral, minusBerries));
		
	}

}
