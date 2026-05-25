package fr.uge.but.schtroumpf.model.phases;

import java.util.ArrayList;

import fr.uge.but.schtroumpf.model.GameRandomness;
import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.ResourceEffect;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.view.Logger;

public class ProductionPhase implements GamePhase {
	final int BASE_ENERGY_RECHARGE_RATE = 3;

	@Override public GamePhaseType getType() { return GamePhaseType.PRODUCTION_PHASE; }
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Logger.LogTrace("entered production phase");
		SmurfVillage village = ctx.village();
		
		// generate resources
		var resourceGenerationEffects = new ArrayList<ResourceEffect>();
		for (ResourceType resourceType : ResourceType.values()) {
			int delta = GameRandomness.randomChoice(1, 3);
			resourceGenerationEffects.add(new ResourceEffect(resourceType, delta));
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
