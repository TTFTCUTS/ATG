package ttftcuts.atg.generator;

import com.google.common.collect.Lists;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Biomes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.datafix.fixes.PaintingDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.storage.WorldInfo;
import ttftcuts.atg.ATG;
import ttftcuts.atg.util.GeneralUtil;

import javax.annotation.Nullable;
import java.util.*;

public class BiomeProviderATG extends BiomeProvider {

    //------ BiomeProvider fields ---------------------------------------------------------
    protected final BiomeCache biomeCache;
    protected final List<Biome> biomesToSpawnIn;

    //------ Biome gen fields ---------------------------------------------------------

    protected World world;

    //------ Constructor ---------------------------------------------------------

    public BiomeProviderATG(World world)
    {
        this.world = world;
        this.biomeCache = new BiomeCache(this);
        this.biomesToSpawnIn = Lists.newArrayList(allowedBiomes);

        // TODO: Set things based on the world info, like biome lists etc
    }

    //------ BiomeProvder functionality ---------------------------------------------------------

    /*@Override
    public Biome getBiomeGenerator(BlockPos pos)
    {
        return this.getBiomeGenerator(pos, (Biome)null);
    }*/

    @Override
    public Biome getBiomeGenerator(BlockPos pos, Biome biomeGenBaseIn)
    {
        return this.biomeCache.getBiome(pos.getX(), pos.getZ(), biomeGenBaseIn);
    }

    @Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height)
    {
        IntCache.resetIntCache();

        if (biomes == null || biomes.length < width * height)
        {
            biomes = new Biome[width * height];
        }

        ChunkProviderATG provider = GeneralUtil.getATGProvider(this.world);

        int ix,iz,bx,bz;
        for (ix = 0; ix < width; ++ix) {
            for (iz = 0; iz < height; ++iz) {
                bx = x + ix;
                bz = z + iz;

                biomes[iz*width + ix] = getBestBiome(bx,bz, provider);
            }
        }

        return biomes;
    }

    /*@Override
    public Biome[] loadBlockGeneratorData(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth)
    {
        return this.getBiomeGenAt(oldBiomeList, x, z, width, depth, true);
    }*/

    @Override
    public Biome[] getBiomeGenAt(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new Biome[width * length];
        }

        if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0)
        {
            Biome[] abiome = this.biomeCache.getCachedBiomes(x, z);
            System.arraycopy(abiome, 0, listToReuse, 0, width * length);
            return listToReuse;
        }
        else
        {
            ChunkProviderATG provider = GeneralUtil.getATGProvider(this.world);

            int ix,iz,bx,bz;
            for (ix = 0; ix < width; ++ix) {
                for (iz = 0; iz < length; ++iz) {
                    bx = x + ix;
                    bz = z + iz;

                    listToReuse[iz*width + ix] = getBestBiome(bx,bz, provider);
                }
            }

            return listToReuse;
        }
    }

    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed)
    {
        IntCache.resetIntCache();
        int xmin = x - (radius >> 2);
        int zmin = z - (radius >> 2);
        int xmax = x + (radius >> 2);
        int zmax = z + (radius >> 2);
        int xdiff = xmax - xmin + 1;
        int zdiff = zmax - zmin + 1;
        Biome[] biomes = this.getBiomeGenAt(null, xmin,zmin,xdiff,zdiff, true);

        try
        {
            for (int index = 0; index < xdiff * zdiff; ++index)
            {
                if (!allowed.contains(biomes[index]))
                {
                    return false;
                }
            }

            return true;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
            crashreportcategory.addCrashSection("x", Integer.valueOf(x));
            crashreportcategory.addCrashSection("z", Integer.valueOf(z));
            crashreportcategory.addCrashSection("radius", Integer.valueOf(radius));
            crashreportcategory.addCrashSection("allowed", allowed);
            throw new ReportedException(crashreport);
        }
    }

    @Override
    @Nullable
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random)
    {
        IntCache.resetIntCache();
        int xmin = x - (range >> 2);
        int zmin = z - (range >> 2);
        int xmax = x + (range >> 2);
        int zmax = z + (range >> 2);
        int xdiff = xmax - xmin + 1;
        int zdiff = zmax - zmin + 1;
        Biome[] genbiomes = this.getBiomeGenAt(null, xmin,zmin,xdiff,zdiff, true);
        BlockPos blockpos = null;
        int k1 = 0;

        for (int index = 0; index < xdiff * zdiff; ++index)
        {
            int i2 = xmin + index % xdiff << 2;
            int j2 = zmin + index / xdiff << 2;
            Biome biome = genbiomes[index];

            if (biomes.contains(biome) && (blockpos == null || random.nextInt(k1 + 1) == 0))
            {
                blockpos = new BlockPos(i2, 0, j2);
                ++k1;
            }
        }

        return blockpos;
    }

    //------ Biome Generation ---------------------------------------------------------

    public Biome getBestBiome(int x, int z, ChunkProviderATG provider) {
        if (provider == null) {
            return Biomes.DEFAULT;
        }

        double weight = Double.MIN_VALUE;
        Biome best = Biomes.DEFAULT;

        Map<Biome,Double> weights = getBiomeWeights(x,z, provider.noise);

        double w;
        for (Biome b : weights.keySet()) {
            w = weights.get(b);
            if (w > weight) {
                best = b;
            }
        }

        return best;
    }

    public Map<Biome, Double> getBiomeWeights(int x, int z, CoreNoise corenoise) {
        Map<Biome, Double> weights = new HashMap<Biome, Double>();

        double height = corenoise.getHeight(x,z);
        double temp = corenoise.getTemperature(x,z);
        double moisture = corenoise.getMoisture(x,z);

        if (height > 192/255.0) {
            weights.put(Biomes.ICE_MOUNTAINS, 1.0);
        } else if (height > 128/255.0) {
            weights.put(Biomes.EXTREME_HILLS, 1.0);
        } else if (height > 66/255.0) {
            weights.put(Biomes.PLAINS, 1.0);
        } else if (height > 60/255.0) {
            weights.put(Biomes.BEACH, 1.0);
        } else if (height > 32/255.0) {
            weights.put(Biomes.OCEAN, 1.0);
        } else {
            weights.put(Biomes.DEEP_OCEAN, 1.0);
        }

        return weights;
    }
}
