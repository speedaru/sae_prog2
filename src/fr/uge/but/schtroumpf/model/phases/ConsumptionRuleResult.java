package fr.uge.but.schtroumpf.model.phases;

import java.util.List;

import fr.uge.but.schtroumpf.model.characters.ResourceEffect;

public record ConsumptionRuleResult(
	String ruleName,
	List<ResourceEffect> effectsApplied,
	boolean crisisTriggered,
	String crisisMessage
) {}
