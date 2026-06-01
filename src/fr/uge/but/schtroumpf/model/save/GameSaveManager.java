package fr.uge.but.schtroumpf.model.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.model.crises.Crisis;
import fr.uge.but.schtroumpf.model.types.EventHistory;
import fr.uge.but.schtroumpf.model.types.ResourceMap;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class GameSaveManager {
    private static final Path SAVE_PATH = Path.of("saves/");
    private static final String SAVE_FILE_FMT = "save_%s.json";

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void init() {
        // enable tab indenting in json dump
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /** serialize Game and output json file in a specified file path */
    public static void saveGame(Game game, String saveName) throws IOException {
        SmurfVillage village = game.getVillage();

        GameSave.EngineState engineState = serialEngine(game);
        GameSave.VillageState villageState = serializeVillage(village);

        GameSave saveFileContent = new GameSave(engineState, villageState);
        saveFile(saveName, saveFileContent);
    }

    /** deserializes a file from a path into a Game instance */
    public static Game loadGame(String saveName) {
        GameSave save = loadFile(saveName);
        if (save == null) {
			Logger.LogError("failed to load save");
			return null;
        }

		Game game = new Game();
		SmurfVillage village = game.getVillage();

		game.loadSave(save);
		village.loadSave(save.villageState());
		
		return game;
    }
    
    private static GameSave.EngineState serialEngine(Game game) {
        return new GameSave.EngineState(
			game.getCurrentRound(),
			game.getGameState(),
			game.getCurrentPhase().getType()
        );
    }
    
    private static GameSave.VillageState serializeVillage(SmurfVillage village) {
    	// resources
    	ResourceMap currentResources = resourcesSnapToMap(village.getResources());
    	ResourceMap previousResources = resourcesSnapToMap(village.getPreviousRoundResources());

        // council members
        List<GameSave.CouncilMemberState> councilState = serializeCouncilMembers(village.getCouncilMembers());;
        
        // events history
        List<EventHistory> historyState = village.getEventsHistory();

        // active crises
        List<GameSave.CrisisState> crises = new ArrayList<>();
        for (Crisis crisis : village.getActiveCrises()) {
        	GameSave.CrisisState save = new GameSave.CrisisState(
				crisis.getType()
			);
        	crises.add(save);
        }

        // game modifiers
        GameSave.VillageModifierCtxState modifiersState = village.getModifiersView().serialize();
        
        return new GameSave.VillageState(
        	village.getAbilitiesUsedThisTurn(),
        	currentResources,
        	previousResources,
        	councilState,
        	historyState,
        	crises,
        	modifiersState
        );
    }
    
    private static List<GameSave.CouncilMemberState> serializeCouncilMembers(List<SmurfCharacter> councilMembers) {
        ArrayList<GameSave.CouncilMemberState> councilState = new ArrayList<>();
        
        for (SmurfCharacter smurf : councilMembers) {
        	councilState.add(new GameSave.CouncilMemberState(smurf.getType(), smurf.getEnergy()));
        }
        
        return List.copyOf(councilState);
    }
    
    // ------------------------- helpers
    
    private static Path getSaveFile(String fileName) {
    	return SAVE_PATH.resolve(String.format(SAVE_FILE_FMT, fileName)).toAbsolutePath();
    }
    
    private static void saveFile(String fileName, GameSave data) {
    	Path file = getSaveFile(fileName);

        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

			objectMapper.writeValue(file.toFile(), data);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private static GameSave loadFile(String fileName) {
    	Path file = getSaveFile(fileName);

		try {
			return objectMapper.readValue(file.toFile(), GameSave.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
    }

    private static ResourceMap resourcesSnapToMap(List<ResourceSnapshot> snapshots) {
        ResourceMap map = new ResourceMap();
        
        for (ResourceSnapshot snap : snapshots) {
        	if (map.containsKey(snap.type())) {
        		throw new IllegalStateException(String.format("resource %s already exists in map", snap.type()));
        	}
        	map.put(snap.type(), snap.quantity());
        }
        
        return map;
    }
}