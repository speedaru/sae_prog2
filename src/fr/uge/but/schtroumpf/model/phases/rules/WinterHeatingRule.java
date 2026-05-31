package fr.uge.but.schtroumpf.model.phases.rules;

import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.Season;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;

public class WinterHeatingRule implements ConsumptionRule {
	private static String TITLE = "Chauffage Hivernal";

    @Override
    public ConsumptionRuleResult evaluate(SmurfVillage village, int turnNumber) {
        boolean isWinter = Season.getSeason(turnNumber) == Season.WINTER;

        if (!isWinter) {
            return new ConsumptionRuleResult(TITLE, List.of(), false, "");
        }

        int currentDefense = village.getResourceQuantity(ResourceType.DEFENSE);
        int defenseRequired = 1;

        List<ResourceEffect> appliedEffects = new ArrayList<>();

        if (currentDefense >= defenseRequired) {
        	// normal, consumes defense
            village.updateResource(ResourceType.DEFENSE, -defenseRequired);
            appliedEffects.add(new ResourceEffect(ResourceType.DEFENSE, -defenseRequired));

            return new ConsumptionRuleResult(
                TITLE,
                appliedEffects,
                false,
                ""
            );
        } else {
        	// no defense left, make moral go down
            if (currentDefense > 0) {
                village.setResourceQuantity(ResourceType.DEFENSE, 0);
                appliedEffects.add(new ResourceEffect(ResourceType.DEFENSE, -currentDefense));
            }

            int moralPenalty = -2;
            village.updateResource(ResourceType.MORAL, moralPenalty);
            appliedEffects.add(new ResourceEffect(ResourceType.MORAL, moralPenalty));

            return new ConsumptionRuleResult(
                TITLE,
                appliedEffects,
                true,
                "⚠️ CRISE DE GEL : Le village n'a plus de Defense pour se defendre cet hiver glaciale ! Les Schtroumpfs gèlent dans leurs chaumières. (-2 Moral)"
            );
        }
    }
}
