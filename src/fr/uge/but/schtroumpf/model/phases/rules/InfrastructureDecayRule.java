package fr.uge.but.schtroumpf.model.phases.rules;

import java.util.ArrayList;
import java.util.List;
import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.phases.ConsumptionRuleResult;
import fr.uge.but.schtroumpf.model.characters.ResourceEffect;

public class InfrastructureDecayRule implements ConsumptionRule {

    @Override
    public ConsumptionRuleResult evaluate(SmurfVillage village, int turnNumber) {
        int currentTools = village.getResourceQuantity(ResourceType.TOOLS);
        int toolsRequired = 1; // Continuous constant maintenance cost

        List<ResourceEffect> appliedEffects = new ArrayList<>();

        if (currentTools >= toolsRequired) {
            // Nominal operation path: consume tools for standard structural upkeep
            village.updateResource(ResourceType.TOOLS, -toolsRequired);
            appliedEffects.add(new ResourceEffect(ResourceType.TOOLS, -toolsRequired));

            return new ConsumptionRuleResult(
                "Maintenance des Infrastructures",
                appliedEffects,
                false,
                ""
            );
        } else {
            // Infrastructure Decay path: out of tools to maintain fences and bridges
            if (currentTools > 0) {
                village.setResourceQuantity(ResourceType.TOOLS, 0);
                appliedEffects.add(new ResourceEffect(ResourceType.TOOLS, -currentTools));
            }

            // Severe physical penalty: the village fortifications crumble away
            int defensePenalty = -2;
            village.updateResource(ResourceType.DEFENSE, defensePenalty);
            appliedEffects.add(new ResourceEffect(ResourceType.DEFENSE, defensePenalty));

            return new ConsumptionRuleResult(
                "Maintenance des Infrastructures",
                appliedEffects,
                true,
                "⚠️ LABRELEMENT : Faute d'Outils, les palissades du village s'effondrent et tombent en ruine ! (-2 Défense)"
            );
        }
    }
}
