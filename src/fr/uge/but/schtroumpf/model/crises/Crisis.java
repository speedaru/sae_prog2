package fr.uge.but.schtroumpf.model.crises;

import fr.uge.but.schtroumpf.model.SmurfVillage;

public interface Crisis {
	CrisisType getType();
	
	/** apply the penalty of the crisis for the current round */
	void doEffects(SmurfVillage village);
}
