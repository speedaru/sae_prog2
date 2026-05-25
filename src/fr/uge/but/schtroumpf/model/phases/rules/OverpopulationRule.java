package fr.uge.but.schtroumpf.model.phases.rules;

import java.util.ArrayList;
import java.util.List;
import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.GameRandomness;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.model.characters.ResourceEffect;

public class OverpopulationRule implements ConsumptionRule {
    @Override
    public ConsumptionRuleResult evaluate(SmurfVillage village, int turnNumber) {
        var smurfs = village.getAvailableSmurfs();
        int population = smurfs.size();

        // Rule Precondition Gate: No risk of overpopulation friction if village is tiny
        if (population < 5) {
            return new ConsumptionRuleResult("Tensions Démographiques", List.of(), false, "");
        }

        // Calculate maximum potential energy vs current collective remaining energy
        int currentTotalEnergy = 0;
        int maxPossibleEnergy = 0;

        for (SmurfCharacter smurf : smurfs) {
            currentTotalEnergy += smurf.getEnergy();
            maxPossibleEnergy += village.getDynamicMaxEnergy(smurf);
        }

        // Calculate exhaustion percentage (0.0 = completely full, 1.0 = entirely exhausted)
        double exhaustionFactor = 1.0 - ((double) currentTotalEnergy / maxPossibleEnergy);

        // Base chance scales with population density, multiplied by exhaustion crankiness!
        double baseDensityChance = 0.20 + ((population - 5) * 0.10); // 5 Smurfs = 20%, 6 = 30%, etc.
        double finalDramaChance = Math.clamp(baseDensityChance + (exhaustionFactor * 0.40), 0.0, 0.90);

        List<ResourceEffect> resourceEffects = new ArrayList<>();

        // Roll the dice through your game loop randomness engine
        if (GameRandomness.rollChance(finalDramaChance)) {
            // A fight breaks out! Slashing village social harmony counters
            village.updateResource(ResourceType.MORAL, -1);
            resourceEffects.add(new ResourceEffect(ResourceType.MORAL, -1));

            return new ConsumptionRuleResult(
                "Tensions Démographiques",
                resourceEffects,
                true,
                String.format("⚠️ DISPUTE : Des Schtroumpfs surmenés et fatigués se sont battus dans le village ! (-1 Moral, Risque était de %.0f%%)", finalDramaChance * 100)
            );
        }

        // Peace prevailed this month
        return new ConsumptionRuleResult(
            "Tensions Démographiques",
            List.of(),
            false,
            ""
        );
    }
}
