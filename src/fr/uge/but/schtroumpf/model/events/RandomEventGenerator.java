package fr.uge.but.schtroumpf.model.events;

import java.util.List;

import fr.uge.but.schtroumpf.model.GameRandomness;

public class RandomEventGenerator {
	private static final List<GameEventType> availableTypes = List.of(GameEventType.values());
	
	public static GameEvent nextEvent(int currentRound) {
		int totalFrequency = 0;
		for (var type : availableTypes) {
			totalFrequency += type.calcFrequency(currentRound);
		}
		
		// pick random event
		int roll = GameRandomness.randomChoice(0, totalFrequency + 1);
		
		// pick event from event list that corresponds to rolled frequency
		for (var type : availableTypes) {
			roll -= type.calcFrequency(currentRound);
			if (roll <= 0) {
				return GameEvent.fromType(type);
			}
		}
		
		throw new IllegalStateException(String.format("invalid roll : %d (totalFreq : %d", roll, totalFrequency));
	}
}
