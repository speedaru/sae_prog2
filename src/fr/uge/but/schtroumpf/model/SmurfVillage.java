package fr.uge.but.schtroumpf.model;

import module java.base;

import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.*;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.crises.*;
import fr.uge.but.schtroumpf.model.save.GameSave;
import fr.uge.but.schtroumpf.model.types.EventHistory;
import fr.uge.but.schtroumpf.model.types.ModifierEffect;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.types.VillageCallbackType;
import fr.uge.but.schtroumpf.model.types.VillageModifierContext;
import fr.uge.but.schtroumpf.model.utils.GameRandomness;
import fr.uge.but.schtroumpf.model.utils.Logger;

public class SmurfVillage {
	public final static int MAX_CRISES = 3; // 3+ crises = lose
	public final static int BASE_ABILITIES_PER_TURN = 3;
	public final static int BASE_PRODUCTION_RATE = 4;

	private final ResourceManager resourceManager = new ResourceManager();
	private List<ResourceSnapshot> previousRoundResources;
	private List<SmurfCharacter> councilMembers;
	private ArrayList<EventHistory> eventsHistory = new ArrayList<>();
	private int abilitiesUsedThisTurn = 0;
	
	// modifier engine and crisis state
	private final ArrayList<Crisis> activeCrises = new ArrayList<>();
	private VillageModifierContext modifiers = new VillageModifierContext();
	private Map<VillageCallbackType, Runnable> callbacks = new HashMap<>();

	public SmurfVillage() {
		councilMembers = createSmurfs();
	}

	// ------------------------- game saving and loading -------------------------
	
	public void loadSave(GameSave.VillageState state) {
		abilitiesUsedThisTurn = state.abilitiesUsedThisTurn();
		
		// current resources
		for (var entry : state.currentResources().entrySet()) {
			resourceManager.set(entry.getKey(), entry.getValue());
		}

		// previous resources
		ResourceManager previousRoundResourceManager = new ResourceManager(state.previousRoundResources());
		previousRoundResources = previousRoundResourceManager.getResourcesSnap();
		
		// council members
		councilMembers = new ArrayList<>();
		for (var savedMember : state.councilMembers()) {
			SmurfCharacter newMember = SmurfCharacter.fromType(savedMember.type());
			councilMembers.add(newMember);
			newMember.setEnergy(savedMember.currentEnergy());
		}
		
		// event history
		loadEventsHistory(state.eventsHistory());
		
		// load crises
		activeCrises.clear();
		for (GameSave.CrisisState savedCrisis : state.activeCrises()) {
			activeCrises.add(Crisis.fromType(savedCrisis.type()));
		}
		
		// load modifiers
		modifiers = new VillageModifierContext(state.modifiers());
	}
	
	// ------------------------- resource management -------------------------

	public List<ResourceSnapshot> getResources() {
		return resourceManager.getResourcesSnap();
	}

	public int getResourceQuantity(ResourceType resourceType) {
		return resourceManager.get(resourceType);
	}
	
	public boolean isResourceFull(ResourceType resourceType) {
		return resourceManager.get(resourceType) == ResourceManager.MAX_QUANTITY;
	}

	public void updateResource(ResourceType resource, int amount) {
		resourceManager.add(resource, amount);
	}

	public void setResourceQuantity(ResourceType resource, int amount) {
		resourceManager.set(resource, amount);
	}

	/** replaced by the hooked version in modifier engine */
//    public void applyEffects(List<ResourceEffect> resourceEffects) {
//        for (ResourceEffect effect : resourceEffects) {
//            updateResource(effect.resourceType(), effect.delta());
//        }
//    }

	public boolean verifyResources(List<ResourceSnapshot> resourcesRequired) {
		return resourcesRequired.stream().allMatch(req -> resourceManager.get(req.type()) >= req.quantity());
	}

	// ------------------------- smurf council management -------------------------

	public List<SmurfCharacter> getCouncilMembers() {
		return List.copyOf(councilMembers);
	}

	public List<SmurfCharacter> getAvailableSmurfs() {
		return councilMembers.stream().filter(smurf -> smurf.getEnergy() >= 1).toList();
	}

	public SmurfCharacter getCouncilMember(SmurfType type) {
		return councilMembers.stream().filter(smurf -> smurf.getType() == type).findFirst()
				.orElseThrow(() -> new IllegalStateException(type + " is not in the council."));
	}

	public AbilityResult executeCouncilMemberAbility(SmurfCharacter smurf, CharacterAbility ability) {
		if (isActionLimitReached()) {
            throw new IllegalStateException("Limite d'actions atteinte pour ce tour.");
        }
		if (!smurf.canExecute(this, ability)) {
			throw new IllegalStateException("Action processing denied: Preconditions unmet.");
		}
		
		// use smurf energy
		smurf.updateEnergy(this, -ability.energyCost());

		// apply effects
		AbilityResult result = ability.actionLogic().apply(this);
		this.applyEffects(result.effectsToApply());
		
		// increase used abilities counter
		this.abilitiesUsedThisTurn++;
		return result;
	}
	
