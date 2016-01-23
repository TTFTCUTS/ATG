package ttftcuts.atg;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ttftcuts.atg.gen.ATGBiomeManager;
import ttftcuts.atg.world.ATGWorldType;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="ATG", name="Alternate Terrain Generation", version="2.0.0")
public class ATG {
	public static final Logger logger = LogManager.getLogger("ATG");
	
	/*public static ATGWorldGenRocks rockGen = new ATGWorldGenRocks();
	public static ATGWorldGenCliffs cliffGen = new ATGWorldGenCliffs();
	public static ATGWorldGenHighAltitudeOres highOreGen = new ATGWorldGenHighAltitudeOres();
	public static ATGWorldGenHighAltitudePockets highPocketGen = new ATGWorldGenHighAltitudePockets();*/
	
	public static ATGBiomeManager biomeManager = new ATGBiomeManager();

	
	@Instance("ATG")
	public static ATG instance;
	
	public static ATGWorldType worldType;
	public static String configPath;
	public static String mapPath;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		configPath = event.getModConfigurationDirectory() + "/atg/";
		
		worldType = new ATGWorldType();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
	
	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {
		
	}
}
