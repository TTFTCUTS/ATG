package ttftcuts.atg.gen.mod;

import java.util.Random;

import net.minecraft.world.World;

import ttftcuts.atg.api.IGenMod;

public class GenModMushroom implements IGenMod {
	public static final int cutoff = 45;
	public static final int sealevel = 64;
	public static final double smoothing = 0.75;
	
	@Override
	public int modify(World world, int height, Random random, double rawHeight, int x, int z) {
		
		int diff = Math.max(0, cutoff-height);
		double smoothdiff = Math.max(0, cutoff-(rawHeight*256));
		
		return (int)Math.round( sealevel - 3 + (diff*(1-smoothing) + smoothdiff*smoothing)*3 );
	}

	public double noiseFactor() {
		return 5.0;
	}
}
