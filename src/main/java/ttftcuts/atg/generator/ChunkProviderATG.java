package ttftcuts.atg.generator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import ttftcuts.atg.ATG;

import java.util.Arrays;

public class ChunkProviderATG extends ChunkProviderBasic {
    public CoreNoise noise;

    public ChunkProviderATG(World world) {
        super(world);

        noise = new CoreNoise(1);
    }

    // CORRECT THE DAMN TEMPERATURE CURVE
    @Override
    public float getFloatTemperature(Biome biome, BlockPos pos) {
        if (pos.getY() < 64) {
            return super.getFloatTemperature(biome, pos);
        } else {
            return super.getFloatTemperature(biome, pos) + (pos.getY() - 64) * BiomeProviderATG.TEMP_CORRECTION_PER_HEIGHT;
        }
    }

    @Override
    public void fillChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
        IBlockState landblock = Blocks.STONE.getDefaultState();
        IBlockState seablock = Blocks.WATER.getDefaultState();

        int x,z,water,height,limit,ix,iz,iy;

        water = 63;

        for (ix = 0; ix < 16; ++ix)
        {
            for (iz = 0; iz < 16; ++iz)
            {
                x = chunkX*16 + ix;
                z = chunkZ*16 + iz;

                height = (int)Math.floor(noise.getHeight(x,z) * 255);

                limit = Math.max(water, height);

                for (iy = 0; iy < limit; ++iy)
                {
                    if (iy <= height) {
                        primer.setBlockState(ix, iy, iz, landblock);
                    } else {
                        primer.setBlockState(ix, iy, iz, seablock);
                    }
                }
            }
        }
    }
}
