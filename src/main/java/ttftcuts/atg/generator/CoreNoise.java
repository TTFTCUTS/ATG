package ttftcuts.atg.generator;

import ttftcuts.atg.ATG;

import java.util.LinkedHashMap;
import java.util.Map;

public class CoreNoise {
    public final long seed;
    protected NoiseCache cache = new NoiseCache();

    public int collisions = 0;

    public CoreNoise(long seed) {
        this.seed = seed;
    }

    public NoiseCache.NoiseEntry getEntry(int x, int z) {
        int coord = NoiseCache.coordHash(x,z);
        if (!this.cache.containsKey(coord)) {
            this.cache.put(coord, new NoiseCache.NoiseEntry(x,z));
        }

        NoiseCache.NoiseEntry vals = this.cache.get(coord);

        if (vals.x != x || vals.z != z){
            //ATG.logger.warn("Coord collision: ("+x+","+z+") vs ("+vals.x+","+vals.z+") at "+coord+", skipping cache.");
            collisions++;
            vals = new NoiseCache.NoiseEntry(x,z);
        }

        return vals;
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
        //ATG.logger.info("Generate Height for "+vals.x+","+vals.z);
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
        //ATG.logger.info("Generate Temperature for "+vals.x+","+vals.z);
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
        //ATG.logger.info("Generate Moisture for "+vals.x+","+vals.z);
    }



    //------ Cache class ---------------------------------------------------------

    public static class NoiseCache extends LinkedHashMap<Integer,NoiseCache.NoiseEntry> {

        public NoiseEntry get(int x, int z) {
            return this.get(coordHash(x,z));
        }

        public NoiseEntry put(int x, int z, NoiseEntry value) {
            return this.put(coordHash(x,z), value);
        }

        public boolean containsKey(int x, int z) {
            return this.containsKey(coordHash(x,z));
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > 256;
        }

        public static int coordHash(int x, int z) {
            //int hash = 17;
            //hash = ((hash + x) << 5) - (hash + x);
            //hash = ((hash + z) << 5) - (hash + z);

            int hash = 31;
            hash = ((hash + x) << 13) - (hash + x);
            hash = ((hash + z) << 13) - (hash + z);
            return hash;
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
