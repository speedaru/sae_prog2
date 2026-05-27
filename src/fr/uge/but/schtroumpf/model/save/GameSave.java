package fr.uge.but.schtroumpf.model.save;

import java.util.List;
import fr.uge.but.schtroumpf.model.Game.GameState;
import fr.uge.but.schtroumpf.model.characters.SmurfType;
import fr.uge.but.schtroumpf.model.crises.CrisisType;
import fr.uge.but.schtroumpf.model.phases.GamePhaseType;
import fr.uge.but.schtroumpf.model.types.EventHistory;
import fr.uge.but.schtroumpf.model.types.ResourceMap;

/** data object representing state of a save file */
public record GameSave(
    EngineState engineState,
    VillageState villageState
) {
    public record EngineState(
        int currentRound,
        GameState gameState,
        GamePhaseType currentPhase
    ) {}

    public record VillageState(
        int abilitiesUsedThisTurn,
        ResourceMap currentResources,
        ResourceMap previousRoundResources,
		List<CouncilMemberState> councilMembers,
		List<EventHistory> eventsHistory,
        List<CrisisState> activeCrises,
        VillageModifierCtxState modifiers
    ) {}

    public record CouncilMemberState(
        SmurfType type,
        int currentEnergy
    ) {}

    public record CrisisState(
		CrisisType type
    ) {}
    
    public record VillageModifierCtxState(
		 double successChanceBonus,
		 int energyRechargeRateDelta,
		 int maxEnergyDelta,
		 double efficiencyMultiplier,
		 boolean passiveFoodProductionBlocked
	) {}
}
