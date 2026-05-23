package fr.uge.but.schtroumpf.model.phases;

import java.util.List;

public record ConsumptionReport(
	int turnNumber,
	String seasonName,
	List<ConsumptionRuleResult> ruleResults, // Dynamic tracking list
	List<String> activeCrises                // Collects "FAMINE", "FREEZING", "DECAY", etc.
) {
	public boolean hasAnyCrisis() {
		return !activeCrises.isEmpty();
	}
}