	// ------------------------- smurf abilities -------------------------

    public void resetTurnAbilitiesCounter() {
        this.abilitiesUsedThisTurn = 0;
    }

    public int getAbilitiesUsedThisTurn() {
        return this.abilitiesUsedThisTurn;
    }
    
    public boolean isActionLimitReached() {
        return this.abilitiesUsedThisTurn >= getDynamicMaxAbilitiesPerTurn();
    }

	// ------------------------- round and snapshot tracking -------------------------
    
    public void prepareNextRound() {
        saveRoundResources(); // save resources
        resetTurnAbilitiesCounter(); // reset council member ability counter
		applyActiveCrisesModifiers(); // apply modifiers
    }

	public void saveRoundResources() {
		previousRoundResources = resourceManager.getResourcesSnap();
	}
	
	public List<ResourceSnapshot> getPreviousRoundResources() {
		return previousRoundResources;
	}

	public List<ResourceSnapshot> getResourcesDiff() {
		if (previousRoundResources == null)
			return getResources();

		var currentSnap = resourceManager.getResourcesSnap();
		var diffSnap = new ArrayList<ResourceSnapshot>();

		for (int i = 0; i < currentSnap.size(); i++) {
			ResourceType type = currentSnap.get(i).type();
			int delta = currentSnap.get(i).quantity() - previousRoundResources.get(i).quantity();
			diffSnap.add(new ResourceSnapshot(type, delta));
		}
		return diffSnap;
	}

	public int getResourceDelta(ResourceType resourceType) {
		if (previousRoundResources == null)
			return 0;

		int previousQty = previousRoundResources.stream().filter(snap -> snap.type() == resourceType)
				.map(ResourceSnapshot::quantity).findFirst().orElse(0);

		return resourceManager.get(resourceType) - previousQty;
	}

	// ------------------------- history and events -------------------------

	public void recordEvent(EventHistory recordedEvent) {
		eventsHistory.add(recordedEvent);
	}

	public List<EventHistory> getEventsHistory() {
		return List.copyOf(eventsHistory);
	}

    public EventHistory getEventFromRound(int round) {
    	for (EventHistory eventRecord : eventsHistory) {
    		if (eventRecord.round() == round) {
    			return eventRecord;
    		}
    	}
    	return null;
    }

	public EventHistory getLastEvent() {
		return eventsHistory.isEmpty() ? null : eventsHistory.getLast();
	}

	private void loadEventsHistory(List<EventHistory> history) {
		eventsHistory = new ArrayList<>(history);
		Logger.LogDebug("loaded event history (%d events), last event: %s",
				eventsHistory.size(), eventsHistory.getLast().eventType().name());
	}

	// ------------------------- hooks for modifier engine -------------------------

	/** shouldn't be called to update a modifier manually */
	public VillageModifierContext getModifiersView() {
		return modifiers;
	}
	
	/** get modifier value */
	public <T> T getModifier(GameModifierType type) {
		return modifiers.get(type);
	}
	
	/** replaces GameRandomness.rollChance, applies sucess chance modifier */
	public boolean rollChance(double baseChance) {
		double sucessChanceModifier = modifiers.getDouble(GameModifierType.SUCCESS_CHANCE_BONUS);
		double finalChance = baseChance + sucessChanceModifier;
		finalChance = Math.clamp(finalChance, 0, 1);
		return GameRandomness.rollChance(finalChance);
	}

	public int getDynamicEffectDelta(ResourceEffect effect) {
		int baseDelta = effect.delta();

		// if effect always returns 0 then return 0
		if (baseDelta == 0) {
			return baseDelta;
		}
		
		// apply efficiency modifier
		
		int efficiencyModifier = modifiers.getInt(GameModifierType.EFFICIENCY_DELTA);
		
		// if positive effect then modifier shouldn't reduce delta to 0
		if (baseDelta > 0) {
			return Math.max(1, baseDelta + efficiencyModifier);
		}
		
		return baseDelta + efficiencyModifier;
	}
	
	/** applies effects with efficiency multipliers */
	public void applyEffects(List<ResourceEffect> resourceEffects) {
		for (ResourceEffect effect : resourceEffects) {
			int finalDelta = getDynamicEffectDelta(effect);
			updateResource(effect.resourceType(), finalDelta);
		}
	}

	/** recharge smurf energy with energy recharge rate delta modifier */
	public void rechargeSmurfEnergy(SmurfCharacter smurf, int baseRate) {
		int energyRechargeRateDelta = modifiers.getInt(GameModifierType.ENERGY_RECHARGE_RATE_DELTA);
		int finalRate = baseRate + energyRechargeRateDelta;
		finalRate = Math.max(1, finalRate); // always at least 1

		smurf.updateEnergy(this, finalRate);
		Logger.LogDebug("recharged %s energy by %d (+%d modifier)", smurf, finalRate, energyRechargeRateDelta);
	}
	
