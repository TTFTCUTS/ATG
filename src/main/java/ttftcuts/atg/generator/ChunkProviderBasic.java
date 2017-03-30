package ttftcuts.atg.generator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class ChunkProviderBasic implements IChunkGenerator {
    protected final World world;
    protected final List<MapGenStructure> structureGenerators = Lists.newArrayList();
    protected final List<MapGenBase> featureGenerators = Lists.newArrayList();
    protected final Map<MapGenStructure, FeatureSpawnListAction> featureSpawnListActions = Maps.newHashMap();
    protected final Random random;

    protected final boolean structuresEnabled;

    protected NoiseGeneratorPerlin surfaceNoise;

    protected double[] depthBuffer = new double[256];
    protected ChunkProviderSettings basicSettings = ChunkProviderSettings.Factory.jsonToFactory("").build();

    public ChunkProviderBasic(World world) {
        this.world = world;
        this.random = new Random(world.getSeed());
        this.structuresEnabled = this.world.getWorldInfo().isMapFeaturesEnabled();

        this.surfaceNoise = new NoiseGeneratorPerlin(this.random, 4);

        this.initFeatures();
    }

    //------------ Structure Gen Utilities ------------//

    interface FeatureSpawnListAction {
        public List<Biome.SpawnListEntry> apply(MapGenStructure structure, EnumCreatureType creaturetype, BlockPos pos);
    }

    public void initFeatures() {
        if (this.structuresEnabled) {
            structureGenerators.add((MapGenVillage) TerrainGen.getModdedMapGen(new MapGenVillage(), InitMapGenEvent.EventType.VILLAGE));

            MapGenScatteredFeature mgsf = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(new MapGenScatteredFeature(), InitMapGenEvent.EventType.SCATTERED_FEATURE);
            structureGenerators.add(mgsf);
            this.featureSpawnListActions.put(mgsf, (s, type, pos) -> {
                MapGenScatteredFeature gen = (MapGenScatteredFeature)s;
                if (type == EnumCreatureType.MONSTER && gen.isSwampHut(pos))
                {
                    return gen.getScatteredFeatureSpawnList();
                }
                return null;
            });

            structureGenerators.add((MapGenMineshaft) TerrainGen.getModdedMapGen(new MapGenMineshaft(), InitMapGenEvent.EventType.MINESHAFT));

            structureGenerators.add((MapGenStronghold) TerrainGen.getModdedMapGen(new MapGenStronghold(), InitMapGenEvent.EventType.STRONGHOLD));

            StructureOceanMonument som = (StructureOceanMonument) TerrainGen.getModdedMapGen(new StructureOceanMonument(), InitMapGenEvent.EventType.OCEAN_MONUMENT);
            structureGenerators.add(som);
            this.featureSpawnListActions.put(som, (s, type, pos) -> {
                StructureOceanMonument gen = (StructureOceanMonument)s;
                if (type == EnumCreatureType.MONSTER && s.isPositionInStructure(this.world, pos))
                {
                    return gen.getScatteredFeatureSpawnList();
                }
                return null;
            });
        }

        featureGenerators.add(TerrainGen.getModdedMapGen(new MapGenCaves(), InitMapGenEvent.EventType.CAVE));
        featureGenerators.add(TerrainGen.getModdedMapGen(new MapGenRavine(), InitMapGenEvent.EventType.RAVINE));
    }

    public void generateFeatures(int x, int z, ChunkPrimer primer) {
        for (MapGenBase mapgenbase : this.featureGenerators) {
            mapgenbase.generate(this.world, x,z, primer);
        }

        for (MapGenStructure mapgenstructure : this.structureGenerators) {
            mapgenstructure.generate(this.world, x,z, primer);
        }
    }

    public boolean populateFeatures(ChunkPos chunkpos) {
        boolean village = false;

        for (MapGenStructure mapgenstructure : this.structureGenerators)
        {
            boolean flag = mapgenstructure.generateStructure(this.world, this.random, chunkpos);

            if (mapgenstructure instanceof MapGenVillage)
            {
                village |= flag;
            }
        }

        return village;
    }

    //------------ Vanilla Methods ------------//

    public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn)
    {
        if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, x, z, primer, this.world)) return;
        double d0 = 0.03125D;
        this.depthBuffer = this.surfaceNoise.getRegion(this.depthBuffer, (double)(x * 16), (double)(z * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);

        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                Biome biome = biomesIn[j + i * 16];
                biome.genTerrainBlocks(this.world, this.random, primer, x * 16 + i, z * 16 + j, this.depthBuffer[j + i * 16]);
            }
        }
    }

    @Override
    public Chunk provideChunk(int x, int z) {
        ChunkPrimer chunkprimer = new ChunkPrimer();

        this.fillChunk(x,z,chunkprimer);

        Biome[] biomes = this.world.getBiomeProvider().getBiomes((Biome[])null, x * 16, z * 16, 16, 16);
        this.replaceBiomeBlocks(x,z, chunkprimer, biomes);

        this.generateFeatures(x,z, chunkprimer);

        Chunk chunk = new Chunk(this.world, chunkprimer, x, z);

        byte[] abyte = chunk.getBiomeArray();

        for (int l = 0; l < abyte.length; ++l)
        {
            abyte[l] = (byte)Biome.getIdForBiome(biomes[l]);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    public void fillChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
        IBlockState iblockstate = Blocks.STONE.getDefaultState();
        for (int iy = 0; iy < 64; ++iy)
        {
            for (int ix = 0; ix < 16; ++ix)
            {
                for (int iz = 0; iz < 16; ++iz)
                {
                    primer.setBlockState(ix, iy, iz, iblockstate);
                }
            }
        }
    }

    @Override
    public void populate(int x, int z) {
        BlockFalling.fallInstantly = true;
        int blockX = x * 16;
        int blockZ = z * 16;
        BlockPos blockpos = new BlockPos(blockX, 0, blockZ);
        Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
        this.random.setSeed(this.world.getSeed());
        long randX = this.random.nextLong() / 2L * 2L + 1L;
        long randZ = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long)x * randX + (long)z * randZ ^ this.world.getSeed());
        boolean hasVillage = false;
        ChunkPos chunkpos = new ChunkPos(x, z);

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.random, x, z, hasVillage);

        hasVillage = this.populateFeatures(chunkpos);

        if (biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS && this.basicSettings.useWaterLakes && !hasVillage && this.random.nextInt(this.basicSettings.waterLakeChance) == 0) {
            if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.random, x, z, hasVillage, PopulateChunkEvent.Populate.EventType.LAKE)) {
                int fx = this.random.nextInt(16) + 8;
                int fy = this.random.nextInt(256);
                int fz = this.random.nextInt(16) + 8;
                (new WorldGenLakes(Blocks.WATER)).generate(this.world, this.random, blockpos.add(fx, fy, fz));
            }
        }

        if (!hasVillage && this.random.nextInt(this.basicSettings.lavaLakeChance / 10) == 0 && this.basicSettings.useLavaLakes) {
            if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.random, x, z, hasVillage, PopulateChunkEvent.Populate.EventType.LAVA)) {
                int fx = this.random.nextInt(16) + 8;
                int fy = this.random.nextInt(this.random.nextInt(248) + 8);
                int fz = this.random.nextInt(16) + 8;

                if (fy < this.world.getSeaLevel() || this.random.nextInt(this.basicSettings.lavaLakeChance / 8) == 0) {
                    (new WorldGenLakes(Blocks.LAVA)).generate(this.world, this.random, blockpos.add(fx, fy, fz));
                }
            }
        }

        if (this.basicSettings.useDungeons) {
            if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.random, x, z, hasVillage, PopulateChunkEvent.Populate.EventType.DUNGEON)) {
                for (int i = 0; i < this.basicSettings.dungeonChance; ++i) {
                    int fx = this.random.nextInt(16) + 8;
                    int fy = this.random.nextInt(256);
                    int fz = this.random.nextInt(16) + 8;
                    (new WorldGenDungeons()).generate(this.world, this.random, blockpos.add(fx, fy, fz));
                }
            }
        }

        biome.decorate(this.world, this.random, new BlockPos(blockX, 0, blockZ));
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.random, x, z, hasVillage, PopulateChunkEvent.Populate.EventType.ANIMALS)) {
            WorldEntitySpawner.performWorldGenSpawning(this.world, biome, blockX + 8, blockZ + 8, 16, 16, this.random);
        }
        blockpos = blockpos.add(8, 0, 8);

        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.random, x, z, hasVillage, PopulateChunkEvent.Populate.EventType.ICE))
        {
            for (int ix = 0; ix < 16; ++ix)
            {
                for (int iz = 0; iz < 16; ++iz)
                {
                    BlockPos blockpos1 = this.world.getPrecipitationHeight(blockpos.add(ix, 0, iz));
                    BlockPos blockpos2 = blockpos1.down();

                    if (this.world.canBlockFreezeWater(blockpos2))
                    {
                        this.world.setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
                    }

                    if (this.canSnowAt(world, blockpos1, true))
                    {
                        this.world.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState(), 2);
                    }
                }
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, this.random, x, z, hasVillage);

        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        Biome biome = this.world.getBiome(pos);

        for (MapGenStructure structure : this.structureGenerators)
        {
            if (this.featureSpawnListActions.containsKey(structure)) {
                List<Biome.SpawnListEntry> list = this.featureSpawnListActions.get(structure).apply(structure, creatureType, pos);
                if (list != null) {
                    return list;
                }
            }
        }

        return biome.getSpawnableList(creatureType);
    }

    @Nullable
    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        if ("Stronghold".equals(structureName))
        {
            for (MapGenStructure mapgenstructure : this.structureGenerators)
            {
                if (mapgenstructure instanceof MapGenStronghold)
                {
                    return mapgenstructure.getClosestStrongholdPos(worldIn, position);
                }
            }
        }

        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        for (MapGenStructure mapgenstructure : this.structureGenerators)
        {
            mapgenstructure.generate(this.world, x, z, (ChunkPrimer)null);
        }
    }

    //------------ Intervention ------------//

    public float getFloatTemperature(Biome biome, BlockPos pos) {
        return biome.getFloatTemperature(pos);
    }

    public boolean canSnowAt(World world, BlockPos pos, boolean checkLight) {
        Biome biome = world.getBiome(pos);
        float f = this.getFloatTemperature(biome, pos);

        if (f > 0.15F)
        {
            return false;
        }
        else if (!checkLight)
        {
            return true;
        }
        else
        {
            if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10)
            {
                IBlockState iblockstate = world.getBlockState(pos);

                if (iblockstate.getBlock().isAir(iblockstate, world, pos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos))
                {
                    return true;
                }
            }

            return false;
        }
    }

}
