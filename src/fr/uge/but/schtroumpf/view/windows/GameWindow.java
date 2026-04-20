package fr.uge.but.schtroumpf.view.windows;

import module java.base;

import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.events.*;
import fr.uge.but.schtroumpf.model.characters.*;

public class GameWindow implements Window {
	// constants
	private static final String SEPARATOR = "-------------------------";
	
	@Override
	public void load() {
		IO.println("GAME !");
	}
	
	public void displayHud(HudContext hud) {
		displayRound(hud.round());
		displayCurrentEvent(hud.round(), hud.village());
		displayResources(hud.village().getResources(), hud.village().getResourcesDiff());
		IO.println(SEPARATOR);
	}

	public void displayCurrentEvent(int currentRound, SmurfVillage village) {
		EventHistory currentEvent = village.getEventFromRound(currentRound);
		if (currentEvent != null) {
			displayEvent(currentEvent.eventType());
		}
	}
	
	public void displayEffectsApplied(List<Effect> effects) {
		IO.println(SEPARATOR);
		IO.println("Effets appliques sur le village");

		for (Effect effect : effects) {
			IO.println(effect);
		}
	}
	
	public void newLine() {
		IO.println();
	}
	
	private void displayRound(int round) {
		IO.println(SEPARATOR);
		IO.println(String.format("Mois: %d", round));
	}
	
	private void displayResources(List<ResourceSnapshot> snapList, List<ResourceSnapshot> diffSnapList) {
		IO.println(SEPARATOR);
		IO.println("Ressources:");

		// both snapshots must contain same amount of elements
		if (snapList.size() != diffSnapList.size()) {
			throw new IllegalStateException("both snapshots should have same size");
		}
		
		// display each resource on a line
		for (int i = 0; i < snapList.size(); i++) {
			ResourceSnapshot snap = snapList.get(i);
			String deltaStr = getResourceDeltaStr(diffSnapList.get(i).quantity());
			
			IO.println(String.format(
				"%s : %d%s", snap.resource(), snap.quantity(), deltaStr
			));
		}
	}
	
	private void displayEvent(GameEventType eventType) {
		// rien afficher si pas d'evenemet
		if (eventType == null) {
			return;
		}
		
		IO.println(SEPARATOR);
		IO.println("Evenement en cours : " + eventType.getTitle());
		IO.println("description de l'evenement : " + eventType.getDescription());
	}
	
	/**
	 * @return a string like (+2) or (-1) to show by how much a resource changed
	 * returns, an empty string if delta is 0
	 */
	private String getResourceDeltaStr(int delta) {
		if (delta > 0) {
			return String.format(" (+%d)", delta);
		}
		else if (delta < 0) {
			return String.format(" (%d)", delta);
		}
		
		return "";
	}

	public enum Choice {
        START_GAME(1),
        CONTINUE_GAME(2),
        EXIT(0);

        private final int code;
        Choice(int code) { this.code = code; }

        public static Choice fromCode(int code) {
            for (Choice c : values()) {
                if (c.code == code) return c;
            }
            throw new IllegalArgumentException("invalid code");
        }
    }
	
	public record HudContext(int round, SmurfVillage village) { }
}
