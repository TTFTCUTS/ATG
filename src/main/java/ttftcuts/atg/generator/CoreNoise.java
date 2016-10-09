package ttftcuts.atg.generator;

import ttftcuts.atg.ATG;

import java.util.LinkedHashMap;
import java.util.Map;

public class CoreNoise {
    public final long seed;
    protected NoiseCache cache = new NoiseCache();

    public CoreNoise(long seed) {
        this.seed = seed;
    }

    public NoiseCache.NoiseEntry getEntry(int x, int z) {
        NoiseCache.CoordPair coord = new NoiseCache.CoordPair(x,z);
        if (!this.cache.containsKey(coord)) {
            this.cache.put(coord, new NoiseCache.NoiseEntry(x,z));
        }
        return this.cache.get(coord);
    }



    //------ Height ---------------------------------------------------------

    public double getHeight(int x, int z) {
        NoiseCache.NoiseEntry vals = this.getEntry(x,z);
        if (vals.height < 0) {
            this.generateHeight(vals);
        }
        return vals.height;
    }
    protected void generateHeight(NoiseCache.NoiseEntry vals) {
        vals.height = 0.5;
        ATG.logger.info("Generate Height for "+vals.x+","+vals.z);
    }



    //------ Temperature ---------------------------------------------------------

    public double getTemperature(int x, int z) {
        NoiseCache.NoiseEntry vals = this.getEntry(x,z);
        if (vals.temperature < 0) {
            this.generateTemperature(vals);
        }
        return vals.temperature;
    }
    protected void generateTemperature(NoiseCache.NoiseEntry vals) {
        if (vals.height < 0) {
            this.generateHeight(vals);
        }
        vals.temperature = 0.5;
        ATG.logger.info("Generate Temperature for "+vals.x+","+vals.z);
    }



    //------ Moisture ---------------------------------------------------------

    public double getMoisture(int x, int z) {
        NoiseCache.NoiseEntry vals = this.getEntry(x,z);
        if (vals.moisture < 0) {
            this.generateMoisture(vals);
        }
        return vals.moisture;
    }
    protected void generateMoisture(NoiseCache.NoiseEntry vals) {
        if (vals.temperature < 0) {
            this.generateTemperature(vals);
        }
        vals.moisture = 0.5;
        ATG.logger.info("Generate Moisture for "+vals.x+","+vals.z);
    }



    //------ Cache class ---------------------------------------------------------

    public static class NoiseCache extends LinkedHashMap<NoiseCache.CoordPair,NoiseCache.NoiseEntry> {

        public NoiseEntry get(int x, int z) {
            return this.get(new NoiseCache.CoordPair(x,z));
        }

        public NoiseEntry put(int x, int z, NoiseEntry value) {
            return this.put(new NoiseCache.CoordPair(x,z), value);
        }

        public boolean containsKey(int x, int z) {
            return this.containsKey(new NoiseCache.CoordPair(x,z));
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > 256;
        }

        // internals
        public static class CoordPair {
            final int x;
            final int z;

            public CoordPair(int x, int z) {
                this.x = x;
                this.z = z;
            }

            @Override
            public int hashCode() {
                int hash = 17;
                hash = ((hash + x) << 5) - (hash + x);
                hash = ((hash + z) << 5) - (hash + z);
                return hash;
            }

            @Override
            public boolean equals(Object other) {
                if (this == other) {
                    return true;
                }
                if (other instanceof CoordPair) {
                    CoordPair oc = (CoordPair)other;
                    return oc.x == this.x && oc.z == this.z;
                }
                return false;
            }
        }

        public static class NoiseEntry {
            public final int x;
            public final int z;

            public double height = -1;
            public double temperature = -1;
            public double moisture = -1;

            public NoiseEntry(int x, int z) {
                this.x = x;
                this.z = z;
            }
        }
    }
}
