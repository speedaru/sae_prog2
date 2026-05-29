package fr.uge.but.schtroumpf.model.phases;

import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.ResourceManager;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.GameRandomness;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class ProductionPhase implements GamePhase {
	private static final int BASE_ENERGY_RECHARGE_RATE = 1;
	private static final int MAX_GAIN_PER_RESOURCE = 2;
	
	@Override public GamePhaseType getType() { return GamePhaseType.PRODUCTION_PHASE; }
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Logger.LogTrace("entered production phase");
		SmurfVillage village = ctx.village();
		
		// generate resources
		int productionRateLeft = village.getProductionRate();
		Logger.LogTrace("production rate: %d", productionRateLeft);
		
		ArrayList<ResourceEffect> effects = new ArrayList<>();
		
		while (productionRateLeft > 0) {
			ResourceEffect selection = generateResource(village, productionRateLeft, effects);

			if (selection == null) {
				break;
			}

			effects.add(selection);
			productionRateLeft -= selection.delta();
		}
		
		// apply generation effects
		village.applyEffects(effects);
		
		// recharge energy for council members
		rechargeCouncil(village);
	}

	@Override
	public void onExit(GamePhaseContext ctx) {
		Logger.LogTrace("finished production phase");
	}

	@Override
	public GamePhase getNextPhase() {
		return new EventPhase();
	}
	
	private void rechargeCouncil(SmurfVillage village) {
		for (SmurfCharacter smurf : village.getCouncilMembers()) {
			village.rechargeSmurfEnergy(smurf, BASE_ENERGY_RECHARGE_RATE);
		}
	}
	
	private ResourceEffect generateResource(SmurfVillage village, int productionRateLeft, ArrayList<ResourceEffect> effects) {
		List<ResourceType> validTypes = getAvailableTypes(village, effects);
		if (validTypes.isEmpty()) {
			return null;
		}

		ResourceType type = randomType(validTypes);

		int maxGain = computeMaxGain(village, type, productionRateLeft, effects);
		if (maxGain <= 0) {
			return null;
		}

		int delta = randomDelta(maxGain);
		Logger.LogTrace("producing %d of %s", delta, type.getDisplayName());

		return new ResourceEffect(type, delta);
	}
	
	private List<ResourceType> getAvailableTypes(SmurfVillage village, ArrayList<ResourceEffect> effects) {
		List<ResourceType> validTypes = village.getProductionAllowedResources();
		
		// remove resources which will be full after we apply effects
		validTypes.removeIf(type -> {
			return getResourceSpace(village, type, effects) == 0;
		});
		
		return List.copyOf(validTypes);
	}
	
	private int computeMaxGain(SmurfVillage village, ResourceType type, int productionRateLeft, ArrayList<ResourceEffect> effects) {
	    int availableSpace = getResourceSpace(village, type, effects);

		return Math.min(Math.min(availableSpace, MAX_GAIN_PER_RESOURCE), productionRateLeft);
	}
	
	/** returns quantity before resource reaches max, returns minimum 0 */
	private int getResourceSpace(SmurfVillage village, ResourceType type, ArrayList<ResourceEffect> effects) {
		int space = ResourceManager.MAX_QUANTITY - village.getResourceQuantity(type);

		for (ResourceEffect effect : effects) {
			if (effect.resourceType() == type) {
				space -= effect.delta();
			}
		}
		
		return Math.max(0, space);
	}
	
	private ResourceType randomType(List<ResourceType> types) {
	    return types.get(GameRandomness.randomChoice(0, types.size()));
	}

	private int randomDelta(int maxGain) {
	    return GameRandomness.randomChoice(1, maxGain + 1);
	}
}
