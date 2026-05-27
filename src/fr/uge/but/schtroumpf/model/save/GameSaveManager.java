package fr.uge.but.schtroumpf.model.save;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
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
import fr.uge.but.schtroumpf.model.types.VillageModifierContext;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class GameSaveManager {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void init() {
        // enable tab indenting in json dump
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /** serialize Game and outputs json file in a specified file path */
    public static void serializeGame(Game game, Path targetPath) throws IOException {
        SmurfVillage village = game.getVillage();

        GameSave.EngineState engineState = serialEngine(game);
        GameSave.VillageState villageState = serializeVillage(village);

        // Assemble unified contract and save to disk
        GameSave saveFileContent = new GameSave(engineState, villageState);
        objectMapper.writeValue(targetPath.toFile(), saveFileContent);
    }

    /**
     * Deserializes a file from a path back into an initialized operational Game loop model.
     */
    public static Game deserializeGame(Path sourcePath) {
        // read json file into game save struct
        GameSave save;
		try {
			save = objectMapper.readValue(sourcePath.toFile(), GameSave.class);

			Game game = new Game();
			SmurfVillage village = game.getVillage();

			game.loadSave(save);
			village.loadSave(save.villageState());

			return game;
		} catch (StreamReadException e) {
			e.printStackTrace();
		} catch (DatabindException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		Logger.LogError("failed to load save");
		return null;
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
        GameSave.VillageModifierCtxState modifiersState = serializeModifiers(village.getModifiers());
        
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
    
    private static GameSave.VillageModifierCtxState serializeModifiers(VillageModifierContext modifiers) {
    	return new GameSave.VillageModifierCtxState(
    		modifiers.getSuccessChanceBonus(),
    		modifiers.getEnergyRechargeRateDelta(),
    		modifiers.getMaxEnergyDelta(),
    		modifiers.getEfficiencyMultiplier(),
    		modifiers.isPassiveFoodProductionBlocked()
    	);
    }
    
    // ------------------------- helpers
    
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