package ttftcuts.atg.generator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import ttftcuts.atg.ATG;

import java.util.Arrays;

public class ChunkProviderATG extends ChunkProviderBasic {
    NoiseGeneratorPerlin testnoise;

    public ChunkProviderATG(World world) {
        super(world);

        testnoise = new NoiseGeneratorPerlin(world.rand, 4);
    }

    @Override
    public void fillChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
        this.depthBuffer = testnoise.getRegion(depthBuffer, chunkX*16.0, chunkZ*16.0, 16,16, 0.0625, 0.0625,1.0);

        IBlockState iblockstate = Blocks.STONE.getDefaultState();

        //ATG.logger.info(Arrays.toString(this.depthBuffer));

        for (int ix = 0; ix < 16; ++ix)
        {
            for (int iz = 0; iz < 16; ++iz)
            {
                int height = (int)Math.round(this.depthBuffer[ix+iz*16] * 255);
                for (int iy = 0; iy < height; ++iy)
                {
                    primer.setBlockState(ix, iy, iz, iblockstate);
                }
            }
        }
    }
}
