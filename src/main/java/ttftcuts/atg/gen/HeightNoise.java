package ttftcuts.atg.gen;

import java.util.Random;

//import ttftcuts.atg.config.configfiles.ATGMainConfig;

public class HeightNoise {
	long seed;
	Random rand;
	ATGPerlin[] heightNoise;
	
	private static final double seafix = 0.234;
	//private static final double mountainfix = 0.90;
	
	public HeightNoise(long seed) {
		this.seed = seed;
		this.rand = new Random( (seed*3481348L) * ((seed+378423L)*48592823589L) + seed*seed*seed );
		
		this.heightNoise = new ATGPerlin[8];
		
		double scale = 1.0; //ATGMainConfig.genModHeightScale.getDouble(1.0);
		
		this.heightNoise[1] = new ATGPerlin(this.rand, 256 * scale);
		this.heightNoise[2] = new ATGPerlin(this.rand, 2132 * scale); // 2132
		this.heightNoise[3] = new ATGPerlin(this.rand, 953 * scale);
		this.heightNoise[4] = new ATGPerlin(this.rand, 436 * scale);
		
		this.heightNoise[5] = new ATGPerlin(this.rand, 48 * scale);
		this.heightNoise[6] = new ATGPerlin(this.rand, 16 * scale);
		
		this.heightNoise[7] = new ATGPerlin(this.rand, 8264 * scale); // 4728
	}
	
	public double getHeight(int x, int z) {
		
		double h1 = this.heightNoise[1].normNoise(x, z, 0);
        double h2 = this.heightNoise[2].normNoise(x, z, 0);
        double h3 = this.heightNoise[3].normNoise(x, z, 0);
        double h4 = this.heightNoise[4].normNoise(x, z, 0);
        
        double d1 = this.heightNoise[5].normNoise(x, z, 0);
        double d2 = this.heightNoise[6].normNoise(x, z, 0);
        
        double sea = 1 - Math.abs( this.heightNoise[7].normNoise(x, z, 0) - 0.5 )*2;
        sea = Math.max(0, (sea*sea) - 0.8);
		
		double height = ( ( h1*h1*h3*h3*0.84 + h1*h4*0.1 + d1*0.05 + d2*0.01 )*2.8 - 0.08 );
        
        if ( height < 0.4 ) {
        	double factor = Math.max(0, (1-height)-0.6);
        	/*if ( height < 0.1 ) {
        		factor = factor * 0.85;
        	}*/
        	height = height*(1-factor) + (h2 - 0.04)*factor;
        }
        
        height = height - sea*0.4;
        
        if ( height < seafix ) {
        	double diff = seafix - height;
        	height = seafix - diff*0.6;
        }/* else if ( height > mountainfix ) {
        	double diff = height - mountainfix;
        	height = mountainfix + diff*0.5;
        }*/
        
        return height;// - 0.02;
	}
	
	public int getHeightInt(int x, int z) {
		return 1 + (int)(this.getHeight(x,z) * 254);
	}
	
	public double getInland(int x, int z) {
		return this.heightNoise[2].normNoise(x, z, 0);
	}
	
	public double getImageNoise(int x, int z) {
		return ( this.heightNoise[4].normNoise(x, z, 0)*0.5 + this.heightNoise[5].normNoise(x, z, 0)*0.35 + this.heightNoise[6].normNoise(x, z, 0)*0.15 );
	}
	
	public static int getInt(double height) {
		return 1 + (int)(height * 254);
	}
	
}
