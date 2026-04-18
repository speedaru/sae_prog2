package fr.uge.but.schtroumpf.view.windows;

import module java.base;

import fr.uge.but.schtroumpf.model.RessourceManager.RessourceSnapshot;

public class GameWindow implements Window {
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

	private static final String SEPARATOR = "-------------------------";

	@Override
	public void load() {
		IO.println("GAME !");
	}
	
	public void displayRessources(List<RessourceSnapshot> ressourcesSnap) {
		IO.println(SEPARATOR);
		IO.println("Ressources:");

		for (RessourceSnapshot snap : ressourcesSnap) {
			String format = String.format("%s : %d", snap.ressource(), snap.quantity());
			IO.println(format);
		}
	}
}
