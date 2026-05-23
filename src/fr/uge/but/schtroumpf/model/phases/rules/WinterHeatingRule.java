package fr.uge.but.schtroumpf.model.phases.rules;

import java.util.ArrayList;
import java.util.List;
import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;
import fr.uge.but.schtroumpf.model.characters.Effect;

public class WinterHeatingRule implements ConsumptionRule {

    @Override
    public ConsumptionRuleResult evaluate(SmurfVillage village, int turnNumber) {
        // Calculate season index (0: Spring, 1: Summer, 2: Autumn, 3: Winter)
        int seasonIndex = ((turnNumber - 1) / 3) % 4;
        boolean isWinter = (seasonIndex == 3);

        // This rule remains dormant during Spring, Summer, and Autumn
        if (!isWinter) {
            return new ConsumptionRuleResult("Chauffage Hivernal", List.of(), false, "");
        }

        int currentDefense = village.getResourceQuantity(ResourceType.DEFENSE);
        int defenseRequired = 1; // Flat upkeep baseline to stay within the max 10 storage cap

        List<Effect> appliedEffects = new ArrayList<>();

        if (currentDefense >= defenseRequired) {
            // Nominal operation path: burn defense fuel
            village.updateResource(ResourceType.DEFENSE, -defenseRequired);
            appliedEffects.add(new Effect(ResourceType.DEFENSE, -defenseRequired));

            return new ConsumptionRuleResult(
                "Defense Hivernal",
                appliedEffects,
                false,
                ""
            );
        } else {
            // Freezing Crisis path: No defense remaining during winter months
            if (currentDefense > 0) {
                village.setResourceQuantity(ResourceType.DEFENSE, 0);
                appliedEffects.add(new Effect(ResourceType.DEFENSE, -currentDefense));
            }

            // Severe penalties to village overall spirit and harmony due to frostbite
            int moralPenalty = -2;
            village.updateResource(ResourceType.MORAL, moralPenalty);
            appliedEffects.add(new Effect(ResourceType.MORAL, moralPenalty));

            return new ConsumptionRuleResult(
                "Chauffage Hivernal",
                appliedEffects,
                true,
                "⚠️ CRISE DE GEL : Le village n'a plus de Defense pour se defendre cet hiver glaciale ! Les Schtroumpfs gèlent dans leurs chaumières. (-2 Moral)"
            );
        }
    }
}
