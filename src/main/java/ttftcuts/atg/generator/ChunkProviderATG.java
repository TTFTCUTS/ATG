package ttftcuts.atg.generator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import ttftcuts.atg.ATG;

import java.util.Arrays;

public class ChunkProviderATG extends ChunkProviderBasic {
    CoreNoise noise;

    public ChunkProviderATG(World world) {
        super(world);

        noise = new CoreNoise(1);

        /*ATG.logger.info("collision test ###########################################################");

        test.collisions = 0;

        for (int x=-1024; x<1024; x++) {
            for (int z=-1024; z<1024; z++) {
                for (int cx = 0; cx <16; cx++) {
                    for (int cz = 0; cz <16; cz++) {
                        test.getHeight(x*16 + cx, z*16 + cz);
                    }
                }
            }
        }

        ATG.logger.info("end collision test: "+test.collisions+" collisions ###########################################################");*/
    }

    @Override
    public void fillChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
        //this.depthBuffer = testnoise.getRegion(depthBuffer, chunkX*16.0, chunkZ*16.0, 16,16, 0.0625, 0.0625,1.0);

        IBlockState landblock = Blocks.STONE.getDefaultState();
        IBlockState seablock = Blocks.WATER.getDefaultState();

        //ATG.logger.info(Arrays.toString(this.depthBuffer));
        double scale = 1.0/100.0;

        int x,z,water,height,limit,ix,iz,iy;

        water = 63;

        for (ix = 0; ix < 16; ++ix)
        {
            for (iz = 0; iz < 16; ++iz)
            {
                x = chunkX*16 + ix;
                z = chunkZ*16 + iz;
                //double n = testnoise.getValue(x*scale, z*scale);
                //int height = (int)Math.round(n*20 + 120);//(int)Math.round(this.depthBuffer[ix+iz*16] * 255);

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
