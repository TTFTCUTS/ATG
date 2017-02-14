package ttftcuts.atg.biome;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import ttftcuts.atg.ATGBiomes;

import java.util.Random;

public class BiomeTropicalShrubland extends Biome {
    public BiomeTropicalShrubland() {
        super(new Biome.BiomeProperties("Tropical Shrubland")
                .setBaseHeight(0.105f)
                .setHeightVariation(0.15f)
                .setTemperature(1.1f)
                .setRainfall(0.45f)
        );

        this.theBiomeDecorator.treesPerChunk = 5;
        this.theBiomeDecorator.grassPerChunk = 10;
        this.theBiomeDecorator.flowersPerChunk = 4;
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.GRASS);

        for (int i = 0; i < 4; ++i)
        {
            int j = rand.nextInt(16) + 8;
            int k = rand.nextInt(16) + 8;
            int l = rand.nextInt(worldIn.getHeight(pos.add(j, 0, k)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j, l, k));
        }

        super.decorate(worldIn, rand, pos);
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return (rand.nextInt(6) == 0 ? Biome.BIG_TREE_FEATURE : (rand.nextInt(3) == 0 ? ATGBiomes.Features.JUNGLE_SHRUB : (rand.nextInt(2) == 0 ? new WorldGenTrees(false, 4 + rand.nextInt(5), ATGBiomes.BiomeBlocks.JUNGLE_LOG, ATGBiomes.BiomeBlocks.JUNGLE_LEAF, true) : ATGBiomes.Features.SAVANNA_TREE)));
    }
}
