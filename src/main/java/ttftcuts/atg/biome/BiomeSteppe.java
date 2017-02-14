package ttftcuts.atg.biome;

import net.minecraft.block.BlockDirt;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import ttftcuts.atg.ATGBiomes;

import java.util.Random;

public class BiomeSteppe extends Biome {

    public BiomeSteppe() {
        super(new BiomeProperties("Steppe")
                .setBaseHeight(0.4f)
                .setHeightVariation(0.05f)
                .setTemperature(0.2f)
                .setRainfall(0.1f)
                .setRainDisabled()
        );

        this.theBiomeDecorator.treesPerChunk = 1;
        this.theBiomeDecorator.grassPerChunk = 25;
        this.theBiomeDecorator.flowersPerChunk = 2;
        this.theBiomeDecorator.reedsPerChunk = -999;
        this.theBiomeDecorator.cactiPerChunk = -999;
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return ATGBiomes.Features.STEPPE_SHRUB;
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        int boulders = rand.nextInt(3)-1;

        for (int i = 0; i < boulders; i++) {
            int x = rand.nextInt(16) + 8;
            int z = rand.nextInt(16) + 8;
            BlockPos blockpos = worldIn.getHeight(pos.add(x, 0, z));
            ATGBiomes.Features.BOULDER_COBBLE.generate(worldIn, rand, blockpos);
        }
    }

    @Override
    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
    {
        this.topBlock = Blocks.GRASS.getDefaultState();
        this.fillerBlock = Blocks.DIRT.getDefaultState();

        if (noiseVal > 1.75D)
        {
            this.topBlock = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
        }
        else if (noiseVal > -0.95D)
        {
            this.topBlock = Blocks.GRAVEL.getDefaultState();
        }

        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
    }

    @Override
    public int getGrassColorAtPos(BlockPos pos) {
        return 0xCBC29E; //super.getGrassColorAtPos(pos);
    }
}
