package ttftcuts.atg.gen.mod;

import java.util.Random;

import net.minecraft.world.World;

import ttftcuts.atg.api.IGenMod;

public class GenModVolcano implements IGenMod {
	public static final int craterHeight = 244;
	public static final int shaftHeight = 230;
	public static final int magmaHeight = shaftHeight - 4;
	
	@Override
	public int modify(World world, int height, Random random, double rawHeight, int x, int z) {
		
		int top = height - craterHeight;
		double dip = top*0.35;
		
		int crater = craterHeight - (int)Math.ceil(dip);
		
		return (crater <= shaftHeight) ? 0 : crater;
	}

	public double noiseFactor() {
		return 0.0;
	}
}
