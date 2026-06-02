package fr.uge.but.schtroumpf.model.phases.rules;

import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.utils.GameRandomness;

public class FoodRule implements ConsumptionRule {
    @Override
    public ConsumptionRuleResult evaluate(SmurfVillage village, int turnNumber) {
        int population = village.getAvailableSmurfs().size();
        
        // 1 berries for 1 smurf
        int foodRequired = GameRandomness.randomChoice(1, population / 2 + 1);
        if (foodRequired == 0) {
            return new ConsumptionRuleResult("Rationnement Alimentaire", List.of(), "");
        }
        
        int currentBerries = village.getResourceQuantity(ResourceType.BERRIES);

        List<ResourceEffect> appliedEffects = new ArrayList<>();

        // normal decrease resources
        if (currentBerries >= foodRequired) {
            village.updateResource(ResourceType.BERRIES, -foodRequired);
            appliedEffects.add(new ResourceEffect(ResourceType.BERRIES, -foodRequired));

            return new ConsumptionRuleResult("Rationnement Alimentaire", appliedEffects, "");
        } else {
        	// if no more berries then apply more penalties
            village.setResourceQuantity(ResourceType.BERRIES, 0);
            appliedEffects.add(new ResourceEffect(ResourceType.BERRIES, -currentBerries));
            
            // reduce gold and moral
            village.updateResource(ResourceType.MORAL, -2);
            appliedEffects.add(new ResourceEffect(ResourceType.MORAL, -2));
            village.updateResource(ResourceType.GOLD, -3);
            appliedEffects.add(new ResourceEffect(ResourceType.GOLD, -3));

            return new ConsumptionRuleResult(
                "Rationnement Alimentaire",
                appliedEffects,
                "Le village a manqué de Baies pour nourrir la population et les prix augmentent !"
            );
        }
    }
}