	/** get list of resources allowed in production phase (excludes maxed out resorces and stuff) */
	public ArrayList<ResourceType> getProductionAllowedResources() {
		ArrayList<ResourceType> allowedTypes = new ArrayList<>(Arrays.asList(ResourceType.values()));
		
		// dont produce resources that are full
		allowedTypes.removeIf(this::isResourceFull);

		// dont include berries if food production blocked
		if (modifiers.getBool(GameModifierType.PASSIVE_FOOD_PRODUCTION_BLOCKED)) {
			allowedTypes.remove(ResourceType.BERRIES);
		}
		
		return allowedTypes;
	}

	/** determines dynamic max energy of a smurf based on active debuffs */
	public int getDynamicMaxEnergy(SmurfCharacter smurf) {
		int maxEnergyDelta = modifiers.getInt(GameModifierType.MAX_ENERGY_DELTA);
		int finalMax = smurf.getBaseMaxEnergy() + maxEnergyDelta;
		return Math.max(1, finalMax); // always at least 1 max energy
	}
	
	public int getDynamicMaxAbilitiesPerTurn() {
		int abilitiesDelta = modifiers.getInt(GameModifierType.ABILITIES_PER_TURN_DELTA);
		int finalAbilities = abilitiesDelta + BASE_ABILITIES_PER_TURN;
		return Math.max(1, finalAbilities);
	}
	
	/** @return number of resources to gain in the production phase */
	public int getProductionRate() {
		int productionDelta = modifiers.getInt(GameModifierType.PRODUCTION_DELTA);
		int finalProductionRate = BASE_PRODUCTION_RATE + productionDelta;
		return Math.max(1, finalProductionRate);
	}
	
	/** used to apply a modifier, also calls the modifier updated callback */
	public void accumulateTempModifier(ModifierEffect modifier) {
		modifiers.accumulateTempEffect(modifier);
		runCallback(VillageCallbackType.MODIFIERS_UPDATED);
	}
	
	/** used to apply a modifier, also calls the modifier updated callback */
	public void accumulatePersistenModifier(GameModifierType type, Object val) {
		modifiers.accumulatePersistentEffect(type, val);
		runCallback(VillageCallbackType.MODIFIERS_UPDATED);
	}

	// ------------------------- callbacks -------------------------
	
	public void registerCallback(VillageCallbackType callbackType, Runnable callback) {
		if (callbacks.containsKey(callbackType)) {
			throw new IllegalStateException("callback type already registered: " + callbackType.name());
		}
		
		callbacks.put(callbackType, callback);
	}
	
	/** runs a callback for the registered type if a callback is registered */
	public void runCallback(VillageCallbackType callbackType) {
		Runnable callback = callbacks.getOrDefault(callbackType, null);
		if (callback != null) {
			callback.run();
		}
	}

	// ------------------------- crisis and end conditions -------------------------
	
	/** recalculates active crises and modifiers */
	public void applyCrises(List<Crisis> crises) {
		activeCrises.clear();
		
		// block crisis if has shields
		int shieldCount = modifiers.getInt(GameModifierType.CRISIS_SHIELD_COUNT);
		if (shieldCount <= 0) {
			// no shields so just set crises
			activeCrises.addAll(crises);
		}

		// use shields
		List<Crisis> unblockedCrises = blockCrises(shieldCount, crises);
		activeCrises.addAll(unblockedCrises);
	}

	public void applyActiveCrisesModifiers() {
		for (Crisis crisis : activeCrises) {
			// apply modifier effects
			for (ModifierEffect effect : crisis.getModifierEffects()) {
				modifiers.accumulateTempEffect(effect);
			}

			// apply resource changes when exiting phae
			crisis.applyImmediateEffects(this);
		}
		
		// decrease round counter
		modifiers.tickRounds();
		runCallback(VillageCallbackType.MODIFIERS_UPDATED);
	}

	public List<Crisis> getActiveCrises() {
		return List.copyOf(activeCrises);
	}

	/** must be called when crises are up to date to work properly */
	public boolean isDefeated() {
		return activeCrises.size() >= MAX_CRISES;
	}

	// ------------------------- private helpers -------------------------
	
	private List<Crisis> blockCrises(int shieldCount, List<Crisis> crises) {
		ArrayList<Crisis> unblockedCrises = new ArrayList<>();
		int shieldsUsed = 0;

		// if can block all crises 
		if (shieldCount >= crises.size()) {
			shieldsUsed = crises.size();
		}
		else {
			// partially block
			unblockedCrises.addAll(crises.subList(shieldCount, shieldCount));
			shieldsUsed = shieldCount;
		}
		
		// decrease modifiers
		modifiers.accumulatePersistentEffect(GameModifierType.CRISIS_SHIELD_COUNT, -shieldsUsed);

		return List.copyOf(unblockedCrises);
	}
	
	private static List<SmurfCharacter> createSmurfs() {
		return List.of(
			new GrandSmurf(),
			new HandySmurf(),
			new Smurfette(),
			new GluttonSmurf(),
			new GrouchySmurf(),
			new BrainySmurf()
		);
	}
}
