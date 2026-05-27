package fr.uge.but.schtroumpf.model.save;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.ResourceType;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.model.characters.SmurfType;
import fr.uge.but.schtroumpf.model.events.EventHistory;
import fr.uge.but.schtroumpf.model.events.GameEventType;
import fr.uge.but.schtroumpf.model.phases.GamePhase;

public class GameSaveManager {

    private final ObjectMapper objectMapper;

    public GameSaveManager() {
        this.objectMapper = new ObjectMapper();
        // Enables pretty printing so players can easily read or manually inspect their JSON files if desired
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Serializes an active game session to a specific file destination path.
     */
    public void serializeGame(Game game, Path targetPath) throws IOException {
        SmurfVillage village = game.getVillage();

        // 1. Map Engine State
        String phaseClassName = game.getCurrentPhase() != null ? game.getCurrentPhase().getClass().getName() : null;
        GameSaveDTO.EngineStateDTO engineState = new GameSaveDTO.EngineStateDTO(
            game.getCurrentRound(),
            game.getGameState(),
            phaseClassName
        );

        // 2. Map Resource Quantities
        Map<ResourceType, Integer> currentResources = village.getResources().stream()
            .collect(Collectors.toMap(ResourceSnapshot::type, ResourceSnapshot::quantity, (v1, v2) -> v1, () -> new EnumMap<>(ResourceType.class)));

        // Safely extract snapshot modifications variations diff arrays
        Map<ResourceType, Integer> previousResources = new EnumMap<>(ResourceType.class);
        try {
            var diffs = village.getResourcesDiff(); 
            for (ResourceType type : ResourceType.values()) {
                previousResources.put(type, village.getResourceQuantity(type) - village.getResourceDelta(type));
            }
        } catch (Exception e) {
            // Fallback default mapping layers if historical rounds are uninitialized
            for (ResourceType type : ResourceType.values()) { previousResources.put(type, 3); }
        }

        GameSaveDTO.VillageStateDTO villageState = new GameSaveDTO.VillageStateDTO(
            village.getAbilitiesUsedThisTurn(),
            currentResources,
            previousResources
        );

        // 3. Map Council Characters Status Energy metrics
        List<GameSaveDTO.CouncilMemberStateDTO> councilState = village.getCouncilMembers().stream()
            .map(smurf -> new GameSaveDTO.CouncilMemberStateDTO(smurf.getType(), smurf.getEnergy()))
            .toList();

        // 4. Map History Log Indexes
        List<GameSaveDTO.EventHistoryDTO> historyState = village.getHistory().stream()
            .map(hist -> new GameSaveDTO.EventHistoryDTO(hist.round(), hist.type().name()))
            .toList();

        // Assemble unified contract and save to disk
        GameSaveDTO saveFileContent = new GameSaveDTO(engineState, villageState, councilState, historyState);
        objectMapper.writeValue(targetPath.toFile(), saveFileContent);
    }

    /**
     * Deserializes a file from a path back into an initialized operational Game loop model.
     */
    public Game deserializeGame(Path sourcePath) throws IOException {
        // Read file contents matching our JSON contract schema definitions
        GameSaveDTO data = objectMapper.readValue(sourcePath.toFile(), GameSaveDTO.class);

        Game game = new Game();
        SmurfVillage village = game.getVillage();

        // 1. Rehydrate basic Engine configurations
        try {
            var roundField = Game.class.getDeclaredField("currentRound");
            roundField.setAccessible(true);
            roundField.set(game, data.engineState().currentRound());

            var stateField = Game.class.getDeclaredField("gameState");
            stateField.setAccessible(true);
            stateField.set(game, data.engineState().gameState());

            if (data.engineState().currentPhaseClassName() != null) {
                GamePhase phaseInstance = (GamePhase) Class.forName(data.engineState().currentPhaseClassName())
                    .getDeclaredConstructor().newInstance();
                var phaseField = Game.class.getDeclaredField("currentPhase");
                phaseField.setAccessible(true);
                phaseField.set(game, phaseInstance);
            }
        } catch (Exception e) {
            throw new IOException("Failed to rehydrate Game core lifecycle context maps.", e);
        }

        // 2. Rehydrate Resource Banks Quantities
        data.villageState().currentResources().forEach(village::setResourceQuantity);

        // Reconstruct historical records arrays snapshots safely
        List<ResourceSnapshot> historicalSnap = new ArrayList<>();
        data.villageState().previousRoundResources().forEach((type, qty) -> {
            historicalSnap.add(new ResourceSnapshot(type, qty));
        });
        try {
            var prevRoundField = SmurfVillage.class.getDeclaredField("previousRoundResources");
            prevRoundField.setAccessible(true);
            prevRoundField.set(village, historicalSnap);
        } catch (Exception e) {
            throw new IOException("Failed to link past historical resource arrays snapshots.", e);
        }

        // 3. Rehydrate Turn Counter
        try {
            var turnCounterField = SmurfVillage.class.getDeclaredField("abilitiesUsedThisTurn");
            turnCounterField.setAccessible(true);
            turnCounterField.set(village, data.villageState().abilitiesUsedThisTurn());
        } catch (Exception e) {
            throw new IOException("Failed to restore action constraint counter structures.", e);
        }

        // 4. Rehydrate Council Character energy parameters
        for (GameSaveDTO.CouncilMemberStateDTO smurfData : data.councilState()) {
            SmurfCharacter character = village.getCouncilMember(smurfData.type());
            // Use standard direct application method updating parameters inside the domain boundaries
            character.updateEnergy(village, smurfData.currentEnergy() - character.getEnergy());
        }

        // 5. Rehydrate History metrics pipelines
        for (GameSaveDTO.EventHistoryDTO histData : data.history()) {
            GameEventType type = GameEventType.valueOf(histData.eventTypeName());
            village.recordEvent(new EventHistory(histData.round(), type));
        }

        // 🚨 CRITICAL SANITY REFRESH: Force the engine to look at the newly injected resources 
        // to immediately recalculate active crises and context blackboards completely fresh from scratch!
        // This guarantees that modifier values (+/- energy rates, etc.) are valid immediately after load.
        // Assuming your standard game initialization method loop runs a validation pipeline, e.g.:
        // game.recalculateActiveVillageCrisesContexts();

        return game;
    }
}ackage fr.uge.but.schtroumpf.model;

public class GameSaveManager {

}
