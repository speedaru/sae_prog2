package fr.uge.but.schtroumpf.model.phases;

import java.util.List;

import fr.uge.but.schtroumpf.model.types.ResourceEffect;

public record ConsumptionRuleResult(
	String ruleName,
	List<ResourceEffect> effectsApplied,
	String feedbackMessage
) {}
