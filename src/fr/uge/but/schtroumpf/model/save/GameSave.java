package fr.uge.but.schtroumpf.model.save;

import java.util.List;
import java.util.Map;

import fr.uge.but.schtroumpf.model.Game.GameState;
import fr.uge.but.schtroumpf.model.characters.CharacterAttribute;
import fr.uge.but.schtroumpf.model.characters.SmurfType;
import fr.uge.but.schtroumpf.model.crises.CrisisType;
import fr.uge.but.schtroumpf.model.phases.GamePhaseType;
import fr.uge.but.schtroumpf.model.types.EventHistory;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
import fr.uge.but.schtroumpf.model.types.ResourceMap;

/** represents state of save file */
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
        int currentEnergy,
        List<AttributeState> attribs
    ) {}
    
    public record AttributeState(
    	CharacterAttribute type,
    	int value
    ) {}

    public record CrisisState(
		CrisisType type
    ) {}
    
	public record VillageModifierCtxState(
        Map<GameModifierType, Object> persistentModifiers,
        List<TemporaryModifierState> temporaryModifiers
    ) {}

    public record TemporaryModifierState(
        GameModifierType type,
        Object value,
        int remainingRounds,
        boolean isCrisis,
        boolean started
    ) {}
}
