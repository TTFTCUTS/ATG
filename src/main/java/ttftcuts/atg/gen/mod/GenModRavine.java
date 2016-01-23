package ttftcuts.atg.gen.mod;

import java.util.Random;

import net.minecraft.world.World;
import ttftcuts.atg.api.IGenMod;

public class GenModRavine implements IGenMod {

	private final static double cutoff = 0.45;	
	
	@Override
	public int modify(World world, int height, Random random, double rawHeight,
			int x, int z) {
		return (int)Math.round(height * 0.6 + ((rawHeight - cutoff)*0.7+cutoff)*256*0.4);
	}

	@Override
	public double noiseFactor() {
		return 20.0;
	}

}
