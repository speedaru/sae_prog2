package fr.uge.but.schtroumpf.model;

import module java.base;

import fr.uge.but.schtroumpf.model.ResourceManager.ResourceSnapshot;
import fr.uge.but.schtroumpf.model.characters.*;
import fr.uge.but.schtroumpf.model.characters.CharacterAbility.AbilityResult;
import fr.uge.but.schtroumpf.model.crises.*;
import fr.uge.but.schtroumpf.model.save.GameSave;
import fr.uge.but.schtroumpf.model.types.EventHistory;
import fr.uge.but.schtroumpf.model.types.GameModifierEffect;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
import fr.uge.but.schtroumpf.model.types.ResourceEffect;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.types.VillageModifierContext;
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

	public void updateResource(ResourceType resource, int amount) {
		resourceManager.add(resource, amount);
	}

	public void setResourceQuantity(ResourceType resource, int amount) {
		int max = ResourceManager.MAX_QUANTITY;
		if (amount < 0 || amount > max) {
			throw new IllegalArgumentException(String.format("Amount (%d) must be between 0 and %d", amount, max));
		}
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

    /**
     * Resets the turn's action counter. 
     * Call this at the start of a new turn (e.g., when saving round resources or replenishing energy).
     */
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

	public VillageModifierContext getModifiers() {
		return modifiers;
	}
	
	/**
	 * replaces standard GameRandomness.rollChance applies active crisis penalties
	 * to random roll
	 */
	public boolean rollChance(double baseChance) {
		double sucessChanceModifier = modifiers.getDouble(GameModifierType.SUCCESS_CHANCE_BONUS);
		double finalChance = baseChance + sucessChanceModifier;
		finalChance = Math.clamp(finalChance, 0, 1);
		return GameRandomness.rollChance(finalChance);
	}

	/** intercepts effect applications to apply efficiency multipliers */
	public void applyEffects(List<ResourceEffect> resourceEffects) {
		double efficienyMultiplier = modifiers.getDouble(GameModifierType.EFFICIENCY_MULTIPLIER);
		
		for (ResourceEffect effect : resourceEffects) {
			int delta = effect.delta();

			// apply active modifier for positive effects
			if (delta > 0) {
				delta = (int) Math.floor(delta * efficienyMultiplier);
			}

			updateResource(effect.resourceType(), delta);
		}
	}

	public void rechargeSmurfEnergy(SmurfCharacter smurf, int baseRate) {
		int energyRechargeRateDelta = modifiers.getInt(GameModifierType.ENERGY_RECHARGE_RATE_DELTA);
		int finalRate = baseRate + energyRechargeRateDelta;
		finalRate = Math.max(1, finalRate); // always at least 1

		smurf.updateEnergy(this, finalRate);
		Logger.LogDebug("recharged %s energy by %d (+%d modifier)", smurf, finalRate, energyRechargeRateDelta);
	}
	
	public ArrayList<ResourceType> getProductionAllowedResourceTypes() {
		ArrayList<ResourceType> allowedTypes = new ArrayList<>(Arrays.asList(ResourceType.values()));

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

	// ------------------------- crisis and end conditions -------------------------
	
	/** recalculates active crises and modifiers */
	public void setActiveCrises(List<Crisis> crises) {
		activeCrises.clear();
		activeCrises.addAll(crises);

		// recalculate all modifiers
		VillageModifierContext newModifiers = new VillageModifierContext();

		for (Crisis crisis : activeCrises) {
			// apply modifier effects
			for (GameModifierEffect<?> effect : crisis.getModifierEffects()) {
				newModifiers.accumulateEffect(effect);
			}

			// apply immediate resource changes
			crisis.applyImmediateEffects(this);
		}

		this.modifiers = newModifiers;
	}

	public List<Crisis> getActiveCrises() {
		return List.copyOf(activeCrises);
	}

	/** must be called when crises are up to date, after checkAndUpdateCrises() */
	public boolean isDefeated() {
//		int crisisCount = 0;
//		for (ResourceType type : ResourceType.values()) {
//			if (resourceManager.get(type) <= 0) {
//				crisisCount += 1;
//			}
//		}
//		return crisisCount >= 3;
		return activeCrises.size() >= MAX_CRISES;
	}

	// ------------------------- private helpers -------------------------
	
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
