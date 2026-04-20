package fr.uge.but.schtroumpf.model.crises;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;

public class FamineCrisis implements Crisis {
	@Override public CrisisType getType() { return CrisisType.FAMINE; }

	@Override
	public void doEffects(SmurfVillage village) {
		// every character loses 1 energy because of hunger
		for (SmurfCharacter smurf : village.getCouncilMembers()) {
			smurf.updateEnergy(-1);
		}
	}
	
	@Override
	public String toString() {
		return getType().toString();
	}
}
