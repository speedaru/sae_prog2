package fr.uge.but.schtroumpf.view.windows;

import fr.uge.but.schtroumpf.view.windows.ConsoleMenu.MenuChoice;

public class StartWindow implements Window {
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

	private final ConsoleMenu menu = new ConsoleMenu();
	
	@Override
	public void load() {
		menu.addChoice(new MenuChoice(Choice.START_GAME.code, "Start Game"));
		menu.addChoice(new MenuChoice(Choice.CONTINUE_GAME.code, "Continue a previous game:Wqa"));
        menu.addChoice(new MenuChoice(Choice.EXIT.code, "Exit"));
	}
	
	@Override
	public int getUserChoice() {
		menu.print();
		return menu.prompt().num();
	}
}
