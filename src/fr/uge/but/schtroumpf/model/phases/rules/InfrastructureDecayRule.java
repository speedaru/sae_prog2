package fr.uge.but.schtroumpf.model.phases.rules;

import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public class InfrastructureDecayRule implements ConsumptionRule {
	private static String TITLE = "Maintenance des Infrastructures";

    @Override
    public ConsumptionRuleResult evaluate(SmurfVillage village, int turnNumber) {
        int currentTools = village.getResourceQuantity(ResourceType.TOOLS);
        int toolsRequired = 1;

        List<ResourceEffect> appliedEffects = new ArrayList<>();

        // random chance to consume tools
        double baseTriggerOdds = 0.5;

        // check for false bcs its negative effect, so more luck should mean higher fail rate
        if (!village.rollChance(baseTriggerOdds)) {
			return new ConsumptionRuleResult(TITLE, List.of(), false, "");
        }
        
        // consume tools
        
        if (currentTools >= toolsRequired) {
        	// normal
            village.updateResource(ResourceType.TOOLS, -toolsRequired);
            appliedEffects.add(new ResourceEffect(ResourceType.TOOLS, -toolsRequired));

            return new ConsumptionRuleResult(
            	TITLE,
                appliedEffects,
                false,
                ""
            );
        } else {
        	// no more tools so apply other penalites
            if (currentTools > 0) {
                village.setResourceQuantity(ResourceType.TOOLS, 0);
                appliedEffects.add(new ResourceEffect(ResourceType.TOOLS, -currentTools));
            }

            int defensePenalty = -2;
            village.updateResource(ResourceType.DEFENSE, defensePenalty);
            appliedEffects.add(new ResourceEffect(ResourceType.DEFENSE, defensePenalty));

            return new ConsumptionRuleResult(
            	TITLE,
                appliedEffects,
                true,
                "⚠️ LABRELEMENT : Faute d'Outils, les palissades du village s'effondrent et tombent en ruine ! (-2 Défense)"
            );
        }
    }
}
