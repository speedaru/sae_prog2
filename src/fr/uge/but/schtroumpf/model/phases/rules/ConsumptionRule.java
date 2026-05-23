package fr.uge.but.schtroumpf.model.phases.rules;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;;

@FunctionalInterface
public interface ConsumptionRule {
    /**
     * Evaluates a single specific consumption constraint.
     * @return A result object containing resource changes and any triggered crises.
     */
    ConsumptionRuleResult evaluate(SmurfVillage village, int turnNumber);
}
