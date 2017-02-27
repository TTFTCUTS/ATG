package ttftcuts.atg.generator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import ttftcuts.atg.generator.biome.BiomeRegistry;
import ttftcuts.atg.generator.biome.IBiomeHeightModifier;
import ttftcuts.atg.util.Kernel;
import ttftcuts.atg.util.MathUtil;

import java.util.HashMap;
import java.util.Map;

public class ChunkProviderATG extends ChunkProviderBasic {
    public CoreNoise noise;

    public static final int BLEND_RADIUS = 5;
    public static final Kernel BLEND_KERNEL = new Kernel(BLEND_RADIUS, (int x, int z) -> {
        double dist = Math.sqrt(x*x+z*z);
        if (dist > BLEND_RADIUS) { return 0.0; }
        return MathUtil.smoothstep( dist / BLEND_RADIUS ) * 0.5 + 0.5;
    });

    public ChunkProviderATG(World world) {
        super(world);

        noise = new CoreNoise(world.getSeed());
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

        int x,z,water,heightInt,limit,ix,iz,iy;
        double height;

        water = 63;

        for (ix = 0; ix < 16; ++ix)
        {
            for (iz = 0; iz < 16; ++iz)
            {
                x = chunkX*16 + ix;
                z = chunkZ*16 + iz;

                height = noise.getHeight(x,z);

                height = this.getBiomeNoiseBlend(x,z, height);

                heightInt = (int)Math.floor(height * 255);

                limit = Math.max(water, heightInt);

                for (iy = 0; iy < limit; ++iy)
                {
                    if (iy <= heightInt) {
                        primer.setBlockState(ix, iy, iz, landblock);
                    } else {
                        primer.setBlockState(ix, iy, iz, seablock);
                    }
                }
            }
        }
    }

    public double getBiomeNoiseBlend(int x, int z, double height) {
        if (!(this.world.getBiomeProvider() instanceof BiomeProviderATG)) {
            return height;
        }

        BiomeProviderATG provider = (BiomeProviderATG)this.world.getBiomeProvider();

        int ix,iz;
        Biome biome;
        BiomeRegistry.HeightModEntry heightmod;
        double k;

        double noise = 0.0;
        double modheight;

        Map<Biome, Double> heights = new HashMap<Biome, Double>();

        for (ix = -BLEND_RADIUS; ix <= BLEND_RADIUS; ix++) {
            for (iz = -BLEND_RADIUS; iz <= BLEND_RADIUS; iz++) {
                k = BLEND_KERNEL.getValue(ix,iz);

                if (k > 0.0) {
                    biome = provider.getBiomeFromProvider(x + ix, z + iz, this);

                    if (heights.containsKey(biome)) {
                        noise += heights.get(biome) * k;
                    } else {
                        heightmod = provider.biomeRegistry.getHeightModifier(biome);
                        if (heightmod == null) {
                            modheight = height;
                        } else {
                            modheight = heightmod.modifier.getModifiedHeight(x + this.noise.heightModOffset.x, z + this.noise.heightModOffset.z, height, heightmod.arguments);
                        }
                        noise += modheight * k;
                        heights.put(biome, modheight);
                    }
                }
            }
        }

        return noise;
    }
}
