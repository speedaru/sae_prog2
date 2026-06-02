package fr.uge.but.schtroumpf.model.phases;

import java.util.List;

public record ConsumptionReport(
	int turnNumber,
	Season season,
	List<ConsumptionRuleResult> ruleResults,
	List<String> activeCrises
) {
	public boolean hasAnyCrisis() {
		return !activeCrises.isEmpty();
	}
}
