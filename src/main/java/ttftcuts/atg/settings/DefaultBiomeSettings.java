package ttftcuts.atg.settings;

import net.minecraft.init.Biomes;
import ttftcuts.atg.generator.biome.BiomeRegistry.EnumBiomeCategory;

public class DefaultBiomeSettings extends BiomeSettings {
    public DefaultBiomeSettings() {
        super();

        /*addGroup(EnumBiomeCategory.LAND, "test getGroup", 1.0, 0.2, 0.75);

        addBiome(EnumBiomeCategory.LAND, "Plains", Biomes.MUSHROOM_ISLAND, 0.5);
        addBiome(EnumBiomeCategory.LAND, "Forest", Biomes.MUSHROOM_ISLAND, 0.25);
        addBiome(EnumBiomeCategory.OCEAN, "Ocean", Biomes.SWAMPLAND, 0.5);

        addReplacement(Biomes.OCEAN, Biomes.FOREST);

        SubBiomeEntry subtest = new SubBiomeEntry();
        subtest.name = Biomes.MUSHROOM_ISLAND.getRegistryName();
        subtest.parentBiome = Biomes.FOREST.getRegistryName();
        this.subBiomes.put(subtest.getMapKey(), subtest);*/

        BiomeSettingsBuilder b = new BiomeSettingsBuilder(this);

        b.addGroup(EnumBiomeCategory.LAND, "test getGroup", 1.0, 0.2, 0.75);

        b.getGroup(EnumBiomeCategory.LAND, "Plains")
                .addBiome(Biomes.MUSHROOM_ISLAND, 0.5);

        b.getGroup(EnumBiomeCategory.LAND, "Forest")
                .addBiome(Biomes.MUSHROOM_ISLAND, 0.25);

        b.getGroup(EnumBiomeCategory.OCEAN, "Ocean")
                .addBiome(Biomes.SWAMPLAND, 0.5);

        b.replaceBiome(Biomes.OCEAN, Biomes.FOREST);
    }
}
