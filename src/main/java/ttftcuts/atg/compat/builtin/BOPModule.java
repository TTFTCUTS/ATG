package ttftcuts.atg.compat.builtin;

import net.minecraft.init.Biomes;
import ttftcuts.atg.ATGBiomes;
import ttftcuts.atg.generator.biome.BiomeRegistry.EnumBiomeCategory;
import ttftcuts.atg.settings.BiomeSettingsBuilder;

public class BOPModule extends ProvidedBiomeModule {

    public BOPModule() {
        super("Biomes'O'Plenty Integration", "BiomesOPlenty");

        final double less_common = 0.5;
        double uncommon = 0.3;
        final double very_uncommon = 0.2;
        final double rare = 0.1;
        final double very_rare = 0.04;

        BiomeSettingsBuilder b = new BiomeSettingsBuilder(this.settings, "biomesoplenty");

        //------ Replacements ---------------------------------------------------------

        b.replaceBiome(ATGBiomes.SHRUBLAND, "shrubland");
        b.replaceBiome(ATGBiomes.WOODLAND, "woodland");
        b.replaceBiome(ATGBiomes.GRAVEL_BEACH, "gravel_beach");
        b.replaceBiome(ATGBiomes.TUNDRA, "tundra");

        //------ Biomes ---------------------------------------------------------

        // plains
        b.getGroup(EnumBiomeCategory.LAND, "Plains")
                .addBiome("flower_field", very_uncommon)
                .addBiome("grassland", very_rare)
                .addBiome("lavender_fields", very_uncommon)
                .addBiome("prairie", less_common);


        // forest
        b.getGroup(EnumBiomeCategory.LAND, "Forest")
                .addBiome("cherry_blossom_grove", rare)
                .addBiome("dead_forest", very_uncommon)
                .addBiome("eucalyptus_forest", uncommon)
                .addBiome("fen", less_common)
                .addBiome("mystic_grove", very_rare)
                .addBiome("ominous_woods", very_rare)
                .addBiome("orchard", less_common)
                .addBiome("redwood_forest", uncommon)
                .addBiome("sacred_springs", rare)
                .addBiome("seasonal_forest", uncommon)
                .addBiome("temperate_rainforest", uncommon);


        // taiga
        b.getGroup(EnumBiomeCategory.LAND, "Taiga")
                .addBiome("snowy_coniferous_forest", less_common)
                .addBiome("snowy_forest", uncommon);


        // ice plains
        b.getGroup(EnumBiomeCategory.LAND, "Ice Plains")
                .addBiome("cold_desert", less_common)
                .addBiome("arctic", less_common);


        // jungle
        b.getGroup(EnumBiomeCategory.LAND, "Jungle")
                .addBiome("overgrown_cliffs", very_uncommon)
                .addBiome("bamboo_forest", rare); // might make this sub to jungle?


        // shrubland
        b.getGroup(EnumBiomeCategory.LAND, "Shrubland")
                .addBiome("chaparral", less_common)
                .addBiome("lavender_fields", uncommon)
                .addBiome("meadow", less_common);


        // boreal forest
        b.getGroup(EnumBiomeCategory.LAND, "Boreal Forest")
                .addBiome("boreal_forest")
                .addBiome("maple_woods", less_common)
                .addBiome("shield");


        // tundra
        b.getGroup(EnumBiomeCategory.LAND, "Tundra")
                .addBiome("cold_desert", very_uncommon)
                .addBiome("steppe");


        // savanna
        b.getGroup(EnumBiomeCategory.LAND, "Savanna")
                .addBiome("chaparral", very_uncommon);


        // tropical shrubland
        b.getGroup(EnumBiomeCategory.LAND, "Tropical Shrubland")
                .addBiome("bamboo_forest")
                .addBiome("overgrown_cliffs", rare);


        // woodland
        b.getGroup(EnumBiomeCategory.LAND, "Woodland")
                .addBiome("cherry_blossom_grove", rare)
                .addBiome("eucalyptus_forest", rare)
                .addBiome("mystic_grove", very_rare);


        // dry scrubland
        b.getGroup(EnumBiomeCategory.LAND, "Dry Scrubland")
                .addBiome("brushland")
                .addBiome("lush_desert", very_uncommon)
                .addBiome("outback", less_common)
                .addBiome("steppe")
                .addBiome("xeric_shrubland");


        // swampland
        b.getGroup(EnumBiomeCategory.SWAMP, "Swampland")
                .addBiome("bayou", uncommon)
                .addBiome("bog", uncommon)
                .addBiome("dead_swamp", rare)
                .addBiome("lush_swamp", uncommon)
                .addBiome("marsh", uncommon)
                .addBiome("quagmire", very_uncommon)
                .addBiome("wetland", uncommon)
                .addBiome("mangrove", very_uncommon);


        // ocean
        b.getGroup(EnumBiomeCategory.OCEAN, "Ocean")
                .addBiome("coral_reef", very_uncommon)
                .addBiome("kelp_forest", uncommon);


        final double island = 0.0025;
        // deep ocean
        b.getGroup(EnumBiomeCategory.OCEAN, "Deep Ocean")
                .addBiome("mangrove", island)
                .addBiome("origin_island", island)
                .addBiome("tropical_island", island)
                .addBiome("flower_island", island)
                .addBiome("volcanic_island", island);


        //------ Sub-biomes ---------------------------------------------------------

        final double mutation = 1.0/16.0;

        b.addSubBiome(Biomes.FOREST, "cherry_blossom_grove", mutation);
        b.addSubBiome(Biomes.ICE_PLAINS, "glacier", mutation);
        b.addSubBiome("cold_desert", "glacier", mutation);
        b.addSubBiome(Biomes.DESERT, "oasis", mutation);
        b.addSubBiome(Biomes.ICE_MOUNTAINS, "alps", 0.5);


        final double clearing = 0.1;

        b.addSubBiome("flower_fields", Biomes.PLAINS, clearing);
        b.addSubBiome("flower_fields", "shrubland", clearing);
        b.addSubBiome("flower_fields", "woodland", clearing);
        b.addSubBiome("lavender_fields", Biomes.PLAINS, clearing);
        b.addSubBiome("lavender_fields", "shrubland", clearing);
        b.addSubBiome("lavender_fields", "woodland", clearing);
        b.addSubBiome("prairie", "shrubland", clearing);
        b.addSubBiome("prairie", "woodland", clearing);
        b.addSubBiome(Biomes.PLAINS, "orchard", clearing * 0.5);

        b.addSubBiome(Biomes.FOREST, "grove", clearing * 0.5);
        b.addSubBiome(Biomes.FOREST, "dead_forest", clearing * 0.2);
        b.addSubBiome(Biomes.FOREST_HILLS, "grove", clearing * 0.2);
        b.addSubBiome(Biomes.FOREST_HILLS, "dead_forest", clearing * 0.2);

        b.addSubBiome("seasonal_forest", Biomes.PLAINS, clearing);
        b.addSubBiome("seasonal_forest", "woodland", clearing);
        b.addSubBiome("seasonal_forest", "shrubland", clearing);
        b.addSubBiome("seasonal_forest", "dead_forest", clearing * 0.2);

        b.addSubBiome("temperate_rainforest", Biomes.PLAINS, clearing * 0.5);
        b.addSubBiome("temperate_rainforest", "woodland", clearing * 0.5);
        b.addSubBiome("temperate_rainforest", "shrubland", clearing * 0.5);
        b.addSubBiome("temperate_rainforest", "grove", clearing * 0.5);

        b.addSubBiome("boreal_forest", Biomes.PLAINS, clearing * 2);
        b.addSubBiome("maple_woods", Biomes.PLAINS, clearing * 2);
        b.addSubBiome("shield", Biomes.PLAINS, clearing);
        b.addSubBiome("shield", "grove", clearing);

        b.addSubBiome("snowy_coniferous_forest", Biomes.ICE_PLAINS, clearing * 2);
        b.addSubBiome("snowy_forest", Biomes.ICE_PLAINS, clearing * 2);

        b.addSubBiome(Biomes.FOREST_HILLS, "mountain_foothills", 1.0);

        //------ Hill biomes ---------------------------------------------------------

        double hills = 128/255.0;
        double upperhills = 170/255.0;
        double mountain = 192/255.0;

        b.addHillBiome("mountain_foothills", "mountain_peaks", upperhills);
        b.addHillBiome("mountain_foothills", "alps", mountain);

        b.addHillBiome("mountain_foothills", "alps", upperhills);

        //------ Height mods ---------------------------------------------------------
        b.addHeightModifier("origin_island", "island");
        b.addHeightModifier("tropical_island", "island");
        b.addHeightModifier("flower_island", "island");

        b.addHeightModifier("overgrown_cliffs", "plateau");
        b.addHeightModifier("glacier", "offset").setParameter("height", 10);
    }
}
