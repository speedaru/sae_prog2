package fr.uge.but.schtroumpf.view.windows;

import module java.base;

import fr.uge.but.schtroumpf.model.Logger;

public class ConsoleMenu {
	public record MenuChoice(int num, String description) {
		public MenuChoice {
			Objects.requireNonNull(description);
		}
	}

	enum Choice {
		START_GAME(new MenuChoice(1, "start game"));
		
		private final MenuChoice choice;
		
		Choice(MenuChoice choice) {
			this.choice = Objects.requireNonNull(choice);
		}
		
		public MenuChoice getChoice() { return choice; }
	}
	
	private final ArrayList<MenuChoice> choices = new ArrayList<MenuChoice>();
	
	public void addChoice(MenuChoice choice) {
		Objects.requireNonNull(choice);
		choices.add(choice);
	}
	
	public void print() {
		for (var choice : choices) {
			IO.println(String.format("%d. %s", choice.num(), choice.description()));
		}
	}
	
	public MenuChoice prompt() {
		while (true) {
			String promptMessage = "\n> ";
			String input = IO.readln(promptMessage);
			
			// try to convert input to int and then return selected choice if it's
			// in the list of choices, otherwise prompt again
			try {
				int num = Integer.parseInt(input);
				for (var choice : choices) {
					if (choice.num() == num) {
						return choice;
					}
				}
			} catch (NumberFormatException ex) {
				Logger.LogError(ex.getMessage());
			}
			
			// reprompt
			Logger.LogError("invalid choice, select a valid choice pls");
		}
	}
}
