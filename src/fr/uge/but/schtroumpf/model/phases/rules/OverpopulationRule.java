package fr.uge.but.schtroumpf.model.phases.rules;

import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.GameRandomness;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;

public class OverpopulationRule implements ConsumptionRule {
	private static String TITLE = "Tensions Démographiques";
	private static int MIN_OVERPOPULATION = 5;
	
	// odds
	private static double BASE_DRAMA_CHANCE = 0.20;;
	private static double DRAMA_POPULATION_INC_RATE = 0.10;
	private static double DRAMA_TIRED_INC_RATE = 0.40;
	private static double MAX_DRAMA_CHANCE = 0.90;
	
    @Override
    public ConsumptionRuleResult evaluate(SmurfVillage village, int turnNumber) {
        var smurfs = village.getAvailableSmurfs();
        int population = smurfs.size();

        // 
        if (population < MIN_OVERPOPULATION) {
            return new ConsumptionRuleResult(TITLE, List.of(), "");
        }

        // calculate max energy vs current energy
        int currentTotalEnergy = 0;
        int maxPossibleEnergy = 0;

        for (SmurfCharacter smurf : smurfs) {
            currentTotalEnergy += smurf.getEnergy();
            maxPossibleEnergy += village.getDynamicMaxEnergy(smurf);
        }

        double populationTiredFactor = 1.0 - ((double) currentTotalEnergy / maxPossibleEnergy);

        double densityChance = BASE_DRAMA_CHANCE + ((population - MIN_OVERPOPULATION) * DRAMA_POPULATION_INC_RATE);
        double finalDramaChance = Math.clamp(densityChance + (populationTiredFactor * DRAMA_TIRED_INC_RATE), 0.0, MAX_DRAMA_CHANCE);

        List<ResourceEffect> resourceEffects = new ArrayList<>();

        // random chance for drama to appear
        if (GameRandomness.rollChance(finalDramaChance)) {
            village.updateResource(ResourceType.MORAL, -1);
            resourceEffects.add(new ResourceEffect(ResourceType.MORAL, -1));

            return new ConsumptionRuleResult(
            	TITLE,
                resourceEffects,
                String.format("Dispute : Des Schtroumpfs surmenés et fatigués se sont battus dans le village ! (risque était de %.0f%%)", finalDramaChance * 100)
            );
        }

        // no drama
        return new ConsumptionRuleResult(TITLE, List.of(), "");
    }
}
