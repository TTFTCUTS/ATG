package ttftcuts.atg.generator.biome;

import ttftcuts.atg.util.CoordCache;
import ttftcuts.atg.util.CoordPair;
import ttftcuts.atg.util.MathUtil;

import java.util.Random;

public class BiomeBlobs {

    protected CoordCache<BlobEntry> valueCache = new CoordCache<BlobEntry>(64);
    protected CoordCache<BlobOffset> offsetCache = new CoordCache<BlobOffset>(256);
    protected long seed;
    protected int layers;
    protected long[] layerseeds;

    public BiomeBlobs(long seed, int layers) {
        this.seed = seed;
        this.layers = layers;

        this.layerseeds = new long[layers];

        Random rand = new Random(seed);
        for (int i=0; i<layers; i++) {
            this.layerseeds[i] = rand.nextLong();
        }
    }

    public BlobEntry getValue(int x, int z, int size) {
        BlobOffset offset = this.offsetCache.get(x,z);
        CoordPair c = null;

        if (offset == null) {
            c = new CoordPair(x, z);

            for (int i = 0; i < Math.min(this.layers, size); i++) {
                c = this.zoom(c, this.layerseeds[i]);
            }

            offset = new BlobOffset(x,z);
            offset.offset = c;

            this.offsetCache.put(x,z,offset);
        } else {
            c = offset.offset;
        }

        BlobEntry blob = this.valueCache.get(c.x, c.z);
        if (blob == null) {
            blob = new BlobEntry(c.x,c.z);
            this.valueCache.put(c.x,c.z,blob);
        }
        if(Double.isNaN(blob.biome)) {
            Random rand = new Random(MathUtil.coordSeed(c.x,c.z,this.seed));
            blob.biome = rand.nextDouble();
            blob.subbiome = rand.nextDouble();
        }

        return blob;
    }

    public CoordPair zoom(CoordPair coords, long seed) {
        boolean ex = (coords.x & 1) == 0;
        boolean ez = (coords.z & 1) == 0;

        int hx = coords.x / 2;
        int hz = coords.z / 2;

        if (ex && ez) {
            return new CoordPair(hx, hz);
        } else {
            Random rand = new Random(MathUtil.coordSeed(coords.x, coords.z, seed));
            int ox = rand.nextBoolean() ? (coords.x < 0 ? -1 : 1) : 0;
            int oz = rand.nextBoolean() ? (coords.z < 0 ? -1 : 1) : 0;

            if (ex) {
                return new CoordPair(hx, hz + oz);
            } else if (ez) {
                return new CoordPair(hx + ox, hz);
            } else {
                return new CoordPair(hx + ox, hz + oz);
            }
        }
    }

    public static class BlobOffset extends CoordPair {
        public CoordPair offset = null;

        public BlobOffset(int x, int z) {
            super(x,z);
        }
    }

    public static class BlobEntry extends CoordPair {
        public double biome = Double.NaN;
        public double subbiome = Double.NaN;

        public BlobEntry(int x, int z) {
            super(x,z);
        }
    }
}
