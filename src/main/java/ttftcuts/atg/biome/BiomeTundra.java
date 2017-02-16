package ttftcuts.atg.biome;

import net.minecraft.block.BlockDirt;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import ttftcuts.atg.ATGBiomes;

import java.util.Random;

public class BiomeTundra extends Biome {

    public BiomeTundra() {
        super(new BiomeProperties("Tundra")
                .setBaseHeight(0.325f)
                .setHeightVariation(0.05f)
                .setTemperature(0.2f) // 0.25f
                .setRainfall(0.45f)
        );

        this.theBiomeDecorator.treesPerChunk = 1;
        this.theBiomeDecorator.grassPerChunk = 10;
        this.theBiomeDecorator.flowersPerChunk = 2;
        this.theBiomeDecorator.reedsPerChunk = -999;
        this.theBiomeDecorator.cactiPerChunk = -999;

        this.spawnableCreatureList.clear();
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityRabbit.class, 10, 2, 3));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntitySheep.class, 12, 1, 5));
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return ATGBiomes.Features.TUNDRA_SHRUB;
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

        super.decorate(worldIn, rand, pos);
    }

    @Override
    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
    {
        this.topBlock = Blocks.GRASS.getDefaultState();
        this.fillerBlock = Blocks.DIRT.getDefaultState();

        if (noiseVal > 1.8D)
        {
            this.topBlock = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
        }
        else if (noiseVal <= -1.85D)
        {
            this.topBlock = Blocks.GRAVEL.getDefaultState();
        }

        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
    }

    @Override
    public int getGrassColorAtPos(BlockPos pos) {
        //return 0xba785a;// 0xC49878; //0xCBC29E; //super.getGrassColorAtPos(pos);
        double d0 = GRASS_COLOR_NOISE.getValue((double)pos.getX() * 0.0225D, (double)pos.getZ() * 0.0225D);
        return d0 < -0.1D ? 0xba785a : 0xC49878;
    }

    @Override
    public int getFoliageColorAtPos(BlockPos pos) {
        return 0xb5bf89;
    }
}
