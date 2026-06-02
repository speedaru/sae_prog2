package fr.uge.but.schtroumpf.model.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import fr.uge.but.schtroumpf.model.Game;
import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.CharacterAttribute;
import fr.uge.but.schtroumpf.model.characters.SmurfCharacter;
import fr.uge.but.schtroumpf.model.crises.Crisis;
import fr.uge.but.schtroumpf.model.save.GameSave.CouncilMemberState;
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

    public static Path getSaveFilePath(String fileName) {
        return getSaveFile(fileName);
    }
    
    public static void saveGame(Game game, String saveName) throws IOException {
        saveFile(saveName, serializeGame(game));
    }

    public static Game loadGame(String saveName) {
        GameSave save = loadFile(saveName);
        return deserializeGame(save);
    }
    
    public static GameSave getGameSave(String saveName) {
        return loadFile(saveName);
    }

//    public static Game loadGame(Path saveFile) {
//        GameSave save = loadFile(saveFile);
//        return deserializeGame(save);
//    }

    /** serialize Game and output json file in a specified file path */
    public static GameSave serializeGame(Game game) {
        SmurfVillage village = game.getVillage();

        GameSave.EngineState engineState = serialEngine(game);
        GameSave.VillageState villageState = serializeVillage(village);

        return new GameSave(engineState, villageState);
    }

    /** deserializes a file from a path into a Game instance */
    public static Game deserializeGame(GameSave save) {
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

    /** @return a list of available save names */
    public static List<String> getSaveNames() {
        List<String> cleanNames = new ArrayList<>();
        if (!Files.exists(SAVE_PATH)) {
            return cleanNames;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(SAVE_PATH, "save_*.json")) {
            for (Path entry : stream) {
                String filename = entry.getFileName().toString();
                // remove save_ suffix and .json extension
                if (filename.length() > 10) {
                    String cleanName = filename.substring(5, filename.length() - 5);
                    cleanNames.add(cleanName);
                }
            }
        } catch (IOException e) {
            Logger.LogError("failed to read save files: %s", e.getMessage());
        }

        return cleanNames;
    }
    
    public static SmurfCharacter deserializeCouncilMember(CouncilMemberState state) {
    	SmurfCharacter smurf = SmurfCharacter.fromType(state.type());
    	smurf.setEnergy(state.currentEnergy());
    	
    	// set attribs
    	for (GameSave.AttributeState attrib : state.attribs()) {
			smurf.setAttribute(attrib.type(), attrib.value());
    	}
    	
    	return smurf;
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
        	// get attributes
        	ArrayList<GameSave.AttributeState> attribs = new ArrayList<>();
        	for (CharacterAttribute attrib : CharacterAttribute.values()) {
        		int val = smurf.getAttribute(attrib);
        		if (val != 0) {
					attribs.add(new GameSave.AttributeState(attrib, val));
        		}
        	}
        	
        	councilState.add(new GameSave.CouncilMemberState(
				smurf.getType(),
				smurf.getEnergy(),
				List.copyOf(attribs)
        	));
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
    
//    private static GameSave loadFile(Path file) {
//		try {
//			return objectMapper.readValue(file.toFile(), GameSave.class);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return null;
//    }

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