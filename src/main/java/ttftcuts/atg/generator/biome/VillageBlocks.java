package ttftcuts.atg.generator.biome;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ttftcuts.atg.ATG;
import ttftcuts.atg.ATGBiomes;

public class VillageBlocks {

    @SubscribeEvent
    public void onVillageBlocks(BiomeEvent.GetVillageBlockID event) {
        IBlockState original = event.getOriginal();

        if (event.getBiome() == ATGBiomes.TUNDRA) {
            if (original.getBlock() == Blocks.LOG || original.getBlock() == Blocks.LOG2) {
                event.setResult(Event.Result.DENY);
                event.setReplacement(Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLog.LOG_AXIS, original.getValue(BlockLog.LOG_AXIS)));
            } else if (original.getBlock() == Blocks.PLANKS) {
                event.setResult(Event.Result.DENY);
                event.setReplacement(Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE));
            } else if (original.getBlock() == Blocks.OAK_STAIRS) {
                event.setResult(Event.Result.DENY);
                event.setReplacement(Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, original.getValue(BlockStairs.FACING)));
            } else if (original.getBlock() == Blocks.OAK_FENCE) {
                event.setResult(Event.Result.DENY);
                event.setReplacement(Blocks.SPRUCE_FENCE.getDefaultState());
            }
        }
        else if (event.getBiome() == ATGBiomes.SCRUBLAND) {
            if (original.getBlock() == Blocks.LOG || original.getBlock() == Blocks.LOG2) {
                event.setResult(Event.Result.DENY);
                event.setReplacement(Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(BlockLog.LOG_AXIS, original.getValue(BlockLog.LOG_AXIS)));
            } else if (original.getBlock() == Blocks.PLANKS) {
                event.setResult(Event.Result.DENY);
                event.setReplacement(Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.ACACIA));
            } else if (original.getBlock() == Blocks.OAK_STAIRS) {
                event.setResult(Event.Result.DENY);
                event.setReplacement(Blocks.ACACIA_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, original.getValue(BlockStairs.FACING)));
            } else if (original.getBlock() == Blocks.OAK_FENCE) {
                event.setResult(Event.Result.DENY);
                event.setReplacement(Blocks.ACACIA_FENCE.getDefaultState());
            }
        }
    }
}
