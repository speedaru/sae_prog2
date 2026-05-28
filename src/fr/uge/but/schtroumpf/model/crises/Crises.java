package fr.uge.but.schtroumpf.model.crises;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.types.GameModifierType;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.types.VillageModifierContext;

public final class Crises {
	private Crises() {}
	
	public static class FamineCrisis implements Crisis {
		@Override public CrisisType getType() { return CrisisType.FAMINE; }

		@Override
		public void applyModifiers(VillageModifierContext ctx) {
			// starving smurfs have less stamina
			ctx.addInt(GameModifierType.MAX_ENERGY_DELTA, -2);
		}
	}
	
	public static class EpidemicCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.EPIDEMIC; }

	    @Override
	    public void applyModifiers(VillageModifierContext ctx) {
	        // sick smurfs only produce 50% of standard ability yields
			ctx.addDouble(GameModifierType.EFFICIENCY_MULTIPLIER, 0.5);
	    }
	}
	
	public static class RevoltCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.REVOLT; }

	    @Override
	    public void applyModifiers(VillageModifierContext ctx) {
	        // uncooperative Smurfs -25% flat chance to all dice rolls
			ctx.addDouble(GameModifierType.SUCCESS_CHANCE_BONUS, -0.25);
	    }
	}
	
	public static class MassiveAttackCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.MASSIVE_ATTACK; }

	    @Override
	    public void applyModifiers(VillageModifierContext ctx) {
	        // attackers cause panic, tiring out the defenders
			ctx.addInt(GameModifierType.MAX_ENERGY_DELTA, -3);
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
	    public void applyModifiers(VillageModifierContext ctx) {
	        // disables passive generation of berries during production phase
			ctx.setBool(GameModifierType.PASSIVE_FOOD_PRODUCTION_BLOCKED, true);
	    }
	}
	public static class BankruptcyCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.BANKRUPTCY; }

	    @Override
	    public void applyModifiers(VillageModifierContext ctx) {
	        // disables passive generation of berries during production phase
			
	    }
	}
}
