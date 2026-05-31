package fr.uge.but.schtroumpf.model.crises;

import java.util.List;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.types.ModifierEffect;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public final class Crises {
	private Crises() {}
	
	public static class FamineCrisis implements Crisis {
		@Override public CrisisType getType() { return CrisisType.FAMINE; }

		@Override
		public List<ModifierEffect> getModifierEffects() {
			// starving smurfs have less stamina
			return List.of(
				ModifierEffect.crisisModifierEffect(GameModifierType.MAX_ENERGY_DELTA, -2)
			);
		}
	}
	
	public static class EpidemicCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.EPIDEMIC; }

	    @Override
		public List<ModifierEffect> getModifierEffects() {
	        // sick smurfs produce 1 less standard ability delta
			return List.of(
				ModifierEffect.crisisModifierEffect(GameModifierType.EFFICIENCY_DELTA, -1)
			);
	    }
	}
	
	public static class RevoltCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.REVOLT; }

	    @Override
		public List<ModifierEffect> getModifierEffects() {
	        // uncooperative smurfs -25% chance to all abilties success luck
			return List.of(
				ModifierEffect.crisisModifierEffect(GameModifierType.SUCCESS_CHANCE_BONUS, -0.25)
			);
	    }
	}
	
	public static class MassiveAttackCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.MASSIVE_ATTACK; }

	    @Override
		public List<ModifierEffect> getModifierEffects() {
	    	// smurfs get tired because of attack
			return List.of(
				ModifierEffect.crisisModifierEffect(GameModifierType.MAX_ENERGY_DELTA, -3)
			);
	    }

	    @Override
	    public void applyImmediateEffects(SmurfVillage village) {
	        // village gets pillaged and lose tools and gold
	        village.updateResource(ResourceType.TOOLS, -1);
	        village.updateResource(ResourceType.GOLD, -3);
	    }
	}
	
	public static class DarkAgesCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.DARK_AGES; }

	    @Override
		public List<ModifierEffect> getModifierEffects() {
	        // disables passive generation of berries during production phase
			return List.of(
				ModifierEffect.crisisModifierEffect(GameModifierType.PASSIVE_FOOD_PRODUCTION_BLOCKED, true)
			);
	    }
	}
	public static class BankruptcyCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.BANKRUPTCY; }

	    @Override
		public List<ModifierEffect> getModifierEffects() {
	    	// bankrupt council has 1 less ability
			return List.of(
				ModifierEffect.crisisModifierEffect(GameModifierType.ABILITIES_PER_TURN_DELTA, -1)
			);
	    }
	}
	public static class StoneAgeCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.STONE_AGE; }

	    @Override
		public List<ModifierEffect> getModifierEffects() {
	    	// stone age village produces less resources
			return List.of(
				ModifierEffect.crisisModifierEffect(GameModifierType.PRODUCTION_DELTA, -1)
			);
	    }
	}
}
