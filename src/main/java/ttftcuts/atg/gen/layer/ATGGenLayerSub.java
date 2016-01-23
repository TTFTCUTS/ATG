package ttftcuts.atg.gen.layer;

import java.util.Random;

import net.minecraft.world.gen.layer.GenLayer;

import ttftcuts.atg.utils.ATGUtils;

public class ATGGenLayerSub extends ATGGenLayer {

	protected Random blob; 
	protected long salt;
	private GenLayer lastlayer;
	
	public ATGGenLayerSub(long seed, GenLayer lastlayer) {
		super(seed);
		
		this.lastlayer = lastlayer;
		this.blob = new Random(0);
		this.salt = 9348712385L;

		//this.initChunkSeed((long)(seed*39841735L + 89896234625L ), (long)(seed*92375465L + 234984624L));
	}

	private int getBlob(int x, int z) {
		
		//this.blob.setSeed( (x^z) + this.salt + this.seed );
		
		this.blob.setSeed( ATGUtils.coordRandom(x+this.seed, z-this.seed, this.seed) );
		return this.blob.nextInt(ATGGenLayerBlobs.subgranularity);
	}
	
	@Override
	public int getInt(int x, int z) {
		return lastlayer.getInts(x,z,1,1)[0] + ( this.getBlob(x,z) << ATGGenLayerBlobs.biomebits );
	}

	@Override
	public double getDouble(int x, int z) {
		return lastlayer.getInts(x,z,1,1)[0] + ( this.getBlob(x,z) << ATGGenLayerBlobs.biomebits );
	}

	@Override
	public int[] getInts(int x, int z, int w, int h) {
		int[] data = lastlayer.getInts(x,z,w,h);

        for (int dz = 0; dz < h; ++dz)
        {
            for (int dx = 0; dx < w; ++dx)
            {
                data[ dx + dz * w ] = data[ dx + dz * w ] + ( this.getBlob(x+dx,z+dz) << ATGGenLayerBlobs.biomebits );
            }
        }

        return data;
	}

}
