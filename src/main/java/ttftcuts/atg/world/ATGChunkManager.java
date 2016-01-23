package ttftcuts.atg.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ttftcuts.atg.ATG;
import ttftcuts.atg.gen.ATGBiomeManager;
import ttftcuts.atg.gen.layer.ATGGenLayer;
import ttftcuts.atg.gen.layer.ATGGenLayerHeight;
import ttftcuts.atg.gen.layer.ATGGenLayerMoisture;
import ttftcuts.atg.gen.layer.ATGGenLayerTemperature;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class ATGChunkManager extends WorldChunkManager {

	protected ATGBiomeManager biomeManager;
	
	private ATGGenLayer genBiomes;
	private GenLayer biomeIndexLayer;
	private ATGGenLayerHeight rawGen;
	private ATGGenLayerTemperature rawTemp;
	private ATGGenLayerMoisture rawMoisture;
	
	private BiomeCache biomeCache;
	
	protected ATGChunkManager() {
		super();
		this.biomeManager = ATG.biomeManager;
		this.biomeCache = new BiomeCache(this);
	}
	
	public ATGChunkManager(long seed, WorldType worldType)
    {
        this();
        
        GenLayer[] gens = ATGGenLayer.initializeAllBiomeGenerators(seed, worldType);
        
        this.genBiomes = (ATGGenLayer) gens[0];
        this.biomeIndexLayer = gens[1];
        this.rawGen = (ATGGenLayerHeight) gens[2];
        this.rawTemp = (ATGGenLayerTemperature) gens[3];
        this.rawMoisture = (ATGGenLayerMoisture) gens[4];
    }
	
    public ATGChunkManager(World world)
    {
        this(world.getSeed(), world.getWorldInfo().getTerrainType());
    }
    
	public int[] getRawGenInts(int x, int z, int w, int h) {
		return this.rawGen.getInts(x, z, w, h);
	}
	
	public int getRawHeight(int x, int z) {
		return this.rawGen.getInt(x,z);
	}
	
	public double getRawHeightDouble(int x, int z) {
		return this.rawGen.getDouble(x,z);
	}
	
	public double getRawTempDouble(int x, int z) {
		return this.rawTemp.getDouble(x, z);
	}
	
	public double getRawMoistureDouble(int x, int z) {
		return this.rawMoisture.getDouble(x, z);
	}
	
	@Override
    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x, int z, int width, int depth)
    {
        return this.getBiomeGenAt(oldBiomeList, x, z, width, depth, true);
    }
	
	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int height)
    {
        IntCache.resetIntCache();

        if (biomes == null || biomes.length < width * height)
        {
            biomes = new BiomeGenBase[width * height];
        }

        int[] aint = this.genBiomes.getInts(x, z, width, height);

        try
        {
            for (int i = 0; i < width * height; ++i)
            {
                biomes[i] = BiomeGenBase.getBiomeFromBiomeList(aint[i], BiomeGenBase.field_180279_ad);
            }

            return biomes;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
            crashreportcategory.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
            crashreportcategory.addCrashSection("x", Integer.valueOf(x));
            crashreportcategory.addCrashSection("z", Integer.valueOf(z));
            crashreportcategory.addCrashSection("w", Integer.valueOf(width));
            crashreportcategory.addCrashSection("h", Integer.valueOf(height));
            throw new ReportedException(crashreport);
        }
    }
	
	@Override
	public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new float[width * length];
        }

        int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);

        for (int i = 0; i < width * length; ++i)
        {
            try
            {
                float f = (float)BiomeGenBase.getBiomeFromBiomeList(aint[i], BiomeGenBase.field_180279_ad).getIntRainfall() / 65536.0F;

                if (f > 1.0F)
                {
                    f = 1.0F;
                }

                listToReuse[i] = f;
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("DownfallBlock");
                crashreportcategory.addCrashSection("biome id", Integer.valueOf(i));
                crashreportcategory.addCrashSection("downfalls[] size", Integer.valueOf(listToReuse.length));
                crashreportcategory.addCrashSection("x", Integer.valueOf(x));
                crashreportcategory.addCrashSection("z", Integer.valueOf(z));
                crashreportcategory.addCrashSection("w", Integer.valueOf(width));
                crashreportcategory.addCrashSection("h", Integer.valueOf(length));
                throw new ReportedException(crashreport);
            }
        }

        return listToReuse;
    }
	
	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new BiomeGenBase[width * length];
        }

        if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0)
        {
            BiomeGenBase[] abiomegenbase = this.biomeCache.getCachedBiomes(x, z);
            System.arraycopy(abiomegenbase, 0, listToReuse, 0, width * length);
            return listToReuse;
        }
        else
        {
            int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);

            for (int i = 0; i < width * length; ++i)
            {
                listToReuse[i] = BiomeGenBase.getBiomeFromBiomeList(aint[i], BiomeGenBase.field_180279_ad);
            }

            return listToReuse;
        }
    }
	
	@Override
    public boolean areBiomesViable(int p_76940_1_, int p_76940_2_, int p_76940_3_, List<BiomeGenBase> p_76940_4_)
    {
        IntCache.resetIntCache();
        int i = p_76940_1_ - (p_76940_3_ >> 2);
        int j = p_76940_2_ - (p_76940_3_ >> 2);
        int k = p_76940_1_ + (p_76940_3_ >> 2);
        int l = p_76940_2_ + (p_76940_3_ >> 2);
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        int[] aint = this.genBiomes.getInts(i, j, i1, j1);

        try
        {
            for (int k1 = 0; k1 < i1 * j1; ++k1)
            {
                BiomeGenBase biomegenbase = BiomeGenBase.getBiome(aint[k1]);

                if (!p_76940_4_.contains(biomegenbase))
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
            crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
            crashreportcategory.addCrashSection("x", Integer.valueOf(p_76940_1_));
            crashreportcategory.addCrashSection("z", Integer.valueOf(p_76940_2_));
            crashreportcategory.addCrashSection("radius", Integer.valueOf(p_76940_3_));
            crashreportcategory.addCrashSection("allowed", p_76940_4_);
            throw new ReportedException(crashreport);
        }
    }

	@Override
    public BlockPos findBiomePosition(int x, int z, int range, List<BiomeGenBase> biomes, Random random)
    {
        IntCache.resetIntCache();
        int i = x - (range >> 2);
        int j = z - (range >> 2);
        int k = x + (range >> 2);
        int l = z + (range >> 2);
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        int[] aint = this.genBiomes.getInts(i, j, i1, j1);
        BlockPos blockpos = null;
        int k1 = 0;

        for (int l1 = 0; l1 < i1 * j1; ++l1)
        {
            int i2 = i + l1 % i1 << 2;
            int j2 = j + l1 / i1 << 2;
            BiomeGenBase biomegenbase = BiomeGenBase.getBiome(aint[l1]);

            if (biomes.contains(biomegenbase) && (blockpos == null || random.nextInt(k1 + 1) == 0))
            {
                blockpos = new BlockPos(i2, 0, j2);
                ++k1;
            }
        }

        return blockpos;
    }
}
