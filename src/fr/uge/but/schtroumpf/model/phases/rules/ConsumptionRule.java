package fr.uge.but.schtroumpf.model.phases.rules;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;;

@FunctionalInterface
public interface ConsumptionRule {
    /**
     * each rule checks what effects it should trigger based on village state
     * @return object that contains a list of effects to trigger and other ui stuff
     */
    ConsumptionRuleResult evaluate(SmurfVillage village, int turnNumber);
}
