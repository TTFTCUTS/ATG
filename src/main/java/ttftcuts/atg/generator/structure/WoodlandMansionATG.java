package ttftcuts.atg.generator.structure;

import com.google.common.collect.Lists;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.*;
import ttftcuts.atg.generator.ChunkProviderBasic;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WoodlandMansionATG extends MapGenStructure
{
    private final int featureSpacing = 80;
    private final int minFeatureSeparation = 20;
    public static final List<Biome> ALLOWED_BIOMES = Arrays.<Biome>asList(new Biome[] {Biomes.ROOFED_FOREST, Biomes.MUTATED_ROOFED_FOREST});
    private final ChunkProviderBasic provider;

    public WoodlandMansionATG(ChunkProviderBasic provider)
    {
        this.provider = provider;
    }

    public String getStructureName()
    {
        return "Mansion";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            i = chunkX - 79;
        }

        if (chunkZ < 0)
        {
            j = chunkZ - 79;
        }

        int k = i / 80;
        int l = j / 80;
        Random random = this.world.setRandomSeed(k, l, 10387319);
        k = k * 80;
        l = l * 80;
        k = k + (random.nextInt(60) + random.nextInt(60)) / 2;
        l = l + (random.nextInt(60) + random.nextInt(60)) / 2;

        if (chunkX == k && chunkZ == l)
        {
            boolean flag = this.world.getBiomeProvider().areBiomesViable(chunkX * 16 + 8, chunkZ * 16 + 8, 32, ALLOWED_BIOMES);

            if (flag)
            {
                return true;
            }
        }

        return false;
    }

    public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos, boolean flag)
    {
        this.world = worldIn;
        BiomeProvider biomeprovider = worldIn.getBiomeProvider();
        return biomeprovider.isFixedBiome() && biomeprovider.getFixedBiome() != Biomes.ROOFED_FOREST ? null : findNearestStructurePosBySpacing(worldIn, this, pos, 80, 20, 10387319, true, 100, flag);
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new WoodlandMansionATG.Start(this.world, this.provider, this.rand, chunkX, chunkZ);
    }

    public static class Start extends StructureStart
    {
        private boolean isValid;

        public Start()
        {
        }

        public Start(World world, ChunkProviderBasic provider, Random rand, int chunkX, int chunkZ)
        {
            super(chunkX, chunkZ);
            this.create(world, provider, rand, chunkX, chunkZ);
        }

        private void create(World world, ChunkProviderBasic provider, Random rand, int chunkX, int chunkZ)
        {
            Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
            ChunkPrimer chunkprimer = new ChunkPrimer();
            provider.fillChunk(chunkX, chunkZ, chunkprimer);
            int offsetX = 5;
            int offsetZ = 5;

            if (rotation == Rotation.CLOCKWISE_90)
            {
                offsetX = -5;
            }
            else if (rotation == Rotation.CLOCKWISE_180)
            {
                offsetX = -5;
                offsetZ = -5;
            }
            else if (rotation == Rotation.COUNTERCLOCKWISE_90)
            {
                offsetZ = -5;
            }

            int tl = chunkprimer.findGroundBlockIdx(7, 7);
            int bl = chunkprimer.findGroundBlockIdx(7, 7 + offsetZ);
            int tr = chunkprimer.findGroundBlockIdx(7 + offsetX, 7);
            int br = chunkprimer.findGroundBlockIdx(7 + offsetX, 7 + offsetZ);
            int minheight = Math.min(Math.min(tl, bl), Math.min(tr, br));

            if (minheight < 60)
            {
                this.isValid = false;
            }
            else
            {
                BlockPos blockpos = new BlockPos(chunkX * 16 + 8, minheight + 1, chunkZ * 16 + 8);
                List<WoodlandMansionPieces.MansionTemplate> list = Lists.<WoodlandMansionPieces.MansionTemplate>newLinkedList();
                WoodlandMansionPieces.generateMansion(world.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, list, rand);
                this.components.addAll(list);
                this.updateBoundingBox();
                this.isValid = true;
            }
        }

        /**
         * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
         */
        public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb)
        {
            super.generateStructure(worldIn, rand, structurebb);
            int y = this.boundingBox.minY;

            for (int x = structurebb.minX; x <= structurebb.maxX; ++x)
            {
                for (int z = structurebb.minZ; z <= structurebb.maxZ; ++z)
                {
                    BlockPos blockpos = new BlockPos(x, y, z);

                    if (!worldIn.isAirBlock(blockpos) && this.boundingBox.isVecInside(blockpos))
                    {
                        boolean flag = false;

                        for (StructureComponent structurecomponent : this.components)
                        {
                            if (structurecomponent.getBoundingBox().isVecInside(blockpos))
                            {
                                flag = true;
                                break;
                            }
                        }

                        if (flag)
                        {
                            for (int iy = y - 1; iy > 1; --iy)
                            {
                                BlockPos blockpos1 = new BlockPos(x, iy, z);

                                if (!worldIn.isAirBlock(blockpos1) && !worldIn.getBlockState(blockpos1).getMaterial().isLiquid())
                                {
                                    break;
                                }

                                worldIn.setBlockState(blockpos1, Blocks.COBBLESTONE.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }
        }

        /**
         * currently only defined for Villages, returns true if Village has more than 2 non-road components
         */
        public boolean isSizeableStructure()
        {
            return this.isValid;
        }
    }
}