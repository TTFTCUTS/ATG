package ttftcuts.atg.generator;

import com.google.common.collect.Lists;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Biomes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.IntCache;
import ttftcuts.atg.generator.biome.BiomeBlobs;
import ttftcuts.atg.generator.biome.BiomeRegistry;
import ttftcuts.atg.generator.biome.BiomeRegistry.BiomeGroup;
import ttftcuts.atg.settings.BiomeSettings;
import ttftcuts.atg.settings.WorldSettings;
import ttftcuts.atg.util.MathUtil;


import javax.annotation.Nullable;
import java.util.*;

public class BiomeProviderATG extends BiomeProvider {
    public CoreNoise noise;

    //------ BiomeProvider fields ---------------------------------------------------------
    protected final BiomeCache biomeCache;
    protected final List<Biome> biomesToSpawnIn;

    // counters the stupid temperature reduction by height mechanic for higher landscapes. (0.05f / 30.0f) is 100% of the baseline reduction.
    public static final float TEMP_CORRECTION_PER_HEIGHT = (0.05f / 30.0f) * 0.66f;

    //------ Biome gen fields ---------------------------------------------------------

    protected World world;
    protected BiomeSettings settings;
    protected Random fuzz;
    public BiomeRegistry biomeRegistry;

    //------ Constructor ---------------------------------------------------------

    public BiomeProviderATG(World world)
    {
        this.world = world;
        this.settings = WorldSettings.loadWorldSettings(world.getWorldInfo().getGeneratorOptions()).biomeSettings;

        this.noise = new CoreNoise(world.getSeed());

        this.fuzz = new Random();
        this.biomeCache = new BiomeCache(this);
        this.biomesToSpawnIn = Lists.newArrayList(allowedBiomes);

        this.biomeRegistry = new BiomeRegistry();
        this.biomeRegistry.populate(this.settings);

        // TODO: Set things based on the world info, like biome lists etc
    }

    //------ BiomeProvder functionality ---------------------------------------------------------

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

        int ix,iz,bx,bz;
        for (ix = 0; ix < width; ++ix) {
            for (iz = 0; iz < height; ++iz) {
                bx = x + ix;
                bz = z + iz;

                biomes[iz*width + ix] = getBestBiome(bx,bz);
            }
        }

