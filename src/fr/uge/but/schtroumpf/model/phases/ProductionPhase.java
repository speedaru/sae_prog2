package fr.uge.but.schtroumpf.model.phases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.uge.but.schtroumpf.model.GameRandomness;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.ResourceEffect;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class ProductionPhase implements GamePhase {
	final int BASE_ENERGY_RECHARGE_RATE = 1;
	final int MAX_RESOURCE_GAIN = 4;
	@Override public GamePhaseType getType() { return GamePhaseType.PRODUCTION_PHASE; }
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Logger.LogTrace("entered production phase");
		SmurfVillage village = ctx.village();
		
		// generate resources
		var resourceGenerationEffects = new ArrayList<ResourceEffect>();
		int resource_to_gain= MAX_RESOURCE_GAIN;
		List<ResourceType> types = new ArrayList<>(Arrays.asList(ResourceType.values()));
		while(resource_to_gain>0) {
			ResourceType type= types.get(GameRandomness.randomChoice(0,types.size()));
			types.remove(type);
			int delta = GameRandomness.randomChoice(1, Math.min(2, resource_to_gain)+1);
			resourceGenerationEffects.add(new ResourceEffect(type,delta));
			resource_to_gain -= delta;
		}
		
		// apply generation effects
		village.applyEffects(resourceGenerationEffects);
		
		// replenish energy for council members
		for (SmurfCharacter smurf : village.getCouncilMembers()) {
			village.rechargeSmurfEnergy(smurf, BASE_ENERGY_RECHARGE_RATE);
		}
	}

	@Override
	public void onExit(GamePhaseContext ctx) {
		Logger.LogTrace("finished production phase");
	}

	@Override
	public GamePhase getNextPhase() {
		return new EventPhase();
	}
}
