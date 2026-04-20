package fr.uge.but.schtroumpf.model.phases;

import module java.base;

import fr.uge.but.schtroumpf.model.characters.CharacterAbility;
import fr.uge.but.schtroumpf.model.characters.Effect;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.view.Logger;
import fr.uge.but.schtroumpf.view.phase_views.*;

public class CouncilPhase implements GamePhase {
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("started council phase");

		CouncilView view = new CouncilView();
		
		while (true) {
			List<SmurfCharacter> availableSmurfs = ctx.village().getAvailableSmurfs();
			
			view.display(ctx);
			int smurfChoice = view.promptSmurfChoice();
			
			// exit choice
			if (smurfChoice == 0) break;
			
			SmurfCharacter selectedSmurf = availableSmurfs.get(smurfChoice - 1);
			
			List<CharacterAbility> availableAbilities = selectedSmurf.getAvailableAbilities();
			
			if (availableAbilities.isEmpty()) {
				view.displayErrorMessage("schtroumpf trop fatigue ou pas assez de resources");
				continue;
			}
			
			view.displayAvailableAbilities(availableAbilities);
			int abilityChoice = view.promptAbilityChoice();
			
			if (abilityChoice == 0) continue;
			
			CharacterAbility chosenAbility = availableAbilities.get(abilityChoice - 1);
			
			// use energy and apply effects
			selectedSmurf.updateEnergy(-chosenAbility.energyCost());
			List<Effect> effects = chosenAbility.actionLogic().apply(ctx.village());
			
			for (var effect : effects) {
				ctx.village().applyEffect(effect);
			}
			
			view.displayActionResults(selectedSmurf, chosenAbility, effects);
		}
		
		Logger.LogTrace("finished council phase");
		return new ConsumptionPhase();
	}
}