        return biomes;
    }

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
            int ix,iz,bx,bz;
            for (ix = 0; ix < width; ++ix) {
                for (iz = 0; iz < length; ++iz) {
                    bx = x + ix;
                    bz = z + iz;

                    listToReuse[iz*width + ix] = getBestBiomeCached(bx,bz);
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

    @Override
    public float getTemperatureAtHeight(float temp, int height)
    {
        if (height < 64) {
            return temp;
        } else {
            return temp + (height - 64) * TEMP_CORRECTION_PER_HEIGHT;
        }
    }

    //------ Biome Generation ---------------------------------------------------------

    public Biome getBestBiomeCached(int x, int z) {

        CoreNoise.NoiseEntry entry = this.noise.getEntry(x,z);
        if (entry.biome != null) {
            return entry.biome;
        }

        Biome biome = this.getBestBiome(x,z);

        entry.biome = biome;

        return biome;
    }

    public Biome getBestBiome(int x, int z) {
        double weight = Double.MIN_VALUE;
        BiomeGroup bestGroup = null;

        Map<BiomeGroup,Double> weights = getBiomeWeights(x,z, this.noise);

        double w;
        for (BiomeGroup b : weights.keySet()) {
            w = weights.get(b);
            if (w > weight) {
                bestGroup = b;
                weight = w;
            }
        }

        if (bestGroup == null) {
            return Biomes.DEFAULT;
        }

        return getSubBiomeForPosition(x,z, bestGroup);
    }

    public Biome getSubBiomeForPosition(int x, int z, BiomeGroup group) {
        BiomeBlobs.BlobEntry blob = this.noise.blobs.getValue(x + group.offsetx, z + group.offsetz, 8 + group.blobSizeModifier, 7 + group.blobSizeModifier + group.subBlobSizeModfier);

        Biome biome = group.getBiome(blob.biome); // get the biome for this blob

        biome = this.biomeRegistry.getHillBiome(biome, this.noise, x,z); // check if it should be changed to a hill
        biome = this.biomeRegistry.getSubBiome(biome, blob.subbiome); // apply sub-biome to it

        return biome;
    }

    public Map<BiomeGroup, Double> getBiomeWeights(int x, int z, CoreNoise corenoise) {
        Map<BiomeGroup, Double> weights = new HashMap<BiomeGroup, Double>();

        double height = corenoise.getHeight(x,z);
        double temp = corenoise.getTemperature(x,z) + MathUtil.getFuzz(x,z,345) * (6/256D);
        temp = MathUtil.spreadRange(temp, 0.4, 1.5, -0.15, -0.05, 1.0); // was max 1 no mult
        double moisture = corenoise.getMoisture(x,z) + MathUtil.getFuzz(x,z,103) * (4/256D);
        moisture = MathUtil.spreadRange(moisture, 0.4, 1.5, 0.07);
        double inland = corenoise.getInland(x,z);
        //temp += Math.max(0, inland-0.5);
        //moisture -= Math.max(0, inland-0.5);

        double swamp = corenoise.getSwamp(x,z);
        double roughness = corenoise.getRoughness(x,z);

        double fertility = this.getFertility(temp, moisture, height);

        BiomeRegistry.EnumBiomeCategory category = BiomeRegistry.EnumBiomeCategory.LAND;

        double heightfuzz = MathUtil.getFuzz(x,z,345) / 256D;

        if (height - (heightfuzz * 0.5) < CoreNoise.COAST_MIN) {
            category = BiomeRegistry.EnumBiomeCategory.OCEAN;
        } else if (height - (heightfuzz * 0.5) < CoreNoise.SWAMP_MAX &&
                ((height >= CoreNoise.BEACH_MAX && swamp > MathUtil.clamp(roughness * 1.5 + 0.25 + heightfuzz * 2.5, 0.0, 0.95))
                || (height < CoreNoise.BEACH_MAX && swamp > 0.0))) {
            category = BiomeRegistry.EnumBiomeCategory.SWAMP;
        } else if (height < CoreNoise.BEACH_MAX) {
            category = BiomeRegistry.EnumBiomeCategory.BEACH;
        }

        Map<String, BiomeGroup> biomeset = this.biomeRegistry.biomeGroups.get(category);

        if (!biomeset.isEmpty()) {
            double bh,bt,bm,bf,dt,dm,df,suitability;

            for (BiomeGroup b : biomeset.values()) {
                if (b == null || b.biomes.isEmpty()) {
                    continue;
                }

                if (height + heightfuzz > b.maxHeight || height + heightfuzz < b.minHeight) {
                    continue;
                }

                bh = b.height;
                bt = b.temperature;
                bm = b.moisture;

                bt = MathUtil.spreadRange(bt, 0.4, 1.3, -0.3, -0.075, 2.0) * 0.667; // was max 1 no mult
                bm = MathUtil.spreadRange(bm, 0.4, 1.2, 0.07);

                bf = getFertility(bt,bm,bh);

                dt = 1-Math.abs(temp - bt);
                dm = 1-Math.abs(moisture - bm);
                df = 1-Math.abs(fertility - bf);

                suitability = df * 0.5 + dt + dm;

                weights.put(b, suitability);
            }
        }

        if(weights.isEmpty()) {
            weights.put(category.fallback, 1.0);
        }

        //ATG.logger.info(weights);

        return weights;
    }

    private double getFertility(double temp, double moisture, double height) {
        return Math.max(0, moisture * 1.15 - ( Math.abs( temp - 0.65 ) ) - ( height - 0.5 ) );
    }
}
