package ttftcuts.atg.generator;

import ttftcuts.atg.ATG;
import ttftcuts.atg.noise.*;
import ttftcuts.atg.util.MathUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class CoreNoise {
    //------ Cache Fields ---------------------------------------------------------

    protected NoiseCache cache = new NoiseCache();
    public int collisions = 0;



    //------ Noise Fields ---------------------------------------------------------

    public final long seed;

    protected Noise ledges;
    protected Noise lumps;
    protected Noise ridges;
    protected Noise oceans;
    protected Noise dunes;
    protected Noise roughness;
    protected Noise swamps;
    protected Noise temperature;
    protected Noise moisture;

    //------ Init ---------------------------------------------------------

    public CoreNoise(long seed) {
        this.seed = seed;

        double scale = 100.0;

        Random rand = new Random(seed);

        this.ledges = new OctaveNoise(rand, scale * 5.0, 2);

        this.lumps = new JordanTurbulence(rand, scale * 3.0, 6, 2.0, 0.8, 0.65, 0.4, 0.45, 1.0, 0.6, 1.0, 2, 0.15, 0.25, 0.5);
        this.ridges = new RidgeNoise(rand, scale * 6.0, 5);

        this.oceans = new OctaveNoise(rand, scale * 10.0, 4);
        this.dunes = new DuneNoise(rand, scale * 0.3, 0.2);
        
        this.roughness = new OctaveNoise(rand, scale * 0.2, 3);

        this.swamps = new OctaveNoise(rand, scale * 8.0, 4, 2.0, 0.75);

        this.temperature = new TailoredNoise(rand, 1281,0.87, 119,0.07, 26,0.06);
        this.moisture = new TailoredNoise(rand, 400,0.76, 243,0.16, 53,0.08);
    }

    //------ Height ---------------------------------------------------------

    public double getHeight(int x, int z) {
        NoiseCache.NoiseEntry vals = this.getEntry(x,z);
        if (vals.height < 0) {
            this.generateHeight(vals);
        }
        return vals.height;
    }

    public double getInland(int x, int z) {
        NoiseCache.NoiseEntry vals = this.getEntry(x,z);
        if (vals.inland < 0) {
            this.generateHeight(vals); // also sets inland
        }
        return vals.inland;
    }

    public double getSwamp(int x, int z) {
        NoiseCache.NoiseEntry vals = this.getEntry(x,z);
        if (vals.swamp < 0) {
            this.generateHeight(vals); // also sets swamp
        }
        return vals.swamp;
    }

    protected void generateHeight(NoiseCache.NoiseEntry vals) {
        vals.height = 0.0;

        double lump = lumps.getValue(vals.x,vals.z);
        double ridge = ridges.getValue(vals.x,vals.z);
        double ocean = oceans.getValue(vals.x,vals.z);

        vals.inland = ocean;

        double islands = (lump*lump - 0.5) * 0.3 + 0.25 + (ocean+0.2) * 0.4;//0.3;

        double ridgelayer = ridge * 0.45 + lump * (0.05 + ridge * 0.25);

        vals.height += MathUtil.polymax(islands, ridgelayer * (ocean + 0.3), 0.2); // 0.15

        /*if (vals.height < 0.05) {
            vals.height = 0.6;
        }*/

        if (vals.height <= 0.2) {
            double abyss = dunes.getValue(vals.x,vals.z) * 0.05 + 0.08;
            vals.height = MathUtil.polymax(vals.height, abyss, 0.1);
        }

        vals.swamp = 0.0;
        if (vals.height >= 0.225 && vals.height < 0.3) {
            double swamp = MathUtil.clamp(swamps.getValue(vals.x, vals.z) * 3.0, 0.0,1.0) + ocean * 0.5;
            if (swamp > 0) {
                if (vals.height < 0.25) {
                    double factor = MathUtil.smoothrange(vals.height, 0.225, 0.25);
                    swamp *= factor;
                } else {
                    double factor = MathUtil.smoothrange(vals.height, 0.25, 0.3);
                    swamp *= 1 - factor;
                }

                if (swamp > 0) {
                    double swamplevel = MathUtil.plateau(vals.height, 57,64,77, 5.0, false);

                    vals.height = (1-swamp) * vals.height + swamp * swamplevel;
                }

                vals.swamp = Math.max(0.0, swamp);
            }
        }
        
        double ledge1 = Math.min(0.975, ledges.getValue(-vals.x + 34273 ,vals.z + 86269) * 1.15);
        ledge1 = ledge1 * ledge1;

        double ledgelumpfactor = 0.0;

        if (ledge1 > 0.05) {
            double ledgefactor = Math.min(1.0, (ledge1 - 0.05) * 3.0);
            double ledgelevel = vals.height;

            ledgelevel = MathUtil.plateau(ledgelevel, 60,68,75, 2.0, false);
            ledgelevel = MathUtil.plateau(ledgelevel, 70,80,90, 3.0, false);

            double diff = Math.abs(vals.height - ledgelevel);

            vals.height = vals.height * (1-ledgefactor) + ledgelevel * ledgefactor;

            ledgelumpfactor += diff * ledgefactor;
        }

        double ledge2 = Math.min(0.975, ledges.getValue(vals.x,vals.z) * 1.15);
        ledge2 = ledge2 * ledge2;

        if (ledge2 > 0.375) {
            double ledgefactor = Math.min(1.0, (ledge2 - 0.375) * 6.0);
            double ledgelevel = vals.height;

            ledgelevel = MathUtil.plateau(ledgelevel, 60,70,85, 2.0, false);
            ledgelevel = MathUtil.plateau(ledgelevel, 85,100,110, 3.0, false);
            ledgelevel = MathUtil.plateau(ledgelevel, 120,140,145, 3.0, false);
            ledgelevel = MathUtil.plateau(ledgelevel, 50,64,66, 2.0, false);

            double diff = Math.abs(vals.height - ledgelevel);

            vals.height = vals.height * (1-ledgefactor) + ledgelevel * ledgefactor;

            ledgelumpfactor += diff * ledgefactor;
        }

        double rough = roughness.getValue(vals.x,vals.z);

        if (ledgelumpfactor > 0.0) {
            vals.height += ledgelumpfactor * (rough + 1.0) * 0.5;
        }

        if (vals.height > 0.5) {
            vals.height += (vals.height - 0.5) * 0.005 * rough;
        }

        vals.height += rough * 0.00125;
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
        double height = this.getHeight(vals.x, vals.z);
        double inland = this.getInland(vals.x, vals.z);

        double inlandfactor = Math.max(0, inland-0.5);
        double heightfactor = Math.max(0, height*2 - 0.9);

        double mix = this.temperature.getValue(vals.x,vals.z) * 1.3 - 0.2;

        vals.temperature = Math.max(0, mix + inlandfactor * 0.5 - heightfactor * 0.85);
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
        double temp = this.getTemperature(vals.x, vals.z);
        double inland = this.getInland(vals.x, vals.z);

        double inlandfactor = Math.max( -0.1, inland-0.5 );
        double tempfactor = temp - 0.45 + inlandfactor*0.9;

        double mix = this.moisture.getValue(vals.x, vals.z) * 1.2 - 0.05;

        vals.moisture = Math.max(0, Math.min(1, mix-tempfactor*0.35));
        //ATG.logger.info("Generate Moisture for "+vals.x+","+vals.z);
    }

    //------ Cache ---------------------------------------------------------

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
            public double inland = -1;
            public double swamp = -1;

            public NoiseEntry(int x, int z) {
                this.x = x;
                this.z = z;
            }
        }
    }
}
