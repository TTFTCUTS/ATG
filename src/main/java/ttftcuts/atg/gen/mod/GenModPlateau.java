package ttftcuts.atg.gen.mod;

import java.util.Random;

import net.minecraft.world.World;

import ttftcuts.atg.api.IGenMod;

public class GenModPlateau implements IGenMod {

	public int minHeight = 64;
	public int maxHeight = 128;
	public int plateauHeight = 96;
	public double heightExponent = 3.0;
	public boolean limit = false;
	
	public GenModPlateau() {}
	
	public GenModPlateau(int min, int height, int max, boolean limit, double exponent) {
		minHeight = min;
		maxHeight = max;
		plateauHeight = height;
		heightExponent = exponent;
	}
	
	@Override
	public int modify(World world, int height, Random random, double rawHeight, int x, int z) {
		return plateau(height, random, rawHeight, minHeight, plateauHeight, maxHeight, heightExponent, limit);
	}
	
	public static int plateau(int height, Random random, double rawHeight, int minHeight, int plateauHeight, int maxHeight, double heightExponent, boolean limit) {
		int out = height;
		if ( height >= minHeight && height <= plateauHeight ) {
			// push upward
			
			int range = plateauHeight - minHeight;
			int diff = plateauHeight - height;
			double rdiff = plateauHeight - rawHeight*255;
			
			double factor = diff/range;
			factor = Math.pow( factor, heightExponent);
			
			double rfactor = rdiff/range;
			rfactor = Math.pow( rfactor, heightExponent);
			
			double ffactor = Math.min(1, factor * 0.25 + rfactor * 0.75 );
			
			out = (int)Math.round(plateauHeight - ffactor*range);
			
		} else if ( height <= maxHeight && height > plateauHeight ) {
			// push downward
			
			int range = maxHeight - plateauHeight;
			int diff = height - plateauHeight;
			double rdiff = rawHeight*255 - plateauHeight;
			
			double factor = diff/range;
			factor = Math.pow( factor, heightExponent);
			
			double rfactor = rdiff/range;
			rfactor = Math.pow( rfactor, heightExponent);
			
			double ffactor = Math.min(1, factor * 0.25 + rfactor * 0.75 );
			
			out = (int)Math.round(plateauHeight + ffactor*range);
			
		}
		
		if ( out < minHeight && limit) { out = minHeight; }
		else if ( out > maxHeight && limit) { out = maxHeight; }
		
		return out;
	}

	@Override
	public double noiseFactor() {
		return 1.0;
	}

}
