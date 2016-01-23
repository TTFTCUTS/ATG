package ttftcuts.atg.gen.layer;

import java.util.Random;

import ttftcuts.atg.gen.ATGPerlin;

public class ATGGenLayerAltNoise extends ATGGenLayer {

private Random rand;
	
	private ATGPerlin[] noise;
	
	public ATGGenLayerAltNoise( Long seed ) {
		super(seed);
		
		this.initChunkSeed((long)(seed*32857482L + 32985720342L ), (long)(seed*4985472834L + 1639572824L));
		
		this.rand = new Random( this.nextInt(1000000000) * seed );
		
		this.noise = new ATGPerlin[4];
		
		this.noise[1] = new ATGPerlin(this.rand, 67);
		this.noise[2] = new ATGPerlin(this.rand, 29);
		this.noise[3] = new ATGPerlin(this.rand, 7);

	}
	
	private double getValue(int x, int z) {
		
		double t1 = this.noise[1].normNoise(x, z, 0);
        double t2 = this.noise[2].normNoise(x, z, 0);
        double t3 = this.noise[3].normNoise(x, z, 0);
        
        //double t4 = this.tempNoise[3].normNoise(x, z, 0);
        
        return ( t1*0.5 + t2*0.35 + t3*0.15);
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
                
                double temp = this.getValue(X,Z);
                
                data[ dx + dz * w ] = 1 + (int)(temp*255);
            }
        }
        return data;
	}

	@Override
	public int getInt(int x, int z) {
		return 1 + (int)( this.getValue(x,z)*255 );
	}
	
	public double getDouble(int x, int z) {
		return getValue(x,z);
	}

}
