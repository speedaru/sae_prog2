package fr.uge.but.schtroumpf.model.phases;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.view.Logger;

public class ProductionPhase implements GamePhase {
	@Override
	public GamePhase execute(GamePhaseContext ctx) {
		Logger.LogTrace("entered production phase");

		SmurfVillage village = ctx.village();
		var ressourcesSnap = village.getRessources();
		
		ctx.window().displayRessources(ressourcesSnap);

		IO.readln("enter stuff to continue");

		Logger.LogTrace("finished production phase");
		return new EventPhase();
	}
}
