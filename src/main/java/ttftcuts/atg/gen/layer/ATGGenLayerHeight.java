package ttftcuts.atg.gen.layer;

import java.util.Random;

import ttftcuts.atg.ATG;
import ttftcuts.atg.gen.AltHeightNoise;
import ttftcuts.atg.gen.AltNoise;
import ttftcuts.atg.gen.HeightNoise;
import ttftcuts.atg.gen.INoiseProvider;
import ttftcuts.atg.gen.mod.GenModPlateau;

public class ATGGenLayerHeight extends ATGGenLayer {

	private Random rand;
	
	private ATGGenLayerInland inland;
	private ATGGenLayerRarity rarity;
	
	private INoiseProvider heightNoise;
	
	private AltNoise testnoise;
	
	private double offset;
	private double multiplier;
	
	public ATGGenLayerHeight( Long seed, ATGGenLayerInland inland, ATGGenLayerRarity rarity) {//, ATGGenLayerSourceImage image ) {
		super(seed);
		
		this.initChunkSeed((long)(seed*3744824524L + 38149085244241L ), (long)(seed*79818951L + 51498718L));
		
		this.rand = new Random(this.seed*3744824524L + 38149085244241L);
		
		this.inland = inland;
		this.rarity = rarity;
		//this.image = image;
		//this.heightNoise = this.inland.getNoise(); 
		this.heightNoise = new AltHeightNoise(this.rand.nextLong());
		
		this.offset = 0.0; //ATGMainConfig.genModHeight.getDouble(0.0);
		this.multiplier = 1.0; //ATGMainConfig.genModHeightMult.getDouble(1.0);
	}
	
	private double getHeight(int x, int z) {
		/*double h = this.heightNoise.getHeight(x,z);
		double r = this.rarity.getDouble(x, z);
		
		if ( r >= 0.375) {
			double factor = Math.min(1, (r-0.375)*12);
			double out = h;
			
			if ( h >= (85/255D) && h <= (110/255D) ) { // lowland plateaus
				
				out = (GenModPlateau.plateau((int)(h*255), rand, h, 85, 100, 110, 3.0, true )/255D);
			}
			
			if ( h >= (120/255D) && h <= (145/255D) ) { // highland plateaus
				
				out = (GenModPlateau.plateau((int)(h*255), rand, h, 120, 140, 145, 3.0, true )/255D);
			}
			
			h = ( h*(1-factor) + out*factor );
		}
		
		return Math.max( 0.005, h*this.multiplier + this.offset );*/

		double s = this.heightNoise.getHeight(x,z);
		
		//ATG.logger.info(s);
		
		return s;
	}
	
	public double getDouble(int x, int z) {
		return this.getHeight(x,z);
	}
	
	public int getInt(int x, int y) {
		return 1 + (int)(this.getHeight(x,y) * 254);
	}
	
	@Override
	public int[] getInts(int x, int z, int w, int h) {
		
		int[] data = new int[w*h];

        for (int dz = 0; dz < h; ++dz)
        {
            for (int dx = 0; dx < w; ++dx)
            {
                this.initChunkSeed((long)(x + dx), (long)(z + dz));
                
                int X = x+dx, Z = z+dz;
                
                double height = this.getHeight(X,Z);
                
                data[ dx + dz * w ] = 1 + (int)(height*254);
                if (data[ dx + dz * w ] == 1) {
                	//ATG.logger.warn("#### MISSING HEIGHT AT " + X + "," + Z);
                }
            }
        }

        return data;
	}

}
