package fr.uge.but.schtroumpf.model.characters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.but.schtroumpf.model.GameRandomness;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResultType;
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
			"Négocier avec les villages voisins",
			"Rencontre avec des villages voisins pour tenter d'obtenir de l'or ou de la Salsepareille",
			3,
			List.of(
					new ResourceSnapshot(ResourceType.DEFENSE, 4)
				),
			List.of(
					new ResourceEffect(ResourceType.GOLD, 1),
					new ResourceEffect(ResourceType.SARSAPARILLA, 1)
				),
			this::executeNegociate
		);
		return List.of(negociate);
	}
	private AbilityResult executeNegociate(SmurfVillage village) {
		final double successChance = 0.5;

		ResourceEffect plusGold = new ResourceEffect(ResourceType.GOLD, 1);
		ResourceEffect plusSarsaparilla = new ResourceEffect(ResourceType.SARSAPARILLA, 1);

		String resMessage = "Schtroumpfette va parler aux villages voisins et a obtenu: ";
		if (GameRandomness.rollChance(successChance)) {
			Logger.LogTrace("Smurfette got gold");
			return new AbilityResult(
				AbilityResultType.NEUTRAL,
				resMessage + plusGold,
				List.of(plusGold)
			);
		}
		else { 
			Logger.LogTrace("smurfette got sarsaparilla");
			return new AbilityResult(
				AbilityResultType.NEUTRAL,
				resMessage + plusSarsaparilla,
				List.of(plusSarsaparilla)
			);
		}
	}

}
