package fr.uge.but.schtroumpf.model.events;

import module java.base;

import fr.uge.but.schtroumpf.model.*;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class GargamelAttack implements GameEvent {
	@Override public GameEventType getEventType() { return GameEventType.GARGAMEL_ATTACK; }

	@Override
	public List<ResourceEffect> trigger(SmurfVillage village) {
		List<ResourceEffect> impacts = new ArrayList<ResourceEffect>();
		
		// always lose defense
		impacts.add(new ResourceEffect(ResourceType.DEFENSE, -3));
		
		// always lose moral
		impacts.add(new ResourceEffect(ResourceType.MORAL, -2));
		
		return impacts;
	}
	
	public static int getFrequencyModifier(int currentRound) {
		final int START_ROUND = 5;
		final int DELTA_PER_ROUND = 10;
		
		if (currentRound >= START_ROUND) {
			int rounds = currentRound - START_ROUND + 1;
			return rounds * DELTA_PER_ROUND;
		}

		return 0;
	}
}
