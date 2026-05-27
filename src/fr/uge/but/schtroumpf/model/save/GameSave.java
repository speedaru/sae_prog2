package fr.uge.but.schtroumpf.model.save;

import java.util.List;
import java.util.Map;
import fr.uge.but.schtroumpf.model.Game.GameState;
import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.model.characters.SmurfType;

/** data object representing state of a save file */
public record GameSave(
    EngineState engineState,
    VillageState villageState,
    List<CouncilMemberState> councilState,
    List<EventHistory> history
) {
    public record EngineState(
        int currentRound,
        GameState gameState,
        String currentPhaseClassName
    ) {}

    public record VillageState(
        int abilitiesUsedThisTurn,
        Map<ResourceType, Integer> currentResources,
        Map<ResourceType, Integer> previousRoundResources
    ) {}

    public record CouncilMemberState(
        SmurfType type,
        int currentEnergy
    ) {}

    public record EventHistory(
        int round,
        String eventTypeName
    ) {}
}
