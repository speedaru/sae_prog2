package fr.uge.but.schtroumpf.model.crises;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.crises.Crises.*;
import fr.uge.but.schtroumpf.model.types.VillageModifierContext;

public interface Crisis {
	CrisisType getType();
	
	/** applies crisis specific penalties to smurf village modifiers */
	void applyModifiers(VillageModifierContext ctx);
	
	/** apply immediate effects like decreasing resources. default bcs not all crises need this  */
	default void applyImmediateEffects(SmurfVillage village) { }
	
	public static Crisis fromType(CrisisType type) {
		return switch (type) {
			case FAMINE -> new FamineCrisis();
			case EPIDEMIC -> new EpidemicCrisis();
			case REVOLT -> new RevoltCrisis();
			case MASSIVE_ATTACK -> new MassiveAttackCrisis();
			case DARK_AGES -> new DarkAgesCrisis();
			case BANKRUPTCY -> new BankruptcyCrisis();
		};
	}
}
