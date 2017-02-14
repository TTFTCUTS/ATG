package ttftcuts.atg.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import ttftcuts.atg.ATGBiomes;

import java.util.Random;

public class BiomeShrubland extends Biome {

    public BiomeShrubland() {
        super(new BiomeProperties("Shrubland")
                .setBaseHeight(0.115f)
                .setHeightVariation(0.1f)
                .setTemperature(0.77f)
                .setRainfall(0.53f)
        );

        this.theBiomeDecorator.treesPerChunk = 1;
        this.theBiomeDecorator.grassPerChunk = 7;
        this.theBiomeDecorator.flowersPerChunk = 3;
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return rand.nextInt(3) != 0 ? ATGBiomes.Features.OAK_SHRUB : Biome.TREE_FEATURE;
    }
}
