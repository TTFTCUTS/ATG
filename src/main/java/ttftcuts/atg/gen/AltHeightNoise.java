package ttftcuts.atg.gen;

import java.util.Random;

import ttftcuts.atg.gen.mod.GenModPlateau;

public class AltHeightNoise implements INoiseProvider {
	long seed;
	Random rand;
	
	private AltNoise[] noise;
	
	private static final double msmooth = 8.0; //64.0;
	private static final double ismooth = 8.0; //32.0;
	
	public AltHeightNoise(long seed) {
		this.seed = seed;
		this.rand = new Random( (seed*3481348L) * ((seed+378423L)*48592823589L) + seed*seed*seed );
		
		this.noise = new AltNoise[7];
		
		double scale = 0.5; //ATGMainConfig.genModHeightScale.getDouble(1.0);
		
		this.noise[0] = new AltNoise(this.rand.nextLong(), 600*scale);
		this.noise[1] = new AltNoise(this.rand.nextLong(), 500*scale);
		this.noise[2] = new AltNoise(this.rand.nextLong(), 850*scale);
		this.noise[3] = new AltNoise(this.rand.nextLong(), 1350*scale);
		this.noise[4] = new AltNoise(this.rand.nextLong(), 2100*scale);
		this.noise[5] = new AltNoise(this.rand.nextLong(), 250*scale);
		this.noise[6] = new AltNoise(this.rand.nextLong(), 1600*scale);
	}
	
	public double getHeight(int x, int z) {
		double ledge = Math.min(1.0,  noise[0].noiseOctaves(x, 9342, z, 2, 2.0, 0.5) * 1.15 );
		ledge *= ledge;
		
		double volcano = noise[1].noiseOctaves(x, 32778, z, 2, 2.0, 0.5) * 1.8 - 1.06;
		volcano *= volcano * volcano;
		volcano = Math.max(0.0, volcano);
		
		double swissbase = noise[2].swissTurbulence(x, z, 3, 2.0, 0.4, 0.15);// * 0.75;
		
		double peak = noise[3].noiseOctaves(x, 1, z, 2, 2.0, 0.5) + 0.2;
		
		double sea = noise[4].noiseOctaves(x, 51, z, 5, 2.0, 0.5);
		double continent = Math.max(0.0, sea * 2.0 - 1.0);
		
		double swiss = swissbase * swissbase * 1.3 - 0.2;
		
		double jordan = noise[5].jordanTurbulence(x, z, 3, 1.92, 0.8, 0.55, 0.4, 0.35, 1.0, 0.7, 1.0, 4, 0.15, 0.25, 0.5);
		double islejordan = noise[6].jordanTurbulence(x + 13847893, z, 3, 1.92, 0.8, 0.55, 0.4, 0.35, 1.0, 0.7, 1.0, 4, 0.15, 0.25, 0.5);
		
		double mountain = (swiss + (1.2-swiss*0.9)*jordan*0.275)*1.18;
		
		mountain *= 1.0 + volcano * 1.5;
		mountain += volcano * 0.15;
		
		mountain *= peak;
		
		double beach = swissbase * 0.3 + 0.0875;
		if (beach < 0.2) {
			beach *= Math.min(1.0, beach * 5);
		}
		
		double lowland = swissbase * 0.55 - 0.0375 + (jordan * 0.1);
		
		lowland += volcano * 0.75 * peak;
		
		double islands = 0.05 + islejordan * 0.4 + jordan * 0.07;
		
		double n = Math.log(
			  Math.exp(mountain * msmooth)
			+ Math.exp(lowland * msmooth)
			+ Math.exp(beach * msmooth)
		) / msmooth;
		
		n = 0.1 + 0.1 * n + (0.9 * continent * n) + 0.15 * sea;
		
		n = Math.log(
			  Math.exp(n * ismooth)
			+ Math.exp(islands * ismooth)
		) / ismooth;
		
		if (ledge > 0.375) {
			double factor = Math.min(1.0, (ledge - 0.375)*12);
			double out = n;
			
			if ( n >= (60/255D) && n <= (85/255D) ) { // lowland plateaus
				out = (GenModPlateau.plateau((int)(n*255), rand, n, 60, 70, 85, 2.0, true )/255D);
			}

			if ( n >= (85/255D) && n <= (110/255D) ) { // lowland plateaus
				out = (GenModPlateau.plateau((int)(n*255), rand, n, 85, 100, 110, 3.0, true )/255D);
			}
			
			if ( n >= (120/255D) && n <= (145/255D) ) { // highland plateaus
				out = (GenModPlateau.plateau((int)(n*255), rand, n, 120, 140, 145, 3.0, true )/255D);
			}
			
			n = (n*(1-factor) + out*factor);
		}
		
		n = Math.min(1.0, Math.max(0.0, n));
		
		return n;
	}
	
	public int getHeightInt(int x, int z) {
		return 1 + (int)(this.getHeight(x,z) * 254);
	}
	
	public double getInland(int x, int z) {
		return noise[4].noiseOctaves(x, 51, z, 5, 2.0, 0.5);
	}
	
	public static int getInt(double height) {
		return 1 + (int)(height * 254);
	}
	
}
