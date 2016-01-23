package ttftcuts.atg.gen.layer;

import java.util.Random;

import ttftcuts.atg.gen.ATGPerlin;

public class ATGGenLayerMoisture extends ATGGenLayer {

private Random rand;
	
	private ATGPerlin[] moistureNoise;
	private ATGGenLayer temp;
	private ATGGenLayer inland;
	//private ATGGenLayerSourceImage image;
	
	private double offset;
	private double multiplier;
	
	public ATGGenLayerMoisture( Long seed, ATGGenLayer temp, ATGGenLayer inland) {//, ATGGenLayerSourceImage image ) {
		super(seed);
		this.temp = temp;
		this.inland = inland;
		//this.image = image;
		
		this.offset = 0.0; //ATGMainConfig.genModRain.getDouble(0.0);
		this.multiplier = 1.0; //ATGMainConfig.genModRainMult.getDouble(1.0);
		
		this.initChunkSeed((long)(seed*23742839842L + 95686748652L ), (long)(seed*4253436256L + 846235461L));
		
		this.rand = new Random( this.nextInt(1000000000) * seed );
		
		double scale = 1.0; //ATGMainConfig.genModTempScale.getDouble(1.0);
		
		this.moistureNoise = new ATGPerlin[4];
		
		this.moistureNoise[1] = new ATGPerlin(this.rand, 400 * scale );
		this.moistureNoise[2] = new ATGPerlin(this.rand, 243 * scale );
		this.moistureNoise[3] = new ATGPerlin(this.rand, 53 * scale );

	}
	
	private double getMoisture(int x, int z) {
		double temp = this.temp.getInt(x,z) * 0.00390625D; // /256
		double inland = this.inland.getInt(x, z) * 0.00390625D;
		
		double inlandfactor = Math.max( -0.1, inland-0.5 );
		double tempfactor = temp - 0.45 + inlandfactor*0.9;
		
		double t1 = this.moistureNoise[1].normNoise(x, z, 0);
        double t2 = this.moistureNoise[2].normNoise(x, z, 0);
        double t3 = this.moistureNoise[3].normNoise(x, z, 0);
        
        double mix = ( t1*0.76 + t2*0.16 + t3*0.08)*1.2 - 0.05; // - 0.1
        
        double moisture = Math.max(0, Math.min(1, mix-tempfactor*0.35));
        
        /*if ( ATGMainConfig.useImageMap.getBoolean(false) ) {
			int imageinfo = this.image.getInt(x, z);
			
			double a = ATGBicubic.getAlpha(imageinfo)/255.0;
			double blue = ATGBicubic.getBlue(imageinfo)/255.0;
			if ( a > 0 && blue > 0 ) {
				moisture = moisture*(1-a) + blue*a;
			}
		}*/
        
        return moisture * this.multiplier + this.offset;
	}
	
	@Override
	public int[] getInts(int x, int z, int w, int h) {
		int[] data = new int[w*h];

		//int i = 0;
        for (int dz = 0; dz < h; ++dz)
        {
            for (int dx = 0; dx < w; ++dx)
            {
                this.initChunkSeed((long)(x + dx), (long)(z + dz));
                
                int X = x+dx, Z = z+dz;
                
                double temp = this.getMoisture(X,Z);
                
                data[ dx + dz * w ] = 1 + (int)(temp*255);
            }
        }
        return data;
	}

	@Override
	public int getInt(int x, int z) {
		return 1 + (int)( this.getMoisture(x,z)*255 );
	}
	
	public double getDouble(int x, int z) {
		return this.getMoisture(x,z);
	}

}
