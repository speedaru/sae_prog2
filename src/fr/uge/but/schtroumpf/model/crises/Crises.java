package fr.uge.but.schtroumpf.model.crises;

import java.util.List;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.types.GameModifierEffect;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
import fr.uge.but.schtroumpf.model.types.ResourceType;

public final class Crises {
	private Crises() {}
	
	public static class FamineCrisis implements Crisis {
		@Override public CrisisType getType() { return CrisisType.FAMINE; }

		@Override
		public List<GameModifierEffect<?>> getModifierEffects() {
			// starving smurfs have less stamina
			return List.of(
				new GameModifierEffect<>(GameModifierType.MAX_ENERGY_DELTA, -2)
			);
		}
	}
	
	public static class EpidemicCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.EPIDEMIC; }

	    @Override
		public List<GameModifierEffect<?>> getModifierEffects() {
	        // sick smurfs produce 1 less standard ability delta
			return List.of(
				new GameModifierEffect<>(GameModifierType.EFFICIENCY_DELTA, -1)
			);
	    }
	}
	
	public static class RevoltCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.REVOLT; }

	    @Override
		public List<GameModifierEffect<?>> getModifierEffects() {
	        // uncooperative Smurfs -25% flat chance to all dice rolls
			return List.of(
				new GameModifierEffect<>(GameModifierType.SUCCESS_CHANCE_BONUS, -0.25)
			);
	    }
	}
	
	public static class MassiveAttackCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.MASSIVE_ATTACK; }

	    @Override
		public List<GameModifierEffect<?>> getModifierEffects() {
	        // attackers cause panic, tiring out the defenders
			return List.of(
				new GameModifierEffect<>(GameModifierType.MAX_ENERGY_DELTA, -3)
			);
	    }

	    @Override
	    public void applyImmediateEffects(SmurfVillage village) {
	        // village gets pillaged and loses tools and gold
	        village.updateResource(ResourceType.TOOLS, -1);
	        village.updateResource(ResourceType.GOLD, -3);
	    }
	}
	
	public static class DarkAgesCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.DARK_AGES; }

	    @Override
		public List<GameModifierEffect<?>> getModifierEffects() {
	        // disables passive generation of berries during production phase
			return List.of(
				new GameModifierEffect<>(GameModifierType.PASSIVE_FOOD_PRODUCTION_BLOCKED, true)
			);
	    }
	}
	public static class BankruptcyCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.BANKRUPTCY; }

	    @Override
		public List<GameModifierEffect<?>> getModifierEffects() {
	        // retire une action pour le conseil
			return List.of(
				new GameModifierEffect<>(GameModifierType.ABILITIES_PER_TURN_DELTA, -1)
			);
	    }
	}
	public static class StoneAgeCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.STONE_AGE; }

	    @Override
		public List<GameModifierEffect<?>> getModifierEffects() {
	        // retire un de chaque production
			return List.of(
				new GameModifierEffect<>(GameModifierType.PRODUCTION_DELTA, -1)
			);
	    }
	}
}
