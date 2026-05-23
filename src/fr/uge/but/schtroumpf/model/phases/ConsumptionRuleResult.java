package fr.uge.but.schtroumpf.model.phases;

import java.util.List;

import fr.uge.but.schtroumpf.model.characters.Effect;

public record ConsumptionRuleResult(
	String ruleName,
	List<Effect> effectsApplied,
	boolean crisisTriggered,
	String crisisMessage
) {}
