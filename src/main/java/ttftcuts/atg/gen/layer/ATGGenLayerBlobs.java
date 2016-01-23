package ttftcuts.atg.gen.layer;

import java.util.Random;

import ttftcuts.atg.utils.ATGUtils;

public class ATGGenLayerBlobs extends ATGGenLayer {

	protected Random blob; 
	protected long salt;
	
	public static final int biomebits = 16;
	public static final int subbits = 14;
	
	public static final int granularity = (int) Math.pow(2, biomebits)-1;
	public static final int subgranularity = (int) Math.pow(2, subbits)-1;
	
	public ATGGenLayerBlobs(long seed) {
		super(seed);
		
		this.blob = new Random(0);
		this.salt = 9348712385L;

		//this.initChunkSeed((long)(seed*39841735L + 89896234625L ), (long)(seed*92375465L + 234984624L));
	}

	private int getBlob(int x, int z) {
		
		//this.blob.setSeed( (x^z) + this.salt + this.seed );
		
		this.blob.setSeed( ATGUtils.coordRandom(x, z, this.seed) );
		return this.blob.nextInt(ATGGenLayerBlobs.granularity);
	}
	
	@Override
	public int getInt(int x, int z) {
		return this.getBlob(x,z);
	}

	@Override
	public double getDouble(int x, int z) {
		return this.getBlob(x,z);
	}

	@Override
	public int[] getInts(int x, int z, int w, int h) {
		int[] data = new int[w*h];

        for (int dz = 0; dz < h; ++dz)
        {
            for (int dx = 0; dx < w; ++dx)
            {
                data[ dx + dz * w ] = this.getBlob(x+dx, z+dz);
            }
        }

        return data;
	}

}
