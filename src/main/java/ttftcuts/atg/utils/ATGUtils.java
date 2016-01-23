package ttftcuts.atg.utils;

//import ttftcuts.atg.config.configfiles.ATGBiomeConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class ATGUtils {
	public static double angleDiff(double a1, double a2) {
		
		double diff = a2 - a1;
		diff = (diff + Math.PI) % 360 - Math.PI;
		
		return diff;
	}
	
	public static boolean blockIsGround(Block block) {
		
		if (block == Blocks.stone ||
			block == Blocks.dirt ||
			block == Blocks.grass ||
			block == Blocks.mycelium ||
			block == Blocks.sand ||
			block == Blocks.clay ||
			block == Blocks.snow ||
			block == Blocks.gravel ) {
			return true;
		}
		
		return false;
	}
	
	public static boolean blockIsGroundOrLiquid(Block block) {
		return (blockIsGround(block) || block == Blocks.water || block == Blocks.lava);
	}
	
	/*public static int getTopBlockOrLiquid(World world, int x, int z) {
		int y;
		for (y = 255; y>=0; y--) {
			Block block = world.getBlock(x, y, z);
			
			if ( blockIsGroundOrLiquid(block) ) {
				break;
			}
		}
		
		return y;
	}
	
	public static int getTopBlock(World world, int x, int z) {
		int y;
		for (y = 255; y>=0; y--) {
			Block block = world.getBlock(x, y, z);
			
			if ( blockIsGround(block) ) {
				break;
			}
		}
		
		return y;
	}
	
	public static boolean blockIsNonsolidNotWater(World world, int x, int y, int z) {
		return !world.getBlock(x,y,z).isOpaqueCube() && world.getBlock(x, y, z) != Blocks.water;
	}*/
	
	public static double spreadRange(double n, double fulcrum, double mult, double offset) {
		return Math.max(0, Math.min(1, (n-fulcrum)*mult + fulcrum*mult + offset ));
	}
	
	public static final long xorShift64(long a) {
		a ^= (a << 21);
		a ^= (a >>> 35);
		a ^= (a << 4);
		return a;
	}
	
	public static final long coordRandom(long x, long z, long seed) {
		return ( xorShift64( xorShift64(x) + Long.rotateLeft(xorShift64(z), 32) ) + seed );
	}	
}
