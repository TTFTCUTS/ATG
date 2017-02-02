package ttftcuts.atg.generator.biome;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import ttftcuts.atg.util.MathUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BiomeRegistry {

    public Map<EnumBiomeCategory, Map<String, Group>> biomeGroups;
    public Map<Biome, Map<Biome, Double>> subBiomes;

    public BiomeRegistry() {
        this.biomeGroups = new HashMap<EnumBiomeCategory, Map<String, Group>>();
        this.subBiomes = new HashMap<Biome, Map<Biome, Double>>();

        for (EnumBiomeCategory category : EnumBiomeCategory.values()) {
            this.biomeGroups.put(category, new HashMap<String, Group>());
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
        addGroup(EnumBiomeCategory.LAND, "Desert", 2.0, 0.0, 0.275)
                .addBiome(Biomes.DESERT);

        // Forest
        addGroup(EnumBiomeCategory.LAND, "Forest", 0.7, 0.8, 0.35)
                .addBiome(Biomes.FOREST)
                .addBiome(Biomes.BIRCH_FOREST, 0.8)
                .addBiome(Biomes.ROOFED_FOREST, 0.5);

        // Taiga
        addGroup(EnumBiomeCategory.LAND, "Taiga", 0.05, 0.7, 0.5)
                .addBiome(Biomes.COLD_TAIGA);

        // Ice Plains
        addGroup(EnumBiomeCategory.LAND, "Ice Plains", 0.0, 0.45, 0.3)
                .addBiome(Biomes.ICE_PLAINS);

        // Jungle
        addGroup(EnumBiomeCategory.LAND, "Jungle", 1.3, 0.75, 0.325)
                .addBiome(Biomes.JUNGLE);

        // Shrubland
        addGroup(EnumBiomeCategory.LAND, "Shrubland", 0.77, 0.53, 0.35);

        // Boreal Forest
        addGroup(EnumBiomeCategory.LAND, "Boreal Forest", 0.25, 0.8, 0.35)
                .addBiome(Biomes.TAIGA)
                .addBiome(Biomes.REDWOOD_TAIGA, 0.5);

        // Tundra
        addGroup(EnumBiomeCategory.LAND, "Tundra", 0.05, 0.65, 0.35);

        // Steppe
        addGroup(EnumBiomeCategory.LAND, "Steppe", 0.2, 0.3, 0.5);

        // Savanna
        addGroup(EnumBiomeCategory.LAND, "Savanna", 1.5, 0.45, 0.275, 0.0, 0.36)
                .addBiome(Biomes.SAVANNA);

        // Tropical Shrubland
        addGroup(EnumBiomeCategory.LAND, "Tropical Shrubland", 1.3, 0.65, 0.35);

        // Woodland
        addGroup(EnumBiomeCategory.LAND, "Woodland", 0.7, 0.67, 0.3);

        // Mesa
        addGroup(EnumBiomeCategory.LAND, "Mesa", 2.0, 0.0, 0.275, 0.44, 1.0)
                .addBiome(Biomes.MESA);



        //------ Beach -----------------------

        // Beach
        addGroup(EnumBiomeCategory.BEACH, "Beach", 0.8, 0.4, 0.25)
                .addBiome(Biomes.BEACH);

        // Stone Beach
        addGroup(EnumBiomeCategory.BEACH, "Stone Beach", 0.25, 0.4, 0.25)
                .addBiome(Biomes.STONE_BEACH);

        // Cold Beach
        addGroup(EnumBiomeCategory.BEACH, "Cold Beach", 0.0, 0.4, 0.25)
                .addBiome(Biomes.COLD_BEACH);



        //------ Swamplands -----------------------

        // Swampland
        addGroup(EnumBiomeCategory.SWAMP, "Swampland", 0.8, 0.9, 0.25)
                .addBiome(Biomes.SWAMPLAND);



        //------ Ocean -----------------------

        // Ocean
        addGroup(EnumBiomeCategory.OCEAN, "Ocean", 0.5, 0.5, 0.25)
                .addBiome(Biomes.OCEAN);
    }

    public Group addGroup(EnumBiomeCategory category, String name, double temperature, double moisture, double height, double minHeight, double maxHeight) {
        Group group = new Group(name, temperature, moisture, height, minHeight, maxHeight);

        this.biomeGroups.get(category).put(name, group);

        return group;
    }

    public Group addGroup(EnumBiomeCategory category, String name, double temperature, double moisture, double height) {
        return this.addGroup(category, name, temperature, moisture, height, 0.0, 1.0);
    }

    //------ Group type enum ---------------------------------------------------------

    public enum EnumBiomeCategory {
        LAND(Biomes.PLAINS),
        OCEAN(Biomes.OCEAN),
        BEACH(Biomes.BEACH),
        SWAMP(Biomes.SWAMPLAND),
        ;

        public final Biome fallback;

        EnumBiomeCategory(Biome fallback) {
            this.fallback = fallback;
        }
    }

    //------ Group Class ---------------------------------------------------------

    public static class Group {
        public String name;
        public double temperature;
        public double moisture;
        public double height;
        public double minHeight;
        public double maxHeight;

        public long salt;
        public int offsetx;
        public int offsetz;

        public Map<Biome, Double> biomes;
        public double totalweight = 0.0;

        public Group(String name, double temperature, double moisture, double height, double minHeight, double maxHeight) {
            this.name = name;
            this.temperature = temperature;
            this.moisture = moisture;
            this.height = height;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.salt = name.hashCode();

            this.offsetx = (int)( ( MathUtil.xorShift64( 2846 * MathUtil.xorShift64(salt + 7391834) - salt ) ) % Integer.MAX_VALUE);
            this.offsetz = (int)( ( MathUtil.xorShift64( 9672 * MathUtil.xorShift64(salt + 4517384) - salt ) ) % Integer.MAX_VALUE);

            this.biomes = new HashMap<Biome, Double>();
        }

        public Group(String name, double temperature, double moisture, double height) {
            this(name, temperature, moisture, height, 0.0, 1.0);
        }

        public Group(String name, double temperature, double moisture) {
            this(name, temperature, moisture, 0.5);
        }

        public Group addBiome(Biome biome, double weight) {
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

        public Group addBiome(Biome biome) {
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
    }
}
