package fr.uge.but.schtroumpf.model.phases;

import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.rules.*;
import fr.uge.but.schtroumpf.view.Logger;

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

        // Execute the rule engine loops sequentially
        for (ConsumptionRule rule : rules) {
            ConsumptionRuleResult res = rule.evaluate(village, currentRound);
            results.add(res);
            if (res.crisisTriggered()) {
                crises.add(res.crisisMessage());
            }
        }

        // Compile the generic summary report for the waiting View layer
        String season = determineSeason(currentRound);
        this.currentReport = new ConsumptionReport(currentRound, season, results, crises);
	}

	@Override
	public void onExit(GamePhaseContext ctx) {
		Logger.LogTrace("finished consumption phase");
	}

	@Override public PhaseType getType() { return PhaseType.CONSUMPTION_PHASE; }
	@Override public GamePhase getNextPhase() { return new CrisisPhase(); }
	
	public ConsumptionReport getCurrentReport() { return this.currentReport; }
	
	private String determineSeason(int currentRound) {
		return switch (currentRound / 3) {
		case 0 -> "Spring";
		case 1 -> "Summer";
		case 2 -> "Autumn";
		case 3 -> "Winter";
		default -> throw new IllegalArgumentException("Unexpected value: " + currentRound % 3);
		};
	}
}
