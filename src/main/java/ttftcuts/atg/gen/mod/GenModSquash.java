package ttftcuts.atg.gen.mod;

import java.util.Random;

import net.minecraft.world.World;

import ttftcuts.atg.api.IGenMod;

public class GenModSquash implements IGenMod {

	private int startHeight = 62;
	private int deltaHeight = 9;
	private double heightExponent = 3.0;
	
	public GenModSquash(){}
	
	public GenModSquash(int start, int height, double exponent) {
		startHeight = start;
		deltaHeight = height;
		heightExponent = exponent;
	}
	
	@Override
	public int modify(World world, int height, Random random, double rawHeight, int x, int z) {
		if ( height > startHeight ) {			
			int aheight = height - startHeight;
			double rheight = rawHeight*255 - startHeight;
			
			double dfactor = (double)rheight / deltaHeight;
			dfactor = Math.pow(dfactor, heightExponent);
			
			double afactor = (double)aheight / deltaHeight;
			afactor = Math.pow(afactor, heightExponent);
			
			double factor = Math.min( 1.0, afactor*0.75 + dfactor*0.25 );
			
			return (int)Math.floor(startHeight + factor*deltaHeight);
			
			//return Math.max(61, (int)Math.floor((Math.max(0, height-59))*0.6) + 59 );
		}
		return height;
	}

	@Override
	public double noiseFactor() {
		return 1.0;
	}

}
