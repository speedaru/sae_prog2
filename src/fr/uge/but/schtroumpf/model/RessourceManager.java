package fr.uge.but.schtroumpf.model;

import module java.base;

public class RessourceManager {
	public record RessourceSnapshot(RessourceType ressource, int quantity) { }

	// hash map of ressource type and quantity
	private final HashMap<RessourceType, Integer> ressources = new HashMap<RessourceType, Integer>();

	// constants
	public static final int MAX_QUANTITY = 10;
	
	public void add(RessourceType type, int quantity) {
		// if ressource doesn't exist create it with a quantity of 0
		if (!ressources.containsKey(type)) {
			ressources.put(type, 0);
		}
		
		// max out ressource at MAX_QUANTITY
		int newQuantity = Math.min(MAX_QUANTITY, ressources.get(type) + quantity);
		ressources.put(type, newQuantity);
	}
	
	public int get(RessourceType type) {
		return ressources.getOrDefault(type, 0);
	}
	
	public List<RessourceSnapshot> getRessourcesSnapshot() {
		var snap = new ArrayList<RessourceSnapshot>();

		for (RessourceType type : RessourceType.values()) {
			snap.add(new RessourceSnapshot(type, get(type)));
		}
		
		return List.copyOf(snap);
	}
}
