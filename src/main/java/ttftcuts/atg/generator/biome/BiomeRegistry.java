package ttftcuts.atg.generator.biome;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import ttftcuts.atg.ATG;
import ttftcuts.atg.ATGBiomes;
import ttftcuts.atg.generator.CoreNoise;
import ttftcuts.atg.util.MathUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class BiomeRegistry {

    public Map<EnumBiomeCategory, Map<String, BiomeGroup>> biomeGroups;
    public Map<Biome, Map<Biome, Double>> subBiomes;
    public Map<Biome, Double> subWeightTotals;
    public Map<Biome, Map<Biome, Double>> hillBiomes; // sub lists are assumed to be ordered lowest to highest!
    public Map<Biome, IBiomeHeightModifier> heightMods;

    public BiomeRegistry() {
        this.biomeGroups = new HashMap<EnumBiomeCategory, Map<String, BiomeGroup>>();
        this.subBiomes = new HashMap<Biome, Map<Biome, Double>>();
        this.subWeightTotals = new HashMap<Biome, Double>();
        this.hillBiomes = new HashMap<Biome, Map<Biome, Double>>();
        this.heightMods = new HashMap<Biome, IBiomeHeightModifier>();

        for (EnumBiomeCategory category : EnumBiomeCategory.values()) {
            this.biomeGroups.put(category, new HashMap<String, BiomeGroup>());
        }

        this.populate();
    }

    public void populate() {
        //TODO: Here's where the settings would apply packs of changes or whatever on top of the default, but for now it's all pre-set

        Random rand = new Random();

        //------ Land -----------------------

        // Plains
        addGroup(EnumBiomeCategory.LAND, "Plains", 0.8, 0.4, 0.35)
                .addBiome(Biomes.PLAINS);

        // Desert
        addGroup(EnumBiomeCategory.LAND, "Desert", 1.8, 0.2, 0.275) // 2.0 temp
                .setBlobSizeModifier(1) // larger blobs, one power of two greater
                .addBiome(Biomes.DESERT)
                .addBiome(Biomes.MESA, 0.3);

        // Forest
        addGroup(EnumBiomeCategory.LAND, "Forest", 0.7, 0.8, 0.35)
                .addBiome(Biomes.FOREST)
                .addBiome(Biomes.BIRCH_FOREST, 0.3)
                .addBiome(Biomes.ROOFED_FOREST, 0.2);

        // Taiga
        addGroup(EnumBiomeCategory.LAND, "Taiga", 0.05, 0.7, 0.5)
                .addBiome(Biomes.COLD_TAIGA);

        // Ice Plains
        addGroup(EnumBiomeCategory.LAND, "Ice Plains", 0.0, 0.45, 0.3)
                .addBiome(Biomes.ICE_PLAINS);

        // Jungle
        addGroup(EnumBiomeCategory.LAND, "Jungle", 1.75, 0.75, 0.325) // 1.3 temp
                .addBiome(Biomes.JUNGLE);

        // Shrubland
        addGroup(EnumBiomeCategory.LAND, "Shrubland", 0.77, 0.53, 0.35)
                .addBiome(ATGBiomes.SHRUBLAND);

        // Boreal Forest
        addGroup(EnumBiomeCategory.LAND, "Boreal Forest", 0.25, 0.8, 0.35)
                .addBiome(Biomes.TAIGA)
                .addBiome(Biomes.REDWOOD_TAIGA, 0.4);

        // Tundra
        addGroup(EnumBiomeCategory.LAND, "Tundra", 0.25, 0.45, 0.325) // 0.05, 0.65, 0.35
                .addBiome(ATGBiomes.TUNDRA);

        // Steppe
        //addGroup(EnumBiomeCategory.LAND, "Steppe", 0.2, 0.3, 0.5)
        //        .addBiome(ATGBiomes.TUNDRA);

        // Savanna
        addGroup(EnumBiomeCategory.LAND, "Savanna", 1.7, 0.55, 0.275)// 1.5, 0.45, 0.275)
                .addBiome(Biomes.SAVANNA);

        // Tropical Shrubland
        addGroup(EnumBiomeCategory.LAND, "Tropical Shrubland", 1.75, 0.65, 0.35) // 1.3 temp
                .addBiome(ATGBiomes.TROPICAL_SHRUBLAND);

        // Woodland
        addGroup(EnumBiomeCategory.LAND, "Woodland", 0.7, 0.67, 0.3)
                .addBiome(ATGBiomes.WOODLAND);

        // Mesa
        //addGroup(EnumBiomeCategory.LAND, "Mesa", 2.0, 0.0, 0.275, 0.44, 1.0)
        //        .addBiome(Biomes.MESA);

        // Dry Scrubland
        addGroup(EnumBiomeCategory.LAND, "Dry Scrubland", 1.8, 0.35, 0.325) // 1.7, 0.3, 0.275
                .addBiome(ATGBiomes.SCRUBLAND);

        //------ Beach -----------------------

        // Beach
        addGroup(EnumBiomeCategory.BEACH, "Beach", 0.8, 0.4, 0.25)
                .addBiome(Biomes.BEACH);

        // Stone Beach
        addGroup(EnumBiomeCategory.BEACH, "Cold Beach", 0.25, 0.4, 0.25)
                .addBiome(ATGBiomes.GRAVEL_BEACH);

        // Cold Beach
        addGroup(EnumBiomeCategory.BEACH, "Snowy Beach", 0.0, 0.4, 0.25)
                .addBiome(ATGBiomes.GRAVEL_BEACH_SNOWY);



        //------ Swamplands -----------------------

        // Swampland
        addGroup(EnumBiomeCategory.SWAMP, "Swampland", 0.8, 0.9, 0.25)
                .addBiome(Biomes.SWAMPLAND);



        //------ Ocean -----------------------

        Double deep = 28.0 / 255.0;

        // Ocean
        addGroup(EnumBiomeCategory.OCEAN, "Ocean", 0.5, 0.5, 0.25, deep, 1.0)
                .addBiome(Biomes.OCEAN);

        // Deep Ocean
        addGroup(EnumBiomeCategory.OCEAN, "Deep Ocean", 0.5, 0.5, 0.25, 0.0, deep)
                .addBiome(Biomes.DEEP_OCEAN);


        //------ SUB-BIOMES -----------------------

        double mutation = 1.0/28.0;
        double mesa_plateaus = 0.25;

        addSubBiome(Biomes.PLAINS, Biomes.MUTATED_PLAINS, mutation);
        addSubBiome(Biomes.DESERT, Biomes.MUTATED_DESERT, mutation);
        addSubBiome(Biomes.EXTREME_HILLS, Biomes.EXTREME_HILLS_WITH_TREES, mutation);
        addSubBiome(Biomes.EXTREME_HILLS, Biomes.MUTATED_EXTREME_HILLS, mutation);
        addSubBiome(Biomes.EXTREME_HILLS, Biomes.MUTATED_EXTREME_HILLS_WITH_TREES, mutation);
        addSubBiome(Biomes.FOREST, Biomes.MUTATED_FOREST, mutation);
        addSubBiome(Biomes.FOREST_HILLS, Biomes.MUTATED_FOREST, mutation);
        addSubBiome(Biomes.TAIGA, Biomes.MUTATED_TAIGA, mutation);
        addSubBiome(Biomes.ICE_PLAINS, Biomes.MUTATED_ICE_FLATS, mutation);
        addSubBiome(Biomes.BIRCH_FOREST, Biomes.MUTATED_BIRCH_FOREST, mutation);
        addSubBiome(Biomes.BIRCH_FOREST_HILLS, Biomes.MUTATED_BIRCH_FOREST_HILLS, mutation);
        addSubBiome(Biomes.COLD_TAIGA, Biomes.MUTATED_TAIGA_COLD, mutation);
        addSubBiome(Biomes.REDWOOD_TAIGA, Biomes.MUTATED_REDWOOD_TAIGA, mutation);
        addSubBiome(Biomes.REDWOOD_TAIGA_HILLS, Biomes.MUTATED_REDWOOD_TAIGA_HILLS, mutation);
        addSubBiome(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, mutation);
        addSubBiome(Biomes.SAVANNA, Biomes.MUTATED_SAVANNA, mutation);
        addSubBiome(Biomes.SAVANNA, Biomes.MUTATED_SAVANNA_ROCK, mutation);
        addSubBiome(Biomes.MESA, Biomes.MUTATED_MESA, mutation); // bryce
        addSubBiome(Biomes.MESA, Biomes.MESA_ROCK, mesa_plateaus); // plateau F
        addSubBiome(Biomes.MESA, Biomes.MESA_CLEAR_ROCK, mesa_plateaus); // plateau

        //------ HILL BIOMES -----------------------

        addHillBiome(Biomes.FOREST, Biomes.FOREST_HILLS, 0.5);

        //------ HEIGHT MODIFIERS -----------------------

        addHeightModifier(Biomes.DESERT, ATGBiomes.HeightModifiers.DUNES);
    }

    public BiomeGroup addGroup(EnumBiomeCategory category, String name, double temperature, double moisture, double height, double minHeight, double maxHeight) {
        BiomeGroup biomeGroup = new BiomeGroup(name, temperature, moisture, height, minHeight, maxHeight);

        this.biomeGroups.get(category).put(name, biomeGroup);

        return biomeGroup;
    }

    public BiomeGroup addGroup(EnumBiomeCategory category, String name, double temperature, double moisture, double height) {
        return this.addGroup(category, name, temperature, moisture, height, 0.0, 1.0);
    }

    public void addSubBiome(Biome parent, Biome subBiome, double weight) {
        if (!this.subBiomes.containsKey(parent)) {
            this.subBiomes.put(parent, new LinkedHashMap<Biome, Double>());
            this.subWeightTotals.put(parent, 0.0);
        }

        Map<Biome,Double> subs = this.subBiomes.get(parent);
        this.subWeightTotals.put(parent, this.subWeightTotals.get(parent) + weight);

        if (!subs.containsKey(subBiome)) {
            subs.put(subBiome, weight);
        } else {
            subs.put(subBiome, subs.get(subBiome) + weight);
        }
    }

    public Biome getSubBiome(Biome parent, double value) {
        if (!this.subBiomes.containsKey(parent)) {
            return parent;
        }

        double weight = MathUtil.clamp(value, 0.0, 1.0) * (this.subWeightTotals.get(parent) + 1.0);

        if (weight <= 1.0) {
            return parent;
        }

        weight -= 1.0;

        Map<Biome, Double> weights = this.subBiomes.get(parent);

        double total = 0.0;
        for (Map.Entry<Biome, Double> entry : weights.entrySet()) {
            total += entry.getValue();
            if (total >= weight) {
                return entry.getKey();
            }
        }

        return null; // shouldn't happen!
    }

    public void addHillBiome(Biome parent, Biome hillBiome, double height) {
        if (!this.hillBiomes.containsKey(parent)) {
            this.hillBiomes.put(parent, new LinkedHashMap<Biome, Double>());
        }

        Map<Biome, Double> hills = this.hillBiomes.get(parent);
        hills.put(hillBiome, height);
    }

    public Biome getHillBiome(Biome parent, CoreNoise noise, int x, int z) {
        if (!this.hillBiomes.containsKey(parent)) {
            return parent;
        }

        Map<Biome, Double> hills = this.hillBiomes.get(parent);

        double height = noise.getHeight(x,z) + noise.getRoughness(x,z) * 0.1;

        Biome biome = parent;

        for (Map.Entry<Biome,Double> e : hills.entrySet()) {
            if (height > e.getValue()) {
                biome = e.getKey();
            } else {
                return biome;
            }
        }

        return biome;
    }

    public void addHeightModifier(Biome biome, IBiomeHeightModifier mod) {
        this.heightMods.put(biome, mod);
    }

    public IBiomeHeightModifier getHeightModifier(Biome biome) {
        return this.heightMods.get(biome);
    }

    //------ BiomeGroup type enum ---------------------------------------------------------

    public enum EnumBiomeCategory {
        LAND(Biomes.PLAINS),
        OCEAN(Biomes.OCEAN),
        BEACH(Biomes.BEACH),
        SWAMP(Biomes.SWAMPLAND),
        ;

        public final BiomeGroup fallback;

        EnumBiomeCategory(Biome fallback) {
            this.fallback = new BiomeGroup(this.name()+"_fallback", 0.5,0.5,0.25);
            this.fallback.addBiome(fallback);
        }
    }

    //------ BiomeGroup Class ---------------------------------------------------------

    public static class BiomeGroup {
        public String name;
        public double temperature;
        public double moisture;
        public double height;
        public double minHeight;
        public double maxHeight;
        public int blobSizeModifier = 0;

        public long salt;
        public int offsetx;
        public int offsetz;

        public Map<Biome, Double> biomes;
        public double totalweight = 0.0;

        public BiomeGroup(String name, double temperature, double moisture, double height, double minHeight, double maxHeight) {
            this.name = name;
            this.temperature = temperature;
            this.moisture = moisture;
            this.height = height;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.salt = name.hashCode();

            this.offsetx = (int)( ( MathUtil.xorShift64( 2846 * MathUtil.xorShift64(salt + 7391834) - salt ) ) % Integer.MAX_VALUE);
            this.offsetz = (int)( ( MathUtil.xorShift64( 9672 * MathUtil.xorShift64(salt + 4517384) - salt ) ) % Integer.MAX_VALUE);

            this.biomes = new LinkedHashMap<Biome, Double>();
        }

        public BiomeGroup(String name, double temperature, double moisture, double height) {
            this(name, temperature, moisture, height, 0.0, 1.0);
        }

        public BiomeGroup(String name, double temperature, double moisture) {
            this(name, temperature, moisture, 0.5);
        }

        public BiomeGroup addBiome(Biome biome, double weight) {
            if (biome != null) {
                if (!this.biomes.containsKey(biome)) {
                    this.biomes.put(biome, weight);
                } else {
                    this.biomes.put(biome, this.biomes.get(biome) + weight);
                }
                this.totalweight += weight;
            }
            return this;
        }

        public BiomeGroup addBiome(Biome biome) {
            return this.addBiome(biome, 1.0);
        }

        public Biome getBiome(double value) {
            double weight = MathUtil.clamp(value,0.0,1.0) * this.totalweight;

            double total = 0.0;
            for (Map.Entry<Biome, Double> entry : this.biomes.entrySet()) {
                total += entry.getValue();
                if (total >= weight) {
                    return entry.getKey();
                }
            }

            return null; // shouldn't happen!
        }

        public double getClassificationScore(Biome biome) {
            return 0.0;
        }

        public BiomeGroup setBlobSizeModifier(int size) {
            this.blobSizeModifier = size;
            return this;
        }
    }
}
