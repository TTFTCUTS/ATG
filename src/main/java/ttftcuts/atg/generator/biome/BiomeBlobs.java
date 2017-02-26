package ttftcuts.atg.generator.biome;

import ttftcuts.atg.util.CoordCache;
import ttftcuts.atg.util.CoordPair;
import ttftcuts.atg.util.MathUtil;

import java.util.Random;

public class BiomeBlobs {

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

    public BlobEntry getValue(int x, int z, int size, int subsize) {
        BlobOffset offset = this.offsetCache.get(x,z);
        CoordPair c = null;
        CoordPair cSub = null;

        if (offset == null) {
            c = new CoordPair(x, z);
            cSub = new CoordPair(x, z);

            size = Math.min(this.layers, size);
            subsize = Math.min(this.layers, subsize);

            int count = Math.max(size, subsize);
            CoordPair ipair = new CoordPair(x,z);

            for (int i = 0; i < count; i++) {
                ipair = this.zoom(ipair, this.layerseeds[i]);

                if (i == size - 1) {
                    c = new CoordPair(ipair.x, ipair.z);
                }

                if (i == subsize - 1) {
                    cSub = new CoordPair(ipair.x, ipair.z);
                }
            }

            offset = new BlobOffset(x,z);
            offset.offset = c;
            offset.subOffset = cSub;

            this.offsetCache.put(x,z,offset);
        } else {
            c = offset.offset;
            cSub = offset.subOffset;
        }

        Random rand = new Random(MathUtil.coordRandom(c.x,c.z, this.seed) + this.seed);
        double biome = rand.nextDouble();
        rand = new Random(MathUtil.coordRandom(cSub.x,cSub.z, this.seed) + this.seed + 13);
        double subbiome = rand.nextDouble();

        return new BlobEntry(biome, subbiome);
    }

    public CoordPair zoom(CoordPair coords, long seed) {
        boolean ex = (coords.x & 1) == 0;
        boolean ez = (coords.z & 1) == 0;

        int hx = coords.x / 2;
        int hz = coords.z / 2;

        if (ex && ez) {
            return new CoordPair(hx, hz);
        } else {
            Random rand = new Random(MathUtil.coordRandom(coords.x,coords.z, this.seed) + seed);
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
        public CoordPair subOffset = null;

        public BlobOffset(int x, int z) {
            super(x,z);
        }
    }

    public static class BlobEntry {
        public final double biome;
        public final double subbiome;

        public BlobEntry(double biome, double subbiome) {
            this.biome = biome;
            this.subbiome = subbiome;
        }
    }
}
