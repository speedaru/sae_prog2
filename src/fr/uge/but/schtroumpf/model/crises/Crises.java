package fr.uge.but.schtroumpf.model.crises;

import fr.uge.but.schtroumpf.model.SmurfVillage;
import fr.uge.but.schtroumpf.model.types.ResourceType;
import fr.uge.but.schtroumpf.model.types.VillageModifierContext;

public final class Crises {
	private Crises() {}
	
	public static class FamineCrisis implements Crisis {
		@Override public CrisisType getType() { return CrisisType.FAMINE; }

		@Override
		public void applyModifiers(VillageModifierContext ctx) {
			// starving smurfs have less stamina
			ctx.addMaxEnergyDelta(-2);
		}
	}
	
	public static class EpidemicCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.EPIDEMIC; }

	    @Override
	    public void applyModifiers(VillageModifierContext ctx) {
	        // sick smurfs only produce 50% of standard ability yields
	        ctx.multiplyEfficiency(0.5);
	    }
	}
	
	public static class RevoltCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.REVOLT; }

	    @Override
	    public void applyModifiers(VillageModifierContext ctx) {
	        // uncooperative Smurfs -25% flat chance to all dice rolls
	        ctx.addSuccessChanceBonus(-0.25);
	    }
	}
	
	public static class MassiveAttackCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.MASSIVE_ATTACK; }

	    @Override
	    public void applyModifiers(VillageModifierContext ctx) {
	        // attackers cause panic, tiring out the defenders
	        ctx.addMaxEnergyDelta(-1);
	    }

	    @Override
	    public void applyImmediateEffects(SmurfVillage village) {
	        // village gets pillaged and loses tools and gold
	        village.updateResource(ResourceType.TOOLS, -1);
	        village.updateResource(ResourceType.GOLD, -1);
	    }
	}
	
	public static class DarkAgesCrisis implements Crisis {
	    @Override public CrisisType getType() { return CrisisType.DARK_AGES; }

	    @Override
	    public void applyModifiers(VillageModifierContext ctx) {
	        // disables passive generation of berries during production phase
	        ctx.setPassiveFoodProductionBlocked(true);
	    }
	}
}
