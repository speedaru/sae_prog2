package fr.uge.but.schtroumpf.model;

import module java.base;

public class GameRandomness {
	/**
	 * @param odds chance that function returns true, between 0 and 1 
	 */
	public static boolean rollChance(double odds) {
		if (odds < 0.0 || odds > 1.0) {
			throw new IllegalArgumentException("odds must be between 0 and 1");
		}
		return ThreadLocalRandom.current().nextDouble() < odds;
	}
	
	public static int randomChoice(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}
}
