package ttftcuts.atg.gen.layer;

import java.util.Random;

import ttftcuts.atg.gen.ATGPerlin;

public class ATGGenLayerRarity extends ATGGenLayer {

private Random rand;
	
	private ATGPerlin[] rareNoise;
	
	public ATGGenLayerRarity( Long seed ) {
		super(seed);
		
		this.initChunkSeed((long)(seed*32857482L + 32985720342L ), (long)(seed*4985472834L + 1639572824L));
		
		this.rand = new Random( this.nextInt(1000000000) * seed );
		
		this.rareNoise = new ATGPerlin[5];
		
		this.rareNoise[1] = new ATGPerlin(this.rand, 931);
		this.rareNoise[2] = new ATGPerlin(this.rand, 243);
		this.rareNoise[3] = new ATGPerlin(this.rand, 53);
		//this.rareNoise[4] = new ATGPerlin(this.rand, 23);

	}
	
	private double getRarity(int x, int z) {
		
		double t1 = this.rareNoise[1].normNoise(x, z, 0);
        double t2 = this.rareNoise[2].normNoise(x, z, 0);
        double t3 = this.rareNoise[3].normNoise(x, z, 0);
        
        //double t4 = this.tempNoise[3].normNoise(x, z, 0);
        
        double rarity = ( t1*0.7 + t2*0.25 + t3*0.05)*1.15;
        
        return Math.max(0, rarity*rarity);
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
                
                double temp = this.getRarity(X,Z);
                
                data[ dx + dz * w ] = 1 + (int)(temp*255);
            }
        }
        return data;
	}

	@Override
	public int getInt(int x, int z) {
		return 1 + (int)( this.getRarity(x,z)*255 );
	}
	
	public double getDouble(int x, int z) {
		return getRarity(x,z);
	}

}
