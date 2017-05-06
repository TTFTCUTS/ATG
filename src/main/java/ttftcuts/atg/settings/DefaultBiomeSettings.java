package ttftcuts.atg.settings;

import net.minecraft.init.Biomes;
import ttftcuts.atg.ATGBiomes;
import ttftcuts.atg.generator.biome.BiomeRegistry.EnumBiomeCategory;

public class DefaultBiomeSettings extends BiomeSettings {
    public DefaultBiomeSettings() {
        super();

        BiomeSettingsBuilder b = new BiomeSettingsBuilder(this);

        // Plains
        b.addGroup(EnumBiomeCategory.LAND, "Plains", 0.8, 0.4, 0.35)
                .addBiome(Biomes.PLAINS);

        // Desert
        b.addGroup(EnumBiomeCategory.LAND, "Desert", 1.8, 0.2, 0.275)
                .setBlobSizeModifier(1) // larger blobs, one power of two greater
                .addBiome(Biomes.DESERT)
                .addBiome(Biomes.MESA, 0.3);

        // Forest
        b.addGroup(EnumBiomeCategory.LAND, "Forest", 0.7, 0.8, 0.35)
                .addBiome(Biomes.FOREST)
                .addBiome(Biomes.BIRCH_FOREST, 0.3)
                .addBiome(Biomes.ROOFED_FOREST, 0.2);

        // Taiga
        b.addGroup(EnumBiomeCategory.LAND, "Taiga", 0.05, 0.7, 0.4) // height 0.5
                .addBiome(Biomes.COLD_TAIGA);

        // Ice Plains
        b.addGroup(EnumBiomeCategory.LAND, "Ice Plains", 0.0, 0.45, 0.3)
                .addBiome(Biomes.ICE_PLAINS);

        // Jungle
        b.addGroup(EnumBiomeCategory.LAND, "Jungle", 1.75, 0.75, 0.325)
                .addBiome(Biomes.JUNGLE);

        // Shrubland
        b.addGroup(EnumBiomeCategory.LAND, "Shrubland", 0.77, 0.53, 0.35)
                .addBiome(ATGBiomes.SHRUBLAND);

        // Boreal Forest
        b.addGroup(EnumBiomeCategory.LAND, "Boreal Forest", 0.4, 0.8, 0.4) // temp 0.25, height 0.35
                .addBiome(Biomes.TAIGA)
                .addBiome(Biomes.REDWOOD_TAIGA, 0.4);

        // Tundra
        b.addGroup(EnumBiomeCategory.LAND, "Tundra", 0.25, 0.45, 0.325)
                .addBiome(ATGBiomes.TUNDRA);

        // Savanna
        b.addGroup(EnumBiomeCategory.LAND, "Savanna", 1.7, 0.55, 0.275)
                .setSubBlobSizeModifier(1)
                .addBiome(Biomes.SAVANNA);

        // Tropical Shrubland
        b.addGroup(EnumBiomeCategory.LAND, "Tropical Shrubland", 1.75, 0.65, 0.35)
                .addBiome(ATGBiomes.TROPICAL_SHRUBLAND);

        // Woodland
        b.addGroup(EnumBiomeCategory.LAND, "Woodland", 0.7, 0.67, 0.3)
                .addBiome(ATGBiomes.WOODLAND);

        // Dry Scrubland
        b.addGroup(EnumBiomeCategory.LAND, "Dry Scrubland", 1.8, 0.35, 0.325)
                .addBiome(ATGBiomes.SCRUBLAND);

        //------ Beach -----------------------

        // Beach
        b.addGroup(EnumBiomeCategory.BEACH, "Beach", 0.6, 0.4, 0.25) // 0.8, 0.4, 0.25
                .addBiome(Biomes.BEACH);

        // Stone Beach
        b.addGroup(EnumBiomeCategory.BEACH, "Cold Beach", 0.34, 0.5, 0.25) // 0.25, 0.4, 0.25
                .addBiome(ATGBiomes.GRAVEL_BEACH);

        // Cold Beach
        b.addGroup(EnumBiomeCategory.BEACH, "Snowy Beach", 0.0, 0.5, 0.26) // 0.0, 0.4, 0.25
                .addBiome(ATGBiomes.GRAVEL_BEACH_SNOWY);



        //------ Swamplands -----------------------

        // Swampland
        b.addGroup(EnumBiomeCategory.SWAMP, "Swampland", 0.8, 0.9, 0.25)
                .addBiome(Biomes.SWAMPLAND);



        //------ Ocean -----------------------

        Double deep = 28.0 / 255.0;

        // Ocean
        b.addGroup(EnumBiomeCategory.OCEAN, "Ocean", 0.5, 0.5, 0.25, deep, 1.0)
                .addBiome(Biomes.OCEAN);

        // Deep Ocean
        b.addGroup(EnumBiomeCategory.OCEAN, "Deep Ocean", 0.5, 0.5, 0.25, 0.0, deep)
                .addBiome(Biomes.DEEP_OCEAN)
                .addBiome(Biomes.MUSHROOM_ISLAND, 0.002);


        //------ SUB-BIOMES -----------------------

        // mutations
        double mutation = 1.0/15.0;

        b.addSubBiome(Biomes.PLAINS, Biomes.MUTATED_PLAINS, mutation);
        b.addSubBiome(Biomes.DESERT, Biomes.MUTATED_DESERT, mutation);
        b.addSubBiome(Biomes.EXTREME_HILLS, Biomes.MUTATED_EXTREME_HILLS, mutation);
        b.addSubBiome(Biomes.EXTREME_HILLS, Biomes.MUTATED_EXTREME_HILLS_WITH_TREES, mutation);
        b.addSubBiome(Biomes.FOREST, Biomes.MUTATED_FOREST, mutation);
        b.addSubBiome(Biomes.FOREST, Biomes.ROOFED_FOREST, mutation);
        b.addSubBiome(Biomes.FOREST_HILLS, Biomes.MUTATED_FOREST, mutation);
        b.addSubBiome(Biomes.ROOFED_FOREST, Biomes.MUTATED_ROOFED_FOREST, mutation);
        b.addSubBiome(Biomes.TAIGA, Biomes.MUTATED_TAIGA, mutation);
        b.addSubBiome(Biomes.TAIGA, Biomes.FOREST, mutation);
        b.addSubBiome(Biomes.ICE_PLAINS, Biomes.MUTATED_ICE_FLATS, mutation);
        b.addSubBiome(Biomes.BIRCH_FOREST, Biomes.MUTATED_BIRCH_FOREST, mutation);
        b.addSubBiome(Biomes.BIRCH_FOREST_HILLS, Biomes.MUTATED_BIRCH_FOREST_HILLS, mutation);
        b.addSubBiome(Biomes.COLD_TAIGA, Biomes.MUTATED_TAIGA_COLD, mutation);
        b.addSubBiome(Biomes.REDWOOD_TAIGA, Biomes.MUTATED_REDWOOD_TAIGA, mutation);
        b.addSubBiome(Biomes.REDWOOD_TAIGA_HILLS, Biomes.MUTATED_REDWOOD_TAIGA_HILLS, mutation);
        b.addSubBiome(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, mutation * 0.5);
        b.addSubBiome(Biomes.SAVANNA, Biomes.MUTATED_SAVANNA, mutation * 0.15);
        b.addSubBiome(Biomes.SAVANNA, Biomes.MUTATED_SAVANNA_ROCK, mutation * 0.15);
        b.addSubBiome(Biomes.MESA, Biomes.MUTATED_MESA_ROCK, mutation);
        b.addSubBiome(Biomes.MESA, Biomes.MUTATED_MESA_CLEAR_ROCK, mutation);

        // mesa plateaus
        double mesa_plateaus = 0.25;
        b.addSubBiome(Biomes.MESA, Biomes.MESA_ROCK, mesa_plateaus); // plateau F
        b.addSubBiome(Biomes.MESA, Biomes.MESA_CLEAR_ROCK, mesa_plateaus); // plateau
        b.addSubBiome(Biomes.MESA, Biomes.MUTATED_MESA, mesa_plateaus); // bryce

        // copses and clearings
        double clearing = 0.10;
        b.addSubBiome(Biomes.PLAINS, ATGBiomes.WOODLAND, clearing);
        b.addSubBiome(Biomes.PLAINS, ATGBiomes.SHRUBLAND, clearing);
        b.addSubBiome(ATGBiomes.SHRUBLAND, ATGBiomes.WOODLAND, clearing);
        b.addSubBiome(ATGBiomes.SHRUBLAND, Biomes.FOREST, clearing);
        b.addSubBiome(ATGBiomes.TUNDRA, Biomes.TAIGA, clearing);
        b.addSubBiome(Biomes.EXTREME_HILLS, Biomes.EXTREME_HILLS_WITH_TREES, clearing);

        b.addSubBiome(Biomes.FOREST, Biomes.PLAINS, clearing);
        b.addSubBiome(Biomes.FOREST, ATGBiomes.WOODLAND, clearing);
        b.addSubBiome(Biomes.FOREST, ATGBiomes.SHRUBLAND, clearing);
        b.addSubBiome(Biomes.FOREST_HILLS, Biomes.PLAINS, clearing);
        b.addSubBiome(Biomes.FOREST_HILLS, ATGBiomes.WOODLAND, clearing);
        b.addSubBiome(Biomes.FOREST_HILLS, ATGBiomes.SHRUBLAND, clearing);
        b.addSubBiome(Biomes.BIRCH_FOREST, Biomes.PLAINS, clearing);
        b.addSubBiome(Biomes.BIRCH_FOREST, ATGBiomes.SHRUBLAND, clearing);
        b.addSubBiome(Biomes.BIRCH_FOREST_HILLS, Biomes.PLAINS, clearing);
        b.addSubBiome(Biomes.BIRCH_FOREST_HILLS, ATGBiomes.SHRUBLAND, clearing);
        b.addSubBiome(Biomes.ROOFED_FOREST, ATGBiomes.WOODLAND, clearing);
        b.addSubBiome(Biomes.ROOFED_FOREST, ATGBiomes.SHRUBLAND, clearing);
        b.addSubBiome(Biomes.TAIGA, Biomes.PLAINS, clearing*2);
        b.addSubBiome(Biomes.TAIGA_HILLS, Biomes.PLAINS, clearing*2);
        b.addSubBiome(Biomes.COLD_TAIGA, Biomes.ICE_PLAINS, clearing*2);
        b.addSubBiome(Biomes.COLD_TAIGA_HILLS, Biomes.ICE_MOUNTAINS, clearing*2);
        b.addSubBiome(Biomes.ICE_MOUNTAINS, Biomes.COLD_TAIGA_HILLS, clearing);

        //------ HILL BIOMES -----------------------

        double hills = 128/255.0;
        double upperhills = 170/255.0;
        double mountain = 192/255.0;

        b.addHillBiome(Biomes.PLAINS, Biomes.EXTREME_HILLS, upperhills);
        b.addHillBiome(Biomes.FOREST, Biomes.FOREST_HILLS, hills);
        b.addHillBiome(Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, hills);
        b.addHillBiome(Biomes.TAIGA, Biomes.TAIGA_HILLS, hills);
        b.addHillBiome(Biomes.COLD_TAIGA, Biomes.COLD_TAIGA_HILLS, hills);
        b.addHillBiome(Biomes.COLD_TAIGA, Biomes.ICE_MOUNTAINS, mountain);
        b.addHillBiome(Biomes.JUNGLE, Biomes.JUNGLE_HILLS, hills);
        b.addHillBiome(Biomes.ICE_PLAINS, Biomes.ICE_MOUNTAINS, mountain);
        b.addHillBiome(ATGBiomes.TUNDRA, Biomes.EXTREME_HILLS, upperhills);
        b.addHillBiome(ATGBiomes.TUNDRA, Biomes.ICE_MOUNTAINS, mountain);
        b.addHillBiome(ATGBiomes.SHRUBLAND, Biomes.EXTREME_HILLS, mountain);
        b.addHillBiome(Biomes.DESERT, Biomes.DESERT_HILLS, hills);


        //------ HEIGHT MODIFIERS -----------------------

        b.addHeightModifier(Biomes.DESERT, "dunes");
        b.addHeightModifier(Biomes.MUSHROOM_ISLAND, "island");
        b.addHeightModifier(Biomes.SAVANNA_PLATEAU, "plateau");
        b.addHeightModifier(Biomes.MUTATED_SAVANNA, "plateau")
                .setParameter("stepsize", 36)
                .setParameter("magnitude", 0.2)
                .setParameter("riftdepth", 0.3);
        b.addHeightModifier(Biomes.MUTATED_SAVANNA_ROCK, "plateau")
                .setParameter("stepsize", 36)
                .setParameter("magnitude", 0.2)
                .setParameter("riftdepth", 0.3);
        b.addHeightModifier(Biomes.MUTATED_ROOFED_FOREST, "plateau");

        b.addHeightModifier(Biomes.MESA, "mesa");
        b.addHeightModifier(Biomes.MESA_ROCK, "mesa").setParameter("variant", 1); // plateau F
        b.addHeightModifier(Biomes.MESA_CLEAR_ROCK, "mesa").setParameter("variant", 1); // plateau
        b.addHeightModifier(Biomes.MUTATED_MESA, "mesa").setParameter("spires", true); // bryce
        b.addHeightModifier(Biomes.MUTATED_MESA_ROCK, "mesa").setParameter("variant", 1); // plateau F M
        b.addHeightModifier(Biomes.MUTATED_MESA_CLEAR_ROCK, "mesa").setParameter("variant", 1); // plateau M
    }
}
