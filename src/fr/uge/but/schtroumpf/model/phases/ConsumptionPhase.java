package fr.uge.but.schtroumpf.model.phases;

import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.rules.*;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class ConsumptionPhase implements GamePhase {
	private final List<ConsumptionRule> rules = new ArrayList<>();
	private ConsumptionReport currentReport;

	public ConsumptionPhase() {
		rules.add(new FoodRule());
		rules.add(new OverpopulationRule());
        rules.add(new WinterHeatingRule());
        rules.add(new InfrastructureDecayRule());
	}
	
	@Override
	public void onEnter(GamePhaseContext ctx) {
		Game game = ctx.game();
        SmurfVillage village = game.getVillage();
        int currentRound = ctx.currentRound();
        
        List<ConsumptionRuleResult> results = new ArrayList<>();
        List<String> crises = new ArrayList<>();

        // loop and execute rules
        for (ConsumptionRule rule : rules) {
            ConsumptionRuleResult res = rule.evaluate(village, currentRound);
            results.add(res);
            if (!res.feedbackMessage().isEmpty()) {
				crises.add(res.feedbackMessage());
            }
        }

        Season season = determineSeason(currentRound);
        this.currentReport = new ConsumptionReport(currentRound, season, results, crises);
	}

	@Override
	public void onExit(GamePhaseContext ctx) {
		Logger.LogTrace("finished consumption phase");
	}

	@Override public GamePhaseType getType() { return GamePhaseType.CONSUMPTION_PHASE; }
	@Override public GamePhase getNextPhase() { return new CrisisPhase(); }
	
	public ConsumptionReport getCurrentReport() { return this.currentReport; }
	
	private Season determineSeason(int currentRound) {
		return Season.getSeason(currentRound);
	}
}
