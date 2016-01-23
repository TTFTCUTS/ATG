package ttftcuts.atg.gen.layer;

import java.util.Random;

import ttftcuts.atg.gen.ATGPerlin;

public class ATGGenLayerTemperature extends ATGGenLayer {

private Random rand;
	
	private ATGPerlin[] tempNoise;
	private ATGGenLayer height;
	private ATGGenLayer inland;
	//private ATGGenLayerSourceImage image;
	
	private double climateOffset;
	
	private double offset;
	private double multiplier;
	
	public ATGGenLayerTemperature( Long seed, ATGGenLayer height, ATGGenLayer inland) {//, ATGGenLayerSourceImage image ) {
		super(seed);
		this.height = height;
		this.inland = inland;
		//this.image = image;
		
		this.offset = 0.0; //ATGMainConfig.genModTemp.getDouble(0.0);
		this.multiplier = 1.0; //ATGMainConfig.genModTempMult.getDouble(1.0);
		
		this.initChunkSeed((long)(seed*197376485L + 435567256165L ), (long)(seed*4645788735624L + 49485237494L));
		
		this.rand = new Random( this.nextInt(1000000000) * seed );
		
		double scale = 1.0; //ATGMainConfig.genModTempScale.getDouble(1.0);
		
		this.tempNoise = new ATGPerlin[5];
		
		this.tempNoise[1] = new ATGPerlin(this.rand, 1281 * scale );
		this.tempNoise[2] = new ATGPerlin(this.rand, 119 * scale );
		this.tempNoise[3] = new ATGPerlin(this.rand, 26 * scale );
		this.tempNoise[4] = new ATGPerlin(this.rand, 3 * scale );
		
		this.climateOffset = this.rand.nextDouble();

	}
	
	private double getTemperature(int x, int z) {
		double height = this.height.getInt(x,z) * 0.00390625D; // /256
		double inland = this.inland.getInt(x,z) * 0.00390625D;
		
		double inlandfactor = Math.max(0, inland-0.5);
		double heightfactor = Math.max(0, height*2 - 0.9);		
		
		double t1 = this.tempNoise[1].normNoise(x, z, 0);
        double t2 = this.tempNoise[2].normNoise(x, z, 0);
        double t3 = this.tempNoise[3].normNoise(x, z, 0);
        
        //double t4 = this.tempNoise[3].normNoise(x, z, 0);
        
        double mix = ( t1*0.87 + t2*0.07 + t3*0.06)*1.3 - 0.2; // - 0.15
        //double temp = ( t1*0.82 + t2*0.06 + t3*0.05 + t4*0.07)*1.3 - 0.15;
        
		double temp = Math.max(0,  mix + inlandfactor*0.5 - heightfactor*0.85);
		
        /*if (ATGMainConfig.useClimate.getBoolean(false)) {
			
        	int mode = ATGMainConfig.climateType.getInt(0);
        	if ( mode > 2 || mode < 0 ) {
        		mode = 0;
        	}
			double min = ATGMainConfig.climateMin.getDouble(0.0);
			double max = ATGMainConfig.climateMax.getDouble(1.0);
			int period = ATGMainConfig.climatePeriod.getInt(5000);
			
			double range = max-min;
			
			double pos = ( (z % period) / (double)period );
			if ( mode != 0 ) {
				pos = z / (double)period;
			}
			
			if ( ATGMainConfig.climateCentre.getBoolean(false) ) {
				if ( mode == 2 ) {
					pos += 0.5;
				} else {
					pos += 0.25;
				}
			} else {
				pos += this.climateOffset;
			}
			
			if ( mode == 0 ) {
				// normal looping
				temp = ((Math.sin(Math.PI*2*pos) + (temp-0.5)*0.2 )* range) + min;
			} else if ( mode == 1 ) {
				pos = Math.max(0, Math.min(1, pos));
				temp = ((-Math.cos(Math.PI*2*pos) + (temp-0.5)*0.2 )* range) + min;
			} else if ( mode == 2 ) {
				pos = Math.max(0, Math.min(1, pos));
				temp = ((-Math.cos(Math.PI*pos) + (temp-0.5)*0.2 )* range) + min;
			}
			
		} */
        
        /*if ( ATGMainConfig.useImageMap.getBoolean(false) ) {
			int imageinfo = this.image.getInt(x, z);
			
			double a = ATGBicubic.getAlpha(imageinfo)/255.0;
			double green = ATGBicubic.getGreen(imageinfo)/255.0;
			//System.out.println("enabled: data="+imageinfo+", a="+a);
			if ( a > 0 && green > 0 ) {
				temp = temp*(1-a) + green*a;
			}
			//h = red;
			//System.out.println(imageinfo +", "+ red);
		}*/
        return temp * this.multiplier + this.offset;
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
                
                double temp = this.getTemperature(X,Z);
                
                data[ dx + dz * w ] = 1 + (int)(temp*255);
            }
        }
        return data;
	}

	@Override
	public int getInt(int x, int z) {
		return 1 + (int)( this.getTemperature(x,z)*255 );
	}
	
	public double getDouble(int x, int z) {
		return getTemperature(x,z);
	}

}
