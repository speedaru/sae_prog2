package fr.uge.but.schtroumpf.model.phases.rules;

import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class FoodRule implements ConsumptionRule {
    @Override
    public ConsumptionRuleResult evaluate(SmurfVillage village, int turnNumber) {
        int population = village.getAvailableSmurfs().size();
        
        // Balanced scaling consumption step rate to respect the max 10 storage cap
        int foodRequired = Math.max(1, population / 3);
        int currentBerries = village.getResourceQuantity(ResourceType.BERRIES);

        List<ResourceEffect> appliedEffects = new ArrayList<>();

        if (currentBerries >= foodRequired) {
            // Normal operation path: deduct resource stock
            village.updateResource(ResourceType.BERRIES, -foodRequired);
            appliedEffects.add(new ResourceEffect(ResourceType.BERRIES, -foodRequired));

            return new ConsumptionRuleResult(
                "Rationnement Alimentaire",
                appliedEffects,
                false,
                ""
            );
        } else {
            // Famine strike path: drain whatever partial berries are remaining to 0
            village.setResourceQuantity(ResourceType.BERRIES, 0);
            appliedEffects.add(new ResourceEffect(ResourceType.BERRIES, -currentBerries));
            
            // Instantly penalize village state parameters due to starvation 
            village.updateResource(ResourceType.MORAL, -2);
            appliedEffects.add(new ResourceEffect(ResourceType.MORAL, -2));
            village.updateResource(ResourceType.GOLD, -3);
            appliedEffects.add(new ResourceEffect(ResourceType.GOLD, -3));

            return new ConsumptionRuleResult(
                "Rationnement Alimentaire",
                appliedEffects,
                true,
                "⚠️ FAMINE : Le village a manqué de Baies pour nourrir la population et les prix augmentent ! (-3 Or, -2 Moral)"
            );
        }
    }
}
