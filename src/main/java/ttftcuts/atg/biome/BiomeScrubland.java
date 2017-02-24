package ttftcuts.atg.biome;

import net.minecraft.block.BlockDirt;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import ttftcuts.atg.ATGBiomes;
import ttftcuts.atg.util.MathUtil;

import java.util.Random;

public class BiomeScrubland extends Biome {

    public BiomeScrubland() {
        super(new BiomeProperties("Scrubland")
                .setBaseHeight(0.125f)
                .setHeightVariation(0.05f)
                .setTemperature(1.60f)
                .setRainfall(0.00f)
                .setRainDisabled()
        );

        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 50;
        this.theBiomeDecorator.reedsPerChunk = 10;
        this.theBiomeDecorator.cactiPerChunk = 4;
        this.theBiomeDecorator.flowersPerChunk = 2;
        this.theBiomeDecorator.grassPerChunk = 4;
        this.spawnableCreatureList.clear();
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityCow.class, 6, 4, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityRabbit.class, 4, 2, 3));
    }

    @Override
    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
    {
        this.topBlock = Blocks.GRASS.getDefaultState();
        this.fillerBlock = Blocks.DIRT.getDefaultState();

        double fuzz = MathUtil.getFuzz(x,z, 342453747);
        if (noiseVal + fuzz * 2 > 1.85) {
            this.topBlock = Blocks.SAND.getDefaultState();
            this.fillerBlock = Blocks.SAND.getDefaultState();
        } else if( noiseVal + fuzz * 2 < -1.85) {
            this.topBlock = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
            this.fillerBlock = Blocks.DIRT.getDefaultState();
        }

        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
    }
}
