package fr.uge.but.schtroumpf.model;

import module java.base;

import fr.uge.but.schtroumpf.model.RessourceManager.RessourceSnapshot;

public class SmurfVillage {
	private final RessourceManager ressourceManager = new RessourceManager();
	
	public SmurfVillage() {
		// set arbitrary quantites
		ressourceManager.add(RessourceType.BERRIES, 2);
		ressourceManager.add(RessourceType.GOLD, 1);
	}
	
	public List<RessourceSnapshot> getRessources() {
		return ressourceManager.getRessourcesSnapshot();
	}
}
