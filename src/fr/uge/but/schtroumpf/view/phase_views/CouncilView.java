package fr.uge.but.schtroumpf.view.phase_views;

import module java.base;

import fr.uge.but.schtroumpf.model.characters.*;
import fr.uge.but.schtroumpf.model.phases.GamePhaseContext;
import fr.uge.but.schtroumpf.view.ConsoleMenu;
import fr.uge.but.schtroumpf.view.ConsoleMenu.*;

public class CouncilView implements PhaseView {
	private final ConsoleMenu menu = new ConsoleMenu();
	
	@Override
	public void display(GamePhaseContext ctx) {
		menu.clear();
		
		List<SmurfCharacter> availableSmurfs = ctx.village().getAvailableSmurfs();
		
		// create smurf choices
		int smurfNum = 1;
		for (SmurfCharacter smurf : availableSmurfs) {
			menu.addChoice(new MenuChoice(smurfNum++, smurf.getName()));
		}

		menu.addChoice(new MenuChoice(0, "Terminer le conseil"));
		menu.print();
	}
	
	public void displayAvailableAbilities(List<CharacterAbility> availableAbilities) {
		IO.println("Aptitudes disponibles :");
		
		menu.clear();

		int abilityNum = 1;
		for (CharacterAbility ability : availableAbilities) {
			menu.addChoice(new MenuChoice(abilityNum++, ability.toString()));
		}
		
		menu.addChoice(new MenuChoice(0, "Annuler"));
		menu.print();
	}
	
	public void displayActionResults(SmurfCharacter selectedSmurf, CharacterAbility choseAbility, List<Effect> effects) {
		IO.println(String.format("Consequences de %s sur le village:", choseAbility.description()));
		
		for (Effect effect : effects) {
			IO.println(effect);
		}
		
		IO.println(String.format("%s a maintenant %d d'energie", selectedSmurf, selectedSmurf.getEnergy()));
	}
	
	public void displayErrorMessage(String message) {
		IO.println(message);
	}
	
	public int promptSmurfChoice() {
		return menu.prompt().num();
	}

	public int promptAbilityChoice() {
		return menu.prompt().num();
	}
}