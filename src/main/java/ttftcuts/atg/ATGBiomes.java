package ttftcuts.atg;

import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import ttftcuts.atg.biome.BiomeShrubland;
import ttftcuts.atg.biome.BiomeWoodland;

public abstract class ATGBiomes {

    public static Biome SHRUBLAND;
    public static Biome WOODLAND;

    public static WorldGenShrub OAK_SHRUB;

    public static void init() {

        SHRUBLAND = register(141, "atg_shrubland", new BiomeShrubland(), true, BiomeDictionary.Type.PLAINS);
        WOODLAND = register(142, "atg_woodland", new BiomeWoodland(), false);

        OAK_SHRUB = new WorldGenShrub(
                Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK),
                Blocks.LEAVES.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK)
        );

    }

    public static Biome register(int id, String name, Biome biome, boolean villages, BiomeDictionary.Type... dictionaryTypes) {
        Biome.registerBiome(id, name, biome);

        if (villages) {
            BiomeManager.addVillageBiome(biome, true);
        }

        if (dictionaryTypes.length > 0) {
            BiomeDictionary.registerBiomeType(biome, dictionaryTypes);
        }

        return biome;
    }
}
