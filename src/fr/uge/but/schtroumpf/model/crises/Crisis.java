package fr.uge.but.schtroumpf.model.crises;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.VillageModifierContext;

public interface Crisis {
	CrisisType getType();
	
	/** applies crisis specific penalties to smurf village modifiers */
	void applyModifiers(VillageModifierContext ctx);
	
	/** apply immediate effects like decreasing resources. default bcs not all crises need this  */
	default void applyImmediateEffects(SmurfVillage village) { }
}
