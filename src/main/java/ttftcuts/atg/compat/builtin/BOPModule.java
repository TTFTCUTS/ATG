package ttftcuts.atg.compat.builtin;

import ttftcuts.atg.ATGBiomes;
import ttftcuts.atg.generator.biome.BiomeRegistry.EnumBiomeCategory;
import ttftcuts.atg.settings.BiomeSettingsBuilder;

public class BOPModule extends ProvidedBiomeModule {

    public BOPModule() {
        super("Biomes'O'Plenty Integration", "BiomesOPlenty");

        final double island = 0.02;

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
                .addBiome("lavender_fields", uncommon)
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
                .addBiome("cold_desert", less_common);


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
                .addBiome("eucalyptus_forest", uncommon)
                .addBiome("grove", less_common)
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


        // deep ocean
        b.getGroup(EnumBiomeCategory.OCEAN, "Deep Ocean")
                .addBiome("mangrove", island)
                .addBiome("origin_island", island)
                .addBiome("tropical_island", island)
                .addBiome("flower_island", island);


        //------ Sub-biomes ---------------------------------------------------------

        double mutation = 1.0/16.0;


        double clearing = 0.1;

        //------ Hill biomes ---------------------------------------------------------

        //------ Height mods ---------------------------------------------------------
        b.addHeightModifier("origin_island", "island");
        b.addHeightModifier("tropical_island", "island");
        b.addHeightModifier("flower_island", "island");
    }
}
