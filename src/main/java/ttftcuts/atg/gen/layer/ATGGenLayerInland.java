package ttftcuts.atg.gen.layer;

import ttftcuts.atg.gen.HeightNoise;

//import java.util.Random;

public class ATGGenLayerInland extends ATGGenLayer {

	//private Random rand;
	
	private HeightNoise heightNoise;
	//private NoiseGeneratorOctaves terrainNoise1;
	//private NoiseGeneratorOctaves tempNoise1;
	//private NoiseGeneratorOctaves moistureNoise1;
	
	public ATGGenLayerInland( Long seed ) {
		super(seed);
		
		this.initChunkSeed((long)(seed*3744824524L + 38149085244241L ), (long)(seed*79818951L + 51498718L));
		
		this.heightNoise = new HeightNoise(seed);
	}
	
	public HeightNoise getNoise() {
		return this.heightNoise;
	}
	
	private double getHeight(int x, int z) {
		return this.heightNoise.getInland(x,z);
	}
	
	public int getInt(int x, int z) {
		return 1 + (int)(this.getHeight(x,z) * 254);
	}
	
	public double getDouble(int x, int z) {
		return getHeight(x,z);
	}
	
	@Override
	public int[] getInts(int x, int z, int w, int h) {
		
		//double[] noise = new double[w*h];
		//this.terrainNoise1.generateNoiseOctaves(noise, x, z, w, h, 0.005D, 0.005D, 0.5D);
		
		int[] data = new int[w*h];

		//int i = 0;
        for (int dz = 0; dz < h; ++dz)
        {
            for (int dx = 0; dx < w; ++dx)
            {
                this.initChunkSeed((long)(x + dx), (long)(z + dz));
                
                int X = x+dx, Z = z+dz;
                
                double height = this.getHeight(X,Z);
                
                //double noise = this.terrainNoise1.normNoise( (x + dx), (z + dz), 1 );
                
                data[ dx + dz * w ] = 1 + (int)(height*254);
                if (data[ dx + dz * w ] == 1) {
                	System.out.println("################## MISSING HEIGHT AT " + X + "," + Z);
                }
                
                //data[ dx + dz * w ] = 10 + (int)( (noise[dz + dx*h] - 5610D) * 8D );
                //System.out.println(noise[i] - 5600D);
                //System.out.println( noise );
                //i++;
            }
        }
       	//System.out.println(i);

        return data;
	}

}
